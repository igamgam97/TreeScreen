package org.example.plotapp.feature.hierarchyeditor.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.plotapp.core.viewmodel.BaseViewModel
import org.example.plotapp.core.viewmodel.StateType
import org.example.plotapp.feature.hierarchyeditor.component.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.domain.HierarchyCacheCoordinator

/**
 * ViewModel for the hierarchy editor screen.
 */
class HierarchyViewModel(
    private val hierarchyCacheCoordinator: HierarchyCacheCoordinator,
    private val hierchyEntityMapper: HierchyEntityMapper,
    private val treeCacheMapper: TreeCacheMapper,
) : BaseViewModel<HierarchyScreenState, HierarchyAction, HierarchyEvent>(
    initialUiState = HierarchyScreenState.Default,
) {

    override fun reduce(uiAction: HierarchyAction) {
        when (uiAction) {
            is HierarchyAction.Init -> loadInitialData()
            is HierarchyAction.Apply -> handleApplyBtnClick()
            is HierarchyAction.ResetCache -> resetCache()
            is HierarchyAction.SelectNode -> selectNode(uiAction.node)
            is HierarchyAction.SelectCacheNode -> selectCacheNode(uiAction.node)
            is HierarchyAction.MoveToCache -> moveToCache(uiAction.nodeId)
            is HierarchyAction.AddNode -> addNode(uiAction.value, uiAction.parentId)
            is HierarchyAction.ModifyNode -> modifyNode(
                uiAction.nodeId,
                uiAction.newValue,
            )

            is HierarchyAction.DeleteNode -> deleteNode(uiAction.nodeId)
        }
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    stateType = StateType.loading(),
                )
            }
        }

        hierarchyCacheCoordinator.initializeWithSampleData()

        viewModelScope.launch {
            hierarchyCacheCoordinator.operationFlow.collect { operations ->
                _uiState.update {
                    it.copy(
                        operationsCache = operations.toImmutableList(),
                    )
                }
            }
        }

        viewModelScope.launch {
            hierarchyCacheCoordinator.dbCacheNodesFlow
                .map { hierchyEntityMapper.convert(it.values.toList()) }
                .collect { nodes ->
                    _uiState.update {
                        it.copy(
                            stateType = StateType.data(),
                            databaseTree = nodes.toImmutableList(),
                        )
                    }
                }
        }

        viewModelScope.launch {
            hierarchyCacheCoordinator.cacheNodesFlow
                .map { nodes -> treeCacheMapper.treeToFlatList(nodes) }
                .collect { tree ->
                    _uiState.update {
                        it.copy(
                            cachedTree = tree.toImmutableList(),
                        )
                    }
                }
        }
    }

    private fun handleApplyBtnClick() {
        viewModelScope.launch {
            hierarchyCacheCoordinator.applyAllCommand()
        }
    }

    private fun resetCache() {
        viewModelScope.launch {
            hierarchyCacheCoordinator.resetCache()
        }
    }

    private fun selectNode(node: HierarchyNodeUiModel) {
        _uiState.update {
            it.copy(
                selectedNodeId = node,
                isSelectedNodeInDb = true,
            )
        }
    }

    private fun selectCacheNode(node: HierarchyNodeUiModel) {
        _uiState.update {
            it.copy(
                selectedNodeId = node,
                isSelectedNodeInDb = false,
            )
        }
    }

    private fun moveToCache(nodeId: String) {
        viewModelScope.launch {
            val result = hierarchyCacheCoordinator.moveNode(nodeId)
            result.onFailure {
                _singleEvent.emit(
                    HierarchyEvent.ShowErrorInfo(
                        message = it.message ?: "Unknown error ",
                    ),
                )
            }
        }
    }

    private fun addNode(value: String, parentId: String?) {
        parentId ?: return
        viewModelScope.launch {
            hierarchyCacheCoordinator.addNode(value, parentId)
        }
    }

    private fun modifyNode(nodeId: String, newValue: String) {
        viewModelScope.launch {
            hierarchyCacheCoordinator.modifyNode(
                nodeId = nodeId,
                newValue = newValue,
            )
        }
    }

    private fun deleteNode(nodeId: String) {
        viewModelScope.launch {
            hierarchyCacheCoordinator.deleteNode(nodeId)
        }
    }
}