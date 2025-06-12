package org.example.plotapp.feature.hierarchyeditor.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus
import org.jetbrains.compose.resources.stringResource
import plotapp.feature.hierarchyeditor.generated.resources.Res
import plotapp.feature.hierarchyeditor.generated.resources.add
import plotapp.feature.hierarchyeditor.generated.resources.add_new_node
import plotapp.feature.hierarchyeditor.generated.resources.cancel
import plotapp.feature.hierarchyeditor.generated.resources.delete
import plotapp.feature.hierarchyeditor.generated.resources.delete_confirmation
import plotapp.feature.hierarchyeditor.generated.resources.delete_node
import plotapp.feature.hierarchyeditor.generated.resources.modify_node
import plotapp.feature.hierarchyeditor.generated.resources.node_value
import plotapp.feature.hierarchyeditor.generated.resources.update

@Composable
fun ControlPanel(
    hasOperations: Boolean,
    selectedNode: HierarchyNodeUiModel?,
    selectedNodeInDatabase: Boolean,
    onAddNode: () -> Unit,
    onModifyNode: () -> Unit,
    onDeleteNode: () -> Unit,
    onMoveToCache: () -> Unit,
    onSyncToDatabase: () -> Unit,
    onResetCache: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        val isCacheSelected =
            selectedNode != null && !selectedNodeInDatabase && selectedNode.status != NodeStatus.Deleted
        val isDbSelected = selectedNodeInDatabase && selectedNode?.status != NodeStatus.Deleted

        // Node operations
        CompactButton(
            icon = Icons.Default.Add,
            onClick = onAddNode,
            enabled = isCacheSelected,
        )

        CompactButton(
            icon = Icons.Default.Edit,
            onClick = onModifyNode,
            enabled = isCacheSelected,
        )

        CompactButton(
            icon = Icons.Default.Delete,
            onClick = onDeleteNode,
            enabled = isCacheSelected,
        )

        CompactButton(
            icon = Icons.AutoMirrored.Filled.ArrowForward,
            onClick = onMoveToCache,
            enabled = isDbSelected,
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Sync operations
        CompactButton(
            icon = Icons.Default.Check,
            onClick = onSyncToDatabase,
            enabled = hasOperations,
        )

        CompactButton(
            icon = Icons.Default.Refresh,
            onClick = onResetCache,
        )
    }
}

@Composable
private fun CompactButton(
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.size(40.dp),
        contentPadding = PaddingValues(0.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNodeDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    selectedParentId: String?,
    onConfirm: (value: String, parentId: String?) -> Unit,
) {
    if (!isVisible) return

    var nodeValue by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(Res.string.add_new_node))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = nodeValue,
                    onValueChange = { nodeValue = it },
                    label = { Text(stringResource(Res.string.node_value)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nodeValue.isNotBlank()) {
                        onConfirm(nodeValue, selectedParentId)
                        nodeValue = ""
                    }
                },
                enabled = nodeValue.isNotBlank(),
            ) {
                Text(stringResource(Res.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyNodeDialog(
    isVisible: Boolean,
    currentNode: HierarchyNodeUiModel?,
    onDismiss: () -> Unit,
    onConfirm: (newValue: String) -> Unit,
) {
    if (!isVisible || currentNode == null) return

    var nodeValue by remember(currentNode) { mutableStateOf(currentNode.value) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(Res.string.modify_node))
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                OutlinedTextField(
                    value = nodeValue,
                    onValueChange = { nodeValue = it },
                    label = { Text(stringResource(Res.string.node_value)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (nodeValue.isNotBlank()) {
                        onConfirm(nodeValue)
                    }
                },
                enabled = nodeValue.isNotBlank(),
            ) {
                Text(stringResource(Res.string.update))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}

@Composable
fun ConfirmDeleteDialog(
    isVisible: Boolean,
    nodeToDelete: HierarchyNodeUiModel?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    if (!isVisible || nodeToDelete == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(stringResource(Res.string.delete_node))
        },
        text = {
            Text(stringResource(Res.string.delete_confirmation, nodeToDelete.value))
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
            ) {
                Text(stringResource(Res.string.delete))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        },
    )
}