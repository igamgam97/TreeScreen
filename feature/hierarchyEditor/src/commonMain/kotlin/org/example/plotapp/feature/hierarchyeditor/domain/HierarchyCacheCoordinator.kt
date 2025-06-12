package org.example.plotapp.feature.hierarchyeditor.domain

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity
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
        get() = operationsSource.operationsFlow

    val dbCacheNodesFlow
        get() = nodeDbSource.nodesFlow

    init {
        // Отслеживаем изменения операций и обновляем дерево инкрементально
        // moveNode обновляет дерево напрямую, поэтому слушаем только операции
        CoroutineScope(ioDispatcher).launch {
            operationsSource.operationsFlow.collect { operations ->
                updateOrderedNodesForOperations(operations)
            }
        }
        CoroutineScope(ioDispatcher).launch {
            nodeDbSource.nodesFlow.collect { operations ->
                actualiseCache(operations)
            }
        }
    }

    private fun actualiseCache(nodes: Map<String, HierarchyNodeEntity>) {
        smartTreeCache.updateNodes(nodes)
    }

    suspend fun moveNode(nodeId: String): Result<Unit> = with(ioDispatcher) {
        val node = nodeDbSource.findNodeById(nodeId)
            ?: throw IllegalArgumentException("Node with id $nodeId not found in database")
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
        val nodesInDeletionQueue = operationsSource.operationsFlow.value
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