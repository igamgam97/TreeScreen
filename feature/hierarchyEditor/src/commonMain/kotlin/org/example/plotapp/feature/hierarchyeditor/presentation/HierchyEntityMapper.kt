package org.example.plotapp.feature.hierarchyeditor.presentation

import org.example.plotapp.feature.hierarchyeditor.component.HierarchyNodeUiModel
import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus

class HierchyEntityMapper {

    fun convert(nodes: List<HierarchyNodeEntity>): List<HierarchyNodeUiModel> {
        val result = mutableListOf<HierarchyNodeUiModel>()

        // Прямые связи в иерархическом порядке с пересчетом depth
        val childrenMap = nodes.groupBy { it.parentId }

        fun addWithChildren(parentId: String?, currentDepth: Int) {
            childrenMap[parentId]?.sortedBy { it.value }?.forEach { node ->
                // Пересчитываем depth относительно корня кэша
                val nodeWithCorrectDepth = node.copy(depth = currentDepth)
                result.add(convert(nodeWithCorrectDepth))
                addWithChildren(node.id, currentDepth + 1)
            }
        }

        addWithChildren(null, 0) // Начинаем с глубины 0

        return result
    }

    private fun convert(hierarchyNodeEntity: HierarchyNodeEntity): HierarchyNodeUiModel {
        return HierarchyNodeUiModel(
            id = hierarchyNodeEntity.id,
            value = hierarchyNodeEntity.value,
            parentId = hierarchyNodeEntity.parentId,
            depth = hierarchyNodeEntity.depth,
            isVirtualConnection = hierarchyNodeEntity.isVirtualConnection,
            status = if (hierarchyNodeEntity.isDeleted) {
                NodeStatus.Deleted
            } else {
                NodeStatus.Unchanged
            },
        )
    }
}