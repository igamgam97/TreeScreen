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
    private val _operationsFlow = MutableStateFlow<List<NodeOperationEntity>>(emptyList())
    override val operationsFlow: StateFlow<List<NodeOperationEntity>> =
        _operationsFlow.asStateFlow()

    override fun addNode(value: String, parentId: String) {
        val newId = generateId(value)
        val operation = NodeOperationEntity.Add(
            nodeId = newId,
            value = value,
            parentId = parentId,
        )

        _operationsFlow.update { operations ->
            operations + operation
        }
    }

    override fun deleteNode(nodeId: String) {
        val operation = NodeOperationEntity.Delete(nodeId)
        _operationsFlow.update { operations ->
            operations + operation
        }
    }

    override fun modifyNode(nodeId: String, newValue: String) {
        val operation = NodeOperationEntity.Modify(nodeId, newValue)
        _operationsFlow.update { operations ->
            operations + operation
        }
    }

    override suspend fun applyAllCommand(): Result<Unit> {
        return runCatching {
            val operations = _operationsFlow.value
            operations.forEach { operation ->
                when (operation) {
                    is NodeOperationEntity.Add -> {
                        hierDbSource.addNode(
                            HierarchyNodeEntity(
                                id = operation.nodeId,
                                value = operation.value,
                                parentId = operation.parentId,
                            ),
                        )
                    }

                    is NodeOperationEntity.Delete -> {
                        hierDbSource.deleteNode(operation.nodeId)
                    }

                    is NodeOperationEntity.Modify -> {
                        hierDbSource.modifyNode(
                            nodeId = operation.nodeId,
                            newValue = operation.newValue,
                        )
                    }
                }
            }

            _operationsFlow.value = emptyList()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun generateId(value: String): String {
        return "temp_${value}_${Clock.System.now().epochSeconds}"
    }

    override fun clearOperations() {
        _operationsFlow.value = emptyList()
    }
}