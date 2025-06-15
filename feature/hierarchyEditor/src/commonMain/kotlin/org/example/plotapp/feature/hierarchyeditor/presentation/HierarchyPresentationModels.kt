package org.example.plotapp.feature.hierarchyeditor.presentation

import org.example.plotapp.feature.hierarchyeditor.component.control.ControlPanelUiModel
import org.example.plotapp.feature.hierarchyeditor.component.tree.DbTreeViewComponentUiModel
import org.example.plotapp.feature.hierarchyeditor.component.tree.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.component.tree.TreeViewComponentUiModel

/**
 * UI state for the hierarchy editor screen.
 */
data class HierarchyScreenState(
    val databaseTree: DbTreeViewComponentUiModel,
    val cachedTree: TreeViewComponentUiModel,
    val selectedNodeId: String?,
    val isSelectedNodeInDb: Boolean,
    val controlPanelUiModel: ControlPanelUiModel,
    val isOperationsInProgress: Boolean,
) {
    companion object {
        val Default = HierarchyScreenState(
            databaseTree = DbTreeViewComponentUiModel.Default,
            cachedTree = TreeViewComponentUiModel.Default,
            selectedNodeId = null,
            isSelectedNodeInDb = true,
            controlPanelUiModel = ControlPanelUiModel.Default,
            isOperationsInProgress = false,
        )
    }

    val selectedNode
        get() = if (isSelectedNodeInDb) {
            databaseTree.nodes.find { it.id == selectedNodeId }
        } else {
            cachedTree.nodes.find { it.id == selectedNodeId }
        }
}

/**
 * Actions that can be dispatched to the view model.
 */
sealed interface HierarchyAction {
    data object Init : HierarchyAction
    data object Apply : HierarchyAction
    data object ResetCache : HierarchyAction

    data class SelectNode(val node: HierarchyNodeUiModel) : HierarchyAction
    data class SelectCacheNode(val node: HierarchyNodeUiModel) : HierarchyAction
    data object MoveToCache : HierarchyAction
    data class AddNode(val value: String, val parentId: String?) : HierarchyAction
    data class ModifyNode(val nodeId: String, val newValue: String) : HierarchyAction
    data class DeleteNode(val nodeId: String) : HierarchyAction
}

/**
 * One-time events emitted by the view model.
 */
sealed interface HierarchyEvent {
    data class ShowErrorInfo(val message: String) : HierarchyEvent
    data object SyncCompleted : HierarchyEvent
    data object CacheCleared : HierarchyEvent
    data class NodeOperationCompleted(val operation: String) : HierarchyEvent
}

fun HierarchyScreenState.updateSelection(
    selectedNode: HierarchyNodeUiModel?,
    isSelectedNodeInDb: Boolean,
): HierarchyScreenState {
    return copy(
        selectedNodeId = selectedNode?.id,
        isSelectedNodeInDb = isSelectedNodeInDb,
        cachedTree = cachedTree.copy(
            selectedNodeId = if (!isSelectedNodeInDb) selectedNode?.id else null,
        ),
        databaseTree = databaseTree.copy(
            selectedNodeId = if (isSelectedNodeInDb) selectedNode?.id else null,
        ),
        controlPanelUiModel = controlPanelUiModel.copy(
            selectedNode = selectedNode,
            selectedNodeInDatabase = isSelectedNodeInDb,
        ),
    )
}

fun HierarchyScreenState.updateOperationLoadingStatus(
    isLoading: Boolean,
): HierarchyScreenState {
    return copy(
        controlPanelUiModel = controlPanelUiModel.copy(
            isLoading = isLoading,
        ),
        isOperationsInProgress = isLoading,
    )
}