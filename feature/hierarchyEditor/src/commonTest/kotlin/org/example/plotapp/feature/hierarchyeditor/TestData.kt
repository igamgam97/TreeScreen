package org.example.plotapp.feature.hierarchyeditor

import org.example.plotapp.feature.hierarchyeditor.data.entity.hiearchy.HierarchyNodeEntity
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeOperationEntity
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus
import org.example.plotapp.feature.hierarchyeditor.data.source.cache.NewTreeNode

object TestData {

    // Test hierarchy structure:
    // Root
    // ├── Child1
    // │   ├── Grandchild1.1
    // │   └── Grandchild1.2
    // └── Child2
    //     └── Grandchild2.1
    val rootNode = HierarchyNodeEntity(
        id = "root",
        value = "Root Node",
        parentId = null,
        depth = 0,
        isDeleted = false,
        isVirtualConnection = false,
    )
    val child1Node = HierarchyNodeEntity(
        id = "child1",
        value = "Child 1",
        parentId = "root",
        depth = 1,
        isDeleted = false,
        isVirtualConnection = false,
    )
    val child2Node = HierarchyNodeEntity(
        id = "child2",
        value = "Child 2",
        parentId = "root",
        depth = 1,
        isDeleted = false,
        isVirtualConnection = false,
    )
    val grandchild1_1Node = HierarchyNodeEntity(
        id = "grandchild1_1",
        value = "Grandchild 1.1",
        parentId = "child1",
        depth = 2,
        isDeleted = false,
        isVirtualConnection = false,
    )
    val grandchild1_2Node = HierarchyNodeEntity(
        id = "grandchild1_2",
        value = "Grandchild 1.2",
        parentId = "child1",
        depth = 2,
        isDeleted = false,
        isVirtualConnection = false,
    )
    val grandchild2_1Node = HierarchyNodeEntity(
        id = "grandchild2_1",
        value = "Grandchild 2.1",
        parentId = "child2",
        depth = 2,
        isDeleted = false,
        isVirtualConnection = false,
    )
    val testHierarchyNodes = mapOf(
        "root" to rootNode,
        "child1" to child1Node,
        "child2" to child2Node,
        "grandchild1_1" to grandchild1_1Node,
        "grandchild1_2" to grandchild1_2Node,
        "grandchild2_1" to grandchild2_1Node,
    )
    val testHierarchyNodesList = listOf(
        rootNode,
        child1Node,
        child2Node,
        grandchild1_1Node,
        grandchild1_2Node,
        grandchild2_1Node,
    )
    val rootCacheNode = NewTreeNode(
        id = "root",
        value = "Root Node",
        parentId = null,
        status = NodeStatus.Unchanged,
    )
    val child1CacheNode = NewTreeNode(
        id = "child1",
        value = "Child 1",
        parentId = "root",
        status = NodeStatus.Unchanged,
    )
    val child2CacheNode = NewTreeNode(
        id = "child2",
        value = "Child 2",
        parentId = "root",
        status = NodeStatus.Unchanged,
    )
    val testCacheNodes = mapOf(
        "root" to rootCacheNode,
        "child1" to child1CacheNode,
        "child2" to child2CacheNode,
    )
    val addOperation = NodeOperationEntity.Add(
        nodeId = "temp_new_123",
        value = "New Node",
        parentId = "root",
    )
    val modifyOperation = NodeOperationEntity.Modify(
        nodeId = "child1",
        newValue = "Modified Child 1",
    )
    val deleteOperation = NodeOperationEntity.Delete(
        nodeId = "child2",
    )
    val testOperations = listOf(
        addOperation,
        modifyOperation,
        deleteOperation,
    )
    const val NETWORK_ERROR_MESSAGE = "Network error"
    const val NODE_NOT_FOUND_MESSAGE = "Node not found"
    const val INVALID_OPERATION_MESSAGE = "Invalid operation"
    val networkError = RuntimeException(NETWORK_ERROR_MESSAGE)
    val nodeNotFoundError = IllegalArgumentException(NODE_NOT_FOUND_MESSAGE)
    val invalidOperationError = IllegalStateException(INVALID_OPERATION_MESSAGE)
    const val NEW_NODE_VALUE = "New Test Node"
    const val MODIFIED_NODE_VALUE = "Modified Test Node"
    const val TEMP_NODE_ID = "temp_123456789"
    const val PARENT_NODE_ID = "root"
    const val CHILD_NODE_ID = "child1"
}