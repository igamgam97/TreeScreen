package org.example.plotapp.feature.hierarchyeditor.component

import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus

data class HierarchyNodeUiModel(
    val id: String,
    val value: String,
    val parentId: String? = null,
    val depth: Int = 1,
    val isVirtualConnection: Boolean = false,
    val status: NodeStatus = NodeStatus.Unchanged,
)