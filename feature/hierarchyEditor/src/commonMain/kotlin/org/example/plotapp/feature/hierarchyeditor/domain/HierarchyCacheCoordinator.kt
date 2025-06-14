package org.example.plotapp.feature.hierarchyeditor.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeOperationEntity
import org.example.plotapp.feature.hierarchyeditor.data.source.cache.TreeStateCache
import org.example.plotapp.feature.hierarchyeditor.data.source.database.NodeDbSource
import org.example.plotapp.feature.hierarchyeditor.data.source.operation.PendingOperationsQueue

class HierarchyCacheCoordinator(
    private val nodeDbSource: NodeDbSource,
    private val smartTreeCache: TreeStateCache,
    private val operationsSource: PendingOperationsQueue,
    private val ioDispatcher: CoroutineDispatcher,
) {

    val cacheNodesFlow
        get() = smartTreeCache.nodesFlow

    val operationFlow
        get() = operationsSource.pendingOperationsFlow

    val dbCacheNodesFlow
        get() = nodeDbSource.nodesFlow

    init {
        // Живём все время жизни приложения.
        // Считаем что операции должны применяться даже при переходе на другой экран.
        CoroutineScope(ioDispatcher).launch {
            operationsSource.pendingOperationsFlow.collect { operations ->
                updateOrderedNodesForOperations(operations)
            }
        }
        CoroutineScope(ioDispatcher).launch {
            operationsSource.updatedNodeIdsFlow.collect { operationIds ->
                actualiseCache(operationIds)
            }
        }
    }

    private suspend fun actualiseCache(nodeIds: List<String>) {
        val operations = buildMap {
            nodeIds.forEach { nodeId ->
                nodeDbSource.findNodeById(nodeId)?.let { nodeValue ->
                    put(nodeId, nodeValue)
                }
            }
        }
        smartTreeCache.updateNodes(operations)
    }

    suspend fun moveNode(nodeId: String): Result<Unit> = with(ioDispatcher) {
        val node = nodeDbSource.findNodeById(nodeId)
            ?: return Result.failure(
                IllegalArgumentException("Node with id $nodeId not found in database"),
            )
        if (isAncestorInDeletionQueue(nodeId)) {
            return Result.failure(Exception("Cannot move node: ancestor is in deletion queue"))
        }
        smartTreeCache.moveNode(
            id = node.id,
            value = node.value,
            parentId = node.parentId,
        )
        return Result.success(Unit)
    }

    private fun isAncestorInDeletionQueue(nodeId: String): Boolean {
        val nodesInDeletionQueue = operationsSource.pendingOperationsFlow.value
            .filterIsInstance<NodeOperationEntity.Delete>()
            .map { it.nodeId }
        return nodeDbSource.getNearestParentWithDepth(nodeId, nodesInDeletionQueue.toSet()) != null
    }

    suspend fun resetCache() {
        operationsSource.clearOperations()
        nodeDbSource.resetCache()
        smartTreeCache.clear()
    }

    /**
     * Обновление дерева только для операций (Add/Delete/Modify).
     */
    private fun updateOrderedNodesForOperations(operations: List<NodeOperationEntity>) {
        val operation = operations.lastOrNull() ?: return

        when (operation) {
            is NodeOperationEntity.Add -> {
                smartTreeCache.addNode(
                    id = operation.nodeId,
                    value = operation.value,
                    parentId = operation.parentId,
                )
            }

            is NodeOperationEntity.Delete -> {
                smartTreeCache.removeNode(operation.nodeId)
            }

            is NodeOperationEntity.Modify -> {
                smartTreeCache.renameNode(operation.nodeId, operation.newValue)
            }
        }
    }

    fun addNode(value: String, parentId: String) {
        operationsSource.addNode(value, parentId)
    }

    fun deleteNode(nodeId: String) {
        operationsSource.deleteNode(nodeId)
    }

    fun modifyNode(nodeId: String, newValue: String) {
        operationsSource.modifyNode(nodeId, newValue)
    }

    suspend fun applyAllCommand(): Result<Unit> {
        return operationsSource.applyAllCommand()
    }

    fun initializeWithSampleData() {
        nodeDbSource.initializeWithSampleData()
    }
}