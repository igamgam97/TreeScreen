package org.example.plotapp.feature.hierarchyeditor.component.tree

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus

data class HierarchyNodeUiModel(
    val id: String,
    val value: String,
    val parentId: String? = null,
    val depth: Int = 1,
    val isVirtualConnection: Boolean = false,
    val status: NodeStatus = NodeStatus.Unchanged,
)

data class TreeViewComponentUiModel(
    val nodes: ImmutableList<HierarchyNodeUiModel>,
    val selectedNodeId: String?,
) {
    companion object {
        val Default = TreeViewComponentUiModel(
            nodes = persistentListOf(),
            selectedNodeId = null,
        )
    }
}

data class DbTreeViewComponentUiModel(
    val isLoading: Boolean,
    val nodes: ImmutableList<HierarchyNodeUiModel>,
    val selectedNodeId: String?,
) {
    companion object {
        val Default = DbTreeViewComponentUiModel(
            isLoading = true,
            nodes = persistentListOf(),
            selectedNodeId = null,
        )
    }
}