package org.example.plotapp.feature.hierarchyeditor.presentation

import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import org.example.plotapp.core.viewmodel.StateType
import org.example.plotapp.feature.hierarchyeditor.component.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeOperationEntity

/**
 * UI state for the hierarchy editor screen.
 */
data class HierarchyScreenState(
    val stateType: StateType<Unit, Unit, String>,
    val databaseTree: ImmutableList<HierarchyNodeUiModel>,
    val cachedTree: ImmutableList<HierarchyNodeUiModel>,
    val operationsCache: ImmutableList<NodeOperationEntity>,
    val selectedNodeId: HierarchyNodeUiModel? = null,
    val isSelectedNodeInDb: Boolean,
) {
    companion object {
        val Default = HierarchyScreenState(
            stateType = StateType.data(),
            databaseTree = persistentListOf(),
            cachedTree = persistentListOf(),
            operationsCache = persistentListOf(),
            selectedNodeId = null,
            isSelectedNodeInDb = true,
        )
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
    data class MoveToCache(val nodeId: String) : HierarchyAction
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