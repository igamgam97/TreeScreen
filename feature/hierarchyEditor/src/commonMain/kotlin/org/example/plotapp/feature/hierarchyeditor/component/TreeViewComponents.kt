package org.example.plotapp.feature.hierarchyeditor.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.collections.immutable.ImmutableList
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus
import org.jetbrains.compose.resources.stringResource
import plotapp.feature.hierarchyeditor.generated.resources.Res
import plotapp.feature.hierarchyeditor.generated.resources.cached_tree
import plotapp.feature.hierarchyeditor.generated.resources.database_tree

@Composable
fun DBTreeView(
    nodes: ImmutableList<HierarchyNodeUiModel>,
    selectedNodeId: String?,
    onNodeSelected: (HierarchyNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    TreeViewContainer(
        title = stringResource(Res.string.database_tree),
        titleColor = Color(0xFF8B4513), // Brown
        modifier = modifier,
    ) {
        TreeNodesList(
            nodes = nodes,
            selectedNodeId = selectedNodeId,
            onNodeSelected = onNodeSelected,
        )
    }
}

@Composable
fun CachedTreeView(
    nodes: ImmutableList<HierarchyNodeUiModel>,
    selectedNodeId: String?,
    onNodeSelected: (HierarchyNodeUiModel) -> Unit,
    modifier: Modifier = Modifier,
) {
    TreeViewContainer(
        title = stringResource(Res.string.cached_tree),
        titleColor = Color(0xFF0066CC), // Blue
        modifier = modifier,
    ) {
        TreeNodesList(
            nodes = nodes,
            selectedNodeId = selectedNodeId,
            onNodeSelected = onNodeSelected,
        )
    }
}

@Composable
private fun TreeViewContainer(
    title: String,
    titleColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(400.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(titleColor.copy(alpha = 0.1f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = titleColor,
                )
            }

            HorizontalDivider()

            // Content
            content()
        }
    }
}

@Composable
private fun TreeNodesList(
    nodes: ImmutableList<HierarchyNodeUiModel>,
    selectedNodeId: String?,
    onNodeSelected: (HierarchyNodeUiModel) -> Unit,
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        items(nodes) { nodes ->
            TreeNodeItem(
                node = nodes,
                isSelected = nodes.id == selectedNodeId,
                onNodeSelected = onNodeSelected,
            )
        }
    }
}

@Composable
private fun TreeNodeItem(
    node: HierarchyNodeUiModel,
    isSelected: Boolean,
    onNodeSelected: (HierarchyNodeUiModel) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onNodeSelected(node) }
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {

        val color = getNodeColor(node.status)
        if (node.isVirtualConnection) {
            Text(
                text = "---",
                color = Color.Gray,
                fontSize = 10.sp,
            )
        } else {
            // Indentation based on depth
            Spacer(modifier = Modifier.width((node.depth * 10).dp))

            // Tree connector lines
            if (node.depth > 0) {
                Text(
                    text = "├── ",
                    color = Color.Gray,
                    fontSize = 12.sp,
                )
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .let { mod ->
                    if (isSelected) {
                        mod.border(
                            2.dp,
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(8.dp),
                        )
                    } else {
                        mod
                    }
                }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {

            Text(
                text = node.value,
                fontWeight = FontWeight.Medium,
                color = color,
                fontSize = 14.sp,
            )
        }
    }
}

fun getNodeColor(status: NodeStatus): Color {
    return when (status) {
        NodeStatus.Unchanged -> Color(color = 0xFF8B4513) // Brown
        NodeStatus.Added -> Color(color = 0xFF4CAF50) // Green
        NodeStatus.Deleted -> Color(color = 0xFF757575) // Gray
        NodeStatus.Modified -> Color(color = 0xFF9C27B0) // Purple
        NodeStatus.NotExists -> Color(color = 0xFF8B4513)
    }
}