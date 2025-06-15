package org.example.plotapp.feature.hierarchyeditor.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.example.plotapp.core.viewmodel.collectInLaunchedEffectWithLifecycle
import org.example.plotapp.feature.hierarchyeditor.component.control.AddNodeDialog
import org.example.plotapp.feature.hierarchyeditor.component.control.ConfirmDeleteDialog
import org.example.plotapp.feature.hierarchyeditor.component.control.ControlPanel
import org.example.plotapp.feature.hierarchyeditor.component.control.ModifyNodeDialog
import org.example.plotapp.feature.hierarchyeditor.component.tree.CachedTreeView
import org.example.plotapp.feature.hierarchyeditor.component.tree.DBTreeView
import org.example.plotapp.feature.hierarchyeditor.component.tree.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.component.tree.getNodeColor
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import plotapp.feature.hierarchyeditor.generated.resources.Res
import plotapp.feature.hierarchyeditor.generated.resources.added_node
import plotapp.feature.hierarchyeditor.generated.resources.cache_reset
import plotapp.feature.hierarchyeditor.generated.resources.changes_applied_to_database
import plotapp.feature.hierarchyeditor.generated.resources.deleted_node
import plotapp.feature.hierarchyeditor.generated.resources.hierarchy_database_editor
import plotapp.feature.hierarchyeditor.generated.resources.modified_node
import plotapp.feature.hierarchyeditor.generated.resources.original_node

/**
 * Main screen for the hierarchy editor.
 */

private fun HierarchyViewModel.onSelectNode(node: HierarchyNodeUiModel) {
    dispatch(HierarchyAction.SelectNode(node))
}

private fun HierarchyViewModel.onSelectCacheNode(node: HierarchyNodeUiModel) {
    dispatch(HierarchyAction.SelectCacheNode(node))
}

private fun HierarchyViewModel.onResetBtnClick() {
    dispatch(HierarchyAction.ResetCache)
}

private fun HierarchyViewModel.onApplyBtnClick() {
    dispatch(HierarchyAction.Apply)
}

private fun HierarchyViewModel.onMoveToCache() {
    dispatch(HierarchyAction.MoveToCache)
}

private fun HierarchyViewModel.init() {
    dispatch(HierarchyAction.Init)
}

@Suppress("LongMethod")
@Composable
fun HierarchyEditorRoute(
    viewModel: HierarchyViewModel,
) {
    val uiState by viewModel.uiState.collectAsState()
    var snackbarHostState = remember { SnackbarHostState() }

    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var showModifyDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    viewModel.singleEvent.collectInLaunchedEffectWithLifecycle { event ->
        when (event) {
            is HierarchyEvent.ShowErrorInfo -> {
                snackbarHostState.showSnackbar(event.message)
            }

            is HierarchyEvent.SyncCompleted -> {
                snackbarHostState.showSnackbar(getString(Res.string.changes_applied_to_database))
            }

            is HierarchyEvent.CacheCleared -> {
                snackbarHostState.showSnackbar(getString(Res.string.cache_reset))
            }

            is HierarchyEvent.NodeOperationCompleted -> {
                snackbarHostState.showSnackbar(event.operation)
            }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.init()
    }

    HierarchyEditorScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onSelectedNode = viewModel::onSelectNode,
        onSelectCacheNode = viewModel::onSelectCacheNode,
        onResetBtnClick = viewModel::onResetBtnClick,
        onApplyBtnClick = viewModel::onApplyBtnClick,
        onMoveNodeBtnClick = viewModel::onMoveToCache,
        onShowDeleteDialog = {
            showDeleteDialog = true
        },
        onShowModifyDialog = {
            showModifyDialog = true
        },
        onShowAddDialog = {
            showAddDialog = true
        },
    )

    // Dialogs
    AddNodeDialog(
        isVisible = showAddDialog,
        selectedParentId = uiState.selectedNode?.id,
        onDismiss = { showAddDialog = false },
        onConfirm = { value, parentId ->
            viewModel.dispatch(HierarchyAction.AddNode(value, parentId))
            showAddDialog = false
        },
    )

    ModifyNodeDialog(
        isVisible = showModifyDialog,
        currentNode = uiState.selectedNode,
        onDismiss = { showModifyDialog = false },
        onConfirm = { newValue ->
            uiState.selectedNode?.let { nodeId ->
                viewModel.dispatch(HierarchyAction.ModifyNode(nodeId.id, newValue))
            }
            showModifyDialog = false
        },
    )

    ConfirmDeleteDialog(
        isVisible = showDeleteDialog,
        nodeToDelete = uiState.selectedNode,
        onDismiss = { showDeleteDialog = false },
        onConfirm = {
            uiState.selectedNode?.let { nodeId ->
                viewModel.dispatch(HierarchyAction.DeleteNode(nodeId.id))
            }
            showDeleteDialog = false
        },
    )
}

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HierarchyEditorScreen(
    uiState: HierarchyScreenState,
    snackbarHostState: SnackbarHostState,
    onSelectedNode: (HierarchyNodeUiModel) -> Unit,
    onSelectCacheNode: (HierarchyNodeUiModel) -> Unit,
    onResetBtnClick: () -> Unit,
    onApplyBtnClick: () -> Unit,
    onMoveNodeBtnClick: () -> Unit,
    onShowAddDialog: () -> Unit,
    onShowModifyDialog: () -> Unit,
    onShowDeleteDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.hierarchy_database_editor)) },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Data(
            uiState = uiState,
            onSelectedNode = onSelectedNode,
            onSelectCacheNode = onSelectCacheNode,
            onResetBtnClick = onResetBtnClick,
            onApplyBtnClick = onApplyBtnClick,
            onMoveNodeBtnClick = onMoveNodeBtnClick,
            onShowDeleteDialog = onShowDeleteDialog,
            onShowModifyDialog = onShowModifyDialog,
            onShowAddDialog = onShowAddDialog,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
        )
    }
}

@Composable
private fun Data(
    uiState: HierarchyScreenState,
    onSelectedNode: (HierarchyNodeUiModel) -> Unit,
    onSelectCacheNode: (HierarchyNodeUiModel) -> Unit,
    onResetBtnClick: () -> Unit,
    onApplyBtnClick: () -> Unit,
    onMoveNodeBtnClick: () -> Unit,
    onShowAddDialog: () -> Unit,
    onShowModifyDialog: () -> Unit,
    onShowDeleteDialog: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Control Panel
        ControlPanel(
            controlPanelUiModel = uiState.controlPanelUiModel,
            onAddNode = onShowAddDialog,
            onModifyNode = onShowModifyDialog,
            onDeleteNode = onShowDeleteDialog,
            onMoveNodeBtnClick = onMoveNodeBtnClick,
            onSyncToDatabase = onApplyBtnClick,
            onResetCache = onResetBtnClick,
        )

        ColorNodeListStatusDescription()

        // Tree Views

        Box(
            modifier = Modifier.height(5.dp),
            contentAlignment = Alignment.Center,
        ) {
            if (uiState.isOperationsInProgress) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Database Tree View
            DBTreeView(
                treeViewComponentUiModel = uiState.databaseTree,
                onNodeSelected = onSelectedNode,
                modifier = Modifier.weight(1f),
            )

            // Cached Tree View
            CachedTreeView(
                treeViewComponentUiModel = uiState.cachedTree,
                onNodeSelected = onSelectCacheNode,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ColumnScope.ColorNodeListStatusDescription() {
    ColorNodeStatusDescription(
        stringResource(Res.string.original_node),
        color = getNodeColor(NodeStatus.Unchanged),
    )

    ColorNodeStatusDescription(
        stringResource(Res.string.modified_node),
        color = getNodeColor(NodeStatus.Modified),
    )

    ColorNodeStatusDescription(
        stringResource(Res.string.deleted_node),
        color = getNodeColor(NodeStatus.Deleted),
    )

    ColorNodeStatusDescription(
        stringResource(Res.string.added_node),
        color = getNodeColor(NodeStatus.Added),
    )
}

@Composable
private fun ColorNodeStatusDescription(
    text: String,
    color: Color,
) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
    )
}