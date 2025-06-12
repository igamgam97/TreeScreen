package org.example.plotapp.feature.hierarchyeditor.data.entity.operation

sealed interface NodeOperationEntity {
    val nodeId: String

    data class Add(
        override val nodeId: String,
        val value: String,
        val parentId: String,
    ) : NodeOperationEntity

    data class Delete(
        override val nodeId: String,
    ) : NodeOperationEntity

    data class Modify(
        override val nodeId: String,
        val newValue: String,
    ) : NodeOperationEntity
}