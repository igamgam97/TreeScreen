package org.example.plotapp.feature.hierarchyeditor.presentation

import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.example.plotapp.core.viewmodel.BaseViewModel
import org.example.plotapp.feature.hierarchyeditor.component.tree.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.domain.HierarchyCacheCoordinator

/**
 * ViewModel for the hierarchy editor screen.
 */
class HierarchyViewModel(
    private val hierarchyCacheCoordinator: HierarchyCacheCoordinator,
    private val treeDbMapper: TreeDbMapper,
    private val treeCacheMapper: TreeCacheMapper,
) : BaseViewModel<HierarchyScreenState, HierarchyAction, HierarchyEvent>(
    initialUiState = HierarchyScreenState.Default,
) {

    override fun reduce(uiAction: HierarchyAction) {
        when (uiAction) {
            is HierarchyAction.Init -> loadInitialData()
            is HierarchyAction.Apply -> handleApplyBtnClick()
            is HierarchyAction.ResetCache -> handleResetCache()
            is HierarchyAction.SelectNode -> handleSelectNode(uiAction.node)
            is HierarchyAction.SelectCacheNode -> handleSelectCacheNode(uiAction.node)
            is HierarchyAction.MoveToCache -> handleMoveToCache()
            is HierarchyAction.AddNode -> handleAddNode(uiAction.value, uiAction.parentId)
            is HierarchyAction.ModifyNode -> handleModifyNode(
                uiAction.nodeId,
                uiAction.newValue,
            )

            is HierarchyAction.DeleteNode -> handleDeleteNode(uiAction.nodeId)
        }
    }

    private fun loadInitialData() {

        hierarchyCacheCoordinator.initializeWithSampleData()

        viewModelScope.launch {
            hierarchyCacheCoordinator.operationFlow.collect { operations ->
                _uiState.update {
                    it.copy(
                        controlPanelUiModel = it.controlPanelUiModel.copy(
                            hasOperations = operations.isNotEmpty(),
                        ),
                    )
                }
            }
        }

        viewModelScope.launch {
            hierarchyCacheCoordinator.dbCacheNodesFlow
                .map { treeDbMapper.convert(it.values.toList()) }
                .collect { nodes ->
                    _uiState.update {
                        it.copy(
                            databaseTree = it.databaseTree.copy(
                                isLoading = false,
                                nodes = nodes.toImmutableList(),
                            ),
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
                            cachedTree = it.cachedTree.copy(
                                nodes = tree.toImmutableList(),
                            ),
                        )
                    }
                }
        }
    }

    private fun handleApplyBtnClick() {
        viewModelScope.launch {
            _uiState.update {
                it.updateOperationLoadingStatus(
                    isLoading = true,
                )
            }
            val result = hierarchyCacheCoordinator.applyAllCommand()
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.updateOperationLoadingStatus(
                            isLoading = false,
                        )
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.updateOperationLoadingStatus(
                            isLoading = false,
                        )
                    }
                    _singleEvent.emit(HierarchyEvent.ShowErrorInfo("Something wrong"))
                },
            )
        }
    }

    private fun handleResetCache() {
        viewModelScope.launch {
            hierarchyCacheCoordinator.resetCache()
        }
    }

    private fun handleSelectNode(node: HierarchyNodeUiModel) {
        _uiState.update {
            it.updateSelection(
                selectedNode = node,
                isSelectedNodeInDb = true,
            )
        }
    }

    private fun handleSelectCacheNode(node: HierarchyNodeUiModel) {
        _uiState.update {
            it.updateSelection(
                selectedNode = node,
                isSelectedNodeInDb = false,
            )
        }
    }

    private fun handleMoveToCache() {
        val selectedNode = _uiState.value.selectedNode ?: return
        viewModelScope.launch {
            _uiState.update {
                it.updateOperationLoadingStatus(
                    isLoading = true,
                )
            }
            val result = hierarchyCacheCoordinator.moveNode(selectedNode.id)
            result.fold(
                onSuccess = {
                    _uiState.update {
                        it.updateOperationLoadingStatus(
                            isLoading = false,
                        )
                    }
                },
                onFailure = {
                    _uiState.update {
                        it.updateOperationLoadingStatus(
                            isLoading = false,
                        )
                    }
                    result.onFailure {
                        _singleEvent.emit(
                            HierarchyEvent.ShowErrorInfo(
                                message = it.message ?: "Unknown error ",
                            ),
                        )
                    }
                },
            )
        }
    }

    private fun handleAddNode(value: String, parentId: String?) {
        parentId ?: return
        viewModelScope.launch {
            hierarchyCacheCoordinator.addNode(value, parentId)
        }
    }

    private fun handleModifyNode(nodeId: String, newValue: String) {
        viewModelScope.launch {
            hierarchyCacheCoordinator.modifyNode(
                nodeId = nodeId,
                newValue = newValue,
            )
        }
    }

    private fun handleDeleteNode(nodeId: String) {
        viewModelScope.launch {
            hierarchyCacheCoordinator.deleteNode(nodeId)
        }
    }
}