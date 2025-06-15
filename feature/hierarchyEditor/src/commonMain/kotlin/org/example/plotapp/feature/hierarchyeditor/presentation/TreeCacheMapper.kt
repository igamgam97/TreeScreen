package org.example.plotapp.feature.hierarchyeditor.presentation

import org.example.plotapp.feature.hierarchyeditor.component.tree.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.data.source.cache.NewTreeNode

class TreeCacheMapper {

    /**
     * Converts a map of tree nodes to a flat list of UI models.
     */
    fun treeToFlatList(nodes: Map<String, NewTreeNode>): List<HierarchyNodeUiModel> {
        val result = mutableListOf<HierarchyNodeUiModel>()
        val visited = mutableSetOf<String>()

        // Find root nodes
        val rootNodes = nodes.values.filter { node ->
            node.parentId == null || !nodes.containsKey(node.parentId)
        }

        // Sort root nodes by value for consistent ordering
        rootNodes.sortedBy { it.value }.forEach { root ->
            flattenNode(root, 0, nodes, result, visited)
        }

        return result
    }

    private fun flattenNode(
        node: NewTreeNode,
        depth: Int,
        nodesMap: Map<String, NewTreeNode>,
        result: MutableList<HierarchyNodeUiModel>,
        visited: MutableSet<String>,
    ) {
        if (!visited.add(node.id)) return // Prevent cycles

        result.add(
            HierarchyNodeUiModel(
                id = node.id,
                value = node.value,
                parentId = node.parentId,
                depth = depth,
                status = node.status,
            ),
        )

        // Get children and process them
        val children = nodesMap.values.filter { it.parentId == node.id }
        children.sortedBy { it.value }.forEach { child ->
            flattenNode(child, depth + 1, nodesMap, result, visited)
        }
    }
}