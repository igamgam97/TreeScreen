package org.example.plotapp.feature.hierarchyeditor.data.source.cache

import kotlinx.coroutines.flow.StateFlow
import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity

interface TreeStateCache {

    val nodesFlow: StateFlow<Map<String, NewTreeNode>>

    fun addNode(id: String, value: String, parentId: String? = null): NewTreeNode

    fun moveNode(id: String, value: String, parentId: String? = null): NewTreeNode

    fun removeNode(id: String): Boolean

    fun renameNode(id: String, newValue: String): Boolean

    fun getRootNodes(): List<NewTreeNode>

    fun getChildren(nodeId: String): List<NewTreeNode>

    fun getAllNodes(): List<NewTreeNode>

    fun getNode(id: String): NewTreeNode?

    fun hasNode(id: String): Boolean

    fun size(): Int

    fun isEmpty(): Boolean

    fun clear()

    fun updateNodes(dbNodes: Map<String, HierarchyNodeEntity>)
}