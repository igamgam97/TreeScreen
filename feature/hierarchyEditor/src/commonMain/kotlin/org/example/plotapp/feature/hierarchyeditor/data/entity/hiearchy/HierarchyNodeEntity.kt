package org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy

data class HierarchyNodeEntity(
    val id: String,
    val value: String,
    val parentId: String? = null,
    val depth: Int = 1,
    val isVirtualConnection: Boolean = false,
    val isDeleted: Boolean = false,
)