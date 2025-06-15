package org.example.plotapp.feature.hierarchyeditor.data.source.database

import kotlinx.coroutines.flow.StateFlow
import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity

interface NodeDbSource {
    /**
     * Flow of all hierarchy nodes mapped by their IDs.
     */
    val nodesFlow: StateFlow<Map<String, HierarchyNodeEntity>>

    /**
     * Adds a new node to the repository.
     */
    suspend fun addNode(node: HierarchyNodeEntity): String

    /**
     * Finds a node by its ID.
     */
    suspend fun findNodeById(nodeId: String): HierarchyNodeEntity?

    /**
     * Deletes a node and all its descendants from the repository.
     */
    suspend fun deleteNode(nodeId: String): String

    /**
     * Modifies an existing node in the repository.
     */
    suspend fun modifyNode(nodeId: String, newValue: String): String

    /**
     * Resets the database cache.
     */
    suspend fun resetCache()

    /**
     * Initializes the database with sample data.
     */
    fun initializeWithSampleData()

    /**
     * Retrieves the nearest parent node with its depth for a given node ID.
     * Returns null if no parent is found or if the node itself is the root.
     */
    fun getNearestParentWithDepth(nodeId: String, cache: Set<String>): NodeWithDepth?
}