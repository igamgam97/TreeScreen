package org.example.plotapp.feature.hierarchyeditor.data.source.operation

import kotlinx.coroutines.flow.StateFlow
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeOperationEntity

interface PendingOperationsQueue {
    /**
     * Flow of pending operations.
     */
    val pendingOperationsFlow: StateFlow<List<NodeOperationEntity>>

    /**
     * Flow of ids nodes for updating.
     */
    val updatedNodeIdsFlow: StateFlow<List<String>>

    /**
     * Adds a new node operation to the queue.
     */
    fun addNode(value: String, parentId: String)

    /**
     * Adds a delete node operation to the queue.
     */
    fun deleteNode(nodeId: String)

    /**
     * Adds a modify node operation to the queue.
     */
    fun modifyNode(nodeId: String, newValue: String)

    /**
     * Applies all pending operations to the repository and clears the queue.
     */
    suspend fun applyAllCommand(): Result<Unit>

    /**
     * Clears all pending operations without applying them.
     */
    fun clearOperations()
}