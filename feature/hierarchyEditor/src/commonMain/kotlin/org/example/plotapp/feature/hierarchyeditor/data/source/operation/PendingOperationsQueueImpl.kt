package org.example.plotapp.feature.hierarchyeditor.data.source.operation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeOperationEntity
import org.example.plotapp.feature.hierarchyeditor.data.source.database.NodeDbSource
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class PendingOperationsQueueImpl(
    private val hierDbSource: NodeDbSource,
) : PendingOperationsQueue {
    private val _pendingOperationsFlow = MutableStateFlow<List<NodeOperationEntity>>(emptyList())
    override val pendingOperationsFlow: StateFlow<List<NodeOperationEntity>> =
        _pendingOperationsFlow.asStateFlow()
    private val _updateNodeIdsFlow = MutableStateFlow<List<String>>(emptyList())
    override val updatedNodeIdsFlow: StateFlow<List<String>> =
        _updateNodeIdsFlow.asStateFlow()

    override fun addNode(value: String, parentId: String) {
        val newId = generateId(value)
        val operation = NodeOperationEntity.Add(
            nodeId = newId,
            value = value,
            parentId = parentId,
        )

        _pendingOperationsFlow.update { operations ->
            operations + operation
        }
    }

    override fun deleteNode(nodeId: String) {
        val operation = NodeOperationEntity.Delete(nodeId)
        _pendingOperationsFlow.update { operations ->
            operations + operation
        }
    }

    override fun modifyNode(nodeId: String, newValue: String) {
        val operation = NodeOperationEntity.Modify(nodeId, newValue)
        _pendingOperationsFlow.update { operations ->
            operations + operation
        }
    }

    override suspend fun applyAllCommand(): Result<Unit> {
        return runCatching {
            val operations = _pendingOperationsFlow.value
            val nodeIds = mutableListOf<String>()
            operations.forEach { operation ->
                when (operation) {
                    is NodeOperationEntity.Add -> {
                        val id = hierDbSource.addNode(
                            HierarchyNodeEntity(
                                id = operation.nodeId,
                                value = operation.value,
                                parentId = operation.parentId,
                            ),
                        )
                        nodeIds.add(id)
                    }

                    is NodeOperationEntity.Delete -> {
                        val id = hierDbSource.deleteNode(operation.nodeId)
                        nodeIds.add(id)
                    }

                    is NodeOperationEntity.Modify -> {
                        val id = hierDbSource.modifyNode(
                            nodeId = operation.nodeId,
                            newValue = operation.newValue,
                        )
                        nodeIds.add(id)
                    }
                }
            }

            _pendingOperationsFlow.value = emptyList()
            _updateNodeIdsFlow.value = nodeIds
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun generateId(value: String): String {
        return "temp_${value}_${Clock.System.now().epochSeconds}"
    }

    override fun clearOperations() {
        _pendingOperationsFlow.value = emptyList()
    }
}