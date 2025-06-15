package org.example.plotapp.feature.hierarchyeditor.component.control

import org.example.plotapp.feature.hierarchyeditor.component.tree.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus

data class ControlPanelUiModel(
    val hasOperations: Boolean,
    val selectedNode: HierarchyNodeUiModel?,
    val selectedNodeInDatabase: Boolean,
    val isLoading: Boolean,
) {

    companion object {
        val Default = ControlPanelUiModel(
            hasOperations = false,
            selectedNode = null,
            selectedNodeInDatabase = true,
            isLoading = false,
        )
    }

    val isCacheSelected
        get() =
            selectedNode != null &&
                !selectedNodeInDatabase &&
                selectedNode.status != NodeStatus.Deleted
    val isDbSelected
        get() =
            selectedNodeInDatabase && selectedNode?.status != NodeStatus.Deleted
}