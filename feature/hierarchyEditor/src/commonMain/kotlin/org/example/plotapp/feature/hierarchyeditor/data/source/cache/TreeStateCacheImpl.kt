package org.example.plotapp.feature.hierarchyeditor.data.source.cache

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus

data class NewTreeNode(
    val id: String,
    val value: String,
    val parentId: String? = null,
    val status: NodeStatus = NodeStatus.Unchanged,
)

class TreeStateCacheImpl : TreeStateCache {
    private val nodeMap = mutableMapOf<String, NewTreeNode>()

    // Observable state for tree nodes
    private val _nodesFlow = MutableStateFlow<Map<String, NewTreeNode>>(emptyMap())
    override val nodesFlow: StateFlow<Map<String, NewTreeNode>> = _nodesFlow.asStateFlow()

    private fun notifyTreeChanged() {
        _nodesFlow.value = nodeMap.toMap()
    }

    override fun addNode(id: String, value: String, parentId: String?): NewTreeNode {
        return upsertNode(id, value, parentId, NodeStatus.Added)
    }

    override fun moveNode(id: String, value: String, parentId: String?): NewTreeNode {
        return upsertNode(id, value, parentId, NodeStatus.Unchanged)
    }

    private fun upsertNode(id: String, value: String, parentId: String?, status: NodeStatus): NewTreeNode {
        val existingNode = nodeMap[id]
        if (existingNode != null) {
            return existingNode
        }

        val newNode = NewTreeNode(id, value, parentId, status)
        nodeMap[id] = newNode
        notifyTreeChanged()
        return newNode
    }

    override fun removeNode(id: String): Boolean {
        val node = nodeMap[id] ?: return false

        markNodeAndDescendantsAsDeleted(node)
        notifyTreeChanged()

        return true
    }

    private fun markNodeAndDescendantsAsDeleted(node: NewTreeNode) {
        val deletedNode = node.copy(status = NodeStatus.Deleted)
        nodeMap[node.id] = deletedNode

        // Рекурсивно помечаем всех потомков
        getDirectChildren(node.id).forEach { child ->
            markNodeAndDescendantsAsDeleted(child)
        }
    }

    override fun renameNode(id: String, newValue: String): Boolean {
        val node = nodeMap[id] ?: return false

        val renamedNode = node.copy(
            value = newValue,
            status = if (node.status == NodeStatus.Unchanged) NodeStatus.Modified else node.status,
        )

        nodeMap[id] = renamedNode
        notifyTreeChanged()

        return true
    }

    override fun getRootNodes(): List<NewTreeNode> {
        return nodeMap.values.filter { node ->
            node.parentId == null || !nodeMap.containsKey(node.parentId)
        }
    }

    override fun getChildren(nodeId: String): List<NewTreeNode> {
        return getDirectChildren(nodeId)
    }

    override fun getAllNodes(): List<NewTreeNode> = nodeMap.values.toList()

    override fun getNode(id: String): NewTreeNode? = nodeMap[id]

    override fun hasNode(id: String): Boolean = nodeMap.containsKey(id)

    override fun size(): Int = nodeMap.size

    override fun isEmpty(): Boolean = nodeMap.isEmpty()

    override fun clear() {
        nodeMap.clear()
        notifyTreeChanged()
    }

    override fun updateNodes(dbNodes: Map<String, HierarchyNodeEntity>) {
        var hasChanges = false

        nodeMap.forEach { (nodeId, cacheNode) ->
            val newStatus = dbNodes[nodeId]?.let { dbNode ->
                when {
                    dbNode.isDeleted -> NodeStatus.Deleted
                    cacheNode.status == NodeStatus.Added -> NodeStatus.Unchanged
                    cacheNode.status == NodeStatus.Modified -> NodeStatus.Unchanged
                    cacheNode.status == NodeStatus.Deleted -> NodeStatus.Unchanged
                    else -> cacheNode.status
                }
            } ?: cacheNode.status

            if (newStatus != cacheNode.status) {
                nodeMap[nodeId] = cacheNode.copy(status = newStatus)
                hasChanges = true
            }
        }

        if (hasChanges) {
            notifyTreeChanged()
        }
    }

    private fun getDirectChildren(nodeId: String): List<NewTreeNode> {
        return nodeMap.values.filter { it.parentId == nodeId }
    }
}