package org.example.plotapp.feature.hierarchyeditor.data.source.database

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity

class NodeDbSourceImpl : NodeDbSource {

    private val _nodesFlow = MutableStateFlow<Map<String, HierarchyNodeEntity>>(emptyMap())
    override val nodesFlow = _nodesFlow.asStateFlow()

    override suspend fun resetCache() {
        initializeWithSampleData()
    }

    override suspend fun addNode(node: HierarchyNodeEntity) {
        val isParentDeleted = _nodesFlow.value[node.parentId]?.isDeleted == true
        _nodesFlow.update {
            it + (node.id to node.copy(isDeleted = isParentDeleted))
        }
    }

    override suspend fun findNodeById(nodeId: String): HierarchyNodeEntity? {
        return nodesFlow.value[nodeId]
    }

    override suspend fun deleteNode(nodeId: String) {
        _nodesFlow.update { nodeMap ->
            val nodesToUpdate = mutableMapOf<String, HierarchyNodeEntity>()

            // Помечаем сам узел как удаленный
            val node = nodeMap[nodeId]
            if (node != null) {
                nodesToUpdate[nodeId] = node.copy(isDeleted = true)

                // Рекурсивно помечаем всех потомков как удаленные
                markDescendantsAsDeleted(nodeId, nodeMap, nodesToUpdate)
            }

            // Возвращаем обновленную карту
            nodeMap + nodesToUpdate
        }
    }

    private fun markDescendantsAsDeleted(
        parentId: String,
        nodeMap: Map<String, HierarchyNodeEntity>,
        nodesToUpdate: MutableMap<String, HierarchyNodeEntity>,
    ) {
        // Находим всех детей данного узла
        nodeMap.values.filter { it.parentId == parentId }.forEach { child ->
            // Помечаем ребенка как удаленный
            nodesToUpdate[child.id] = child.copy(isDeleted = true)

            // Рекурсивно обрабатываем потомков
            markDescendantsAsDeleted(child.id, nodeMap, nodesToUpdate)
        }
    }

    override suspend fun modifyNode(nodeId: String, newValue: String) {
        _nodesFlow.update {
            val nodeIndex = it
            if (nodeId !in nodeIndex) return@update it
            val node = nodeIndex[nodeId] ?: return@update it
            val modifiedNode = node.copy(value = newValue)
            it + (nodeId to modifiedNode)
        }
    }

    override fun getNearestParentWithDepth(nodeId: String, cache: Set<String>): NodeWithDepth? {
        val nodeIndex = nodesFlow.value
        var currentNode = nodeIndex[nodeId] ?: return null
        var depth = 0

        while (currentNode.parentId != null) {
            depth++
            if (cache.contains(currentNode.parentId)) {
                return NodeWithDepth(currentNode.parentId!!, depth)
            }
            currentNode = nodeIndex[currentNode.parentId] ?: return null
        }
        return null
    }

    fun getNearestDescendants(nodeId: String, cache: Set<String>): List<NodeWithDepth> {
        val nodeIndex = nodesFlow.value
        val result = mutableListOf<NodeWithDepth>()
        val visited = mutableSetOf<String>()

        fun dfs(currentNodeId: String, currentDepth: Int) {
            if (currentNodeId in visited) return
            visited.add(currentNodeId)

            // Ищем детей текущего узла
            nodeIndex.values.forEach { node ->
                if (node.parentId == currentNodeId) {
                    if (node.id in cache) {
                        // Нашли кэшированного потомка - добавляем и НЕ идем глубже
                        result.add(NodeWithDepth(node.id, currentDepth + 1))
                    } else {
                        // Потомок не в кэше - продолжаем поиск глубже
                        dfs(node.id, currentDepth + 1)
                    }
                }
            }
        }

        dfs(nodeId, 0)
        return result
    }

    override fun initializeWithSampleData() {
        val sampleNodes = mapOf(
            "1" to HierarchyNodeEntity(
                id = "1",
                value = "Root Node",
                parentId = null,
                depth = 0,
            ),
            "2" to HierarchyNodeEntity(
                id = "2",
                value = "Child 1",
                parentId = "1",
                depth = 1,
            ),
            "3" to HierarchyNodeEntity(
                id = "3",
                value = "Child 2",
                parentId = "1",
                depth = 1,
            ),
            "4" to HierarchyNodeEntity(
                id = "4",
                value = "Grandchild 1.1",
                parentId = "2",
                depth = 2,
            ),
            "5" to HierarchyNodeEntity(
                id = "5",
                value = "Grandchild 1.2",
                parentId = "2",
                depth = 2,
            ),
            "6" to HierarchyNodeEntity(
                id = "6",
                value = "Grandchild 2.1",
                parentId = "3",
                depth = 2,
            ),
            "7" to HierarchyNodeEntity(
                id = "7",
                value = "Grandchild 1.1.1",
                parentId = "4",
                depth = 3,
            ),
            "8" to HierarchyNodeEntity(
                id = "8",
                value = "Grandchild 1.1.1.1",
                parentId = "7",
                depth = 4,
            ),
        )

        _nodesFlow.update {
            sampleNodes
        }
    }
}

data class NodeWithDepth(
    val nodeId: String,
    val depth: Int,
)