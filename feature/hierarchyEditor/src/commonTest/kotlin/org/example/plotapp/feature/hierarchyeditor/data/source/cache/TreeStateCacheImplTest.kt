@file:Suppress("LateinitUsage")

package org.example.plotapp.feature.hierarchyeditor.data.source.cache

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.example.plotapp.feature.hierarchyeditor.TestData
import org.example.plotapp.feature.hierarchyeditor.data.entity.operation.NodeStatus
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TreeStateCacheImplTest {

    private lateinit var cache: TreeStateCacheImpl

    @BeforeTest
    fun setup() {
        cache = TreeStateCacheImpl()
    }

    @Test
    fun `addNode EXPECT node added with correct status`() {
        // When
        val result = cache.addNode(
            id = TestData.TEMP_NODE_ID,
            value = TestData.NEW_NODE_VALUE,
            parentId = TestData.PARENT_NODE_ID,
        )

        // Then
        assertEquals(TestData.TEMP_NODE_ID, result.id)
        assertEquals(TestData.NEW_NODE_VALUE, result.value)
        assertEquals(TestData.PARENT_NODE_ID, result.parentId)
        assertEquals(NodeStatus.Added, result.status)

        // Verify node is in cache
        val cachedNode = cache.getNode(TestData.TEMP_NODE_ID)
        assertNotNull(cachedNode)
        assertEquals(result, cachedNode)
    }

    @Test
    fun `addNode with existing id EXPECT return existing node`() {
        // Given
        val firstNode = cache.addNode(
            id = TestData.TEMP_NODE_ID,
            value = TestData.NEW_NODE_VALUE,
            parentId = TestData.PARENT_NODE_ID,
        )

        // When
        val secondNode = cache.addNode(
            id = TestData.TEMP_NODE_ID,
            value = "Different Value",
            parentId = "different_parent",
        )

        // Then
        assertEquals(firstNode, secondNode)
        assertEquals(TestData.NEW_NODE_VALUE, secondNode.value) // Original value preserved
    }

    @Test
    fun `moveNode EXPECT node added with unchanged status`() {
        // When
        val result = cache.moveNode(
            id = TestData.CHILD_NODE_ID,
            value = TestData.child1Node.value,
            parentId = TestData.child1Node.parentId,
        )

        // Then
        assertEquals(TestData.CHILD_NODE_ID, result.id)
        assertEquals(NodeStatus.Unchanged, result.status)
    }

    @Test
    fun `removeNode EXPECT node marked as deleted`() {
        // Given
        cache.addNode(
            id = TestData.TEMP_NODE_ID,
            value = TestData.NEW_NODE_VALUE,
            parentId = TestData.PARENT_NODE_ID,
        )

        // When
        val result = cache.removeNode(TestData.TEMP_NODE_ID)

        // Then
        assertTrue(result)
        val node = cache.getNode(TestData.TEMP_NODE_ID)
        assertNotNull(node)
        assertEquals(NodeStatus.Deleted, node.status)
    }

    @Test
    fun `removeNode with children EXPECT all descendants marked as deleted`() {
        // Given
        cache.addNode(id = "parent", value = "Parent", parentId = null)
        cache.addNode(id = "child1", value = "Child 1", parentId = "parent")
        cache.addNode(id = "child2", value = "Child 2", parentId = "parent")
        cache.addNode(id = "grandchild", value = "Grandchild", parentId = "child1")

        // When
        cache.removeNode("parent")

        // Then
        assertEquals(NodeStatus.Deleted, cache.getNode("parent")?.status)
        assertEquals(NodeStatus.Deleted, cache.getNode("child1")?.status)
        assertEquals(NodeStatus.Deleted, cache.getNode("child2")?.status)
        assertEquals(NodeStatus.Deleted, cache.getNode("grandchild")?.status)
    }

    @Test
    fun `removeNode with non-existing id EXPECT return false`() {
        // When
        val result = cache.removeNode("non_existing_id")

        // Then
        assertFalse(result)
    }

    @Test
    fun `renameNode EXPECT node value updated and status changed`() {
        // Given
        cache.addNode(
            id = TestData.TEMP_NODE_ID,
            value = TestData.NEW_NODE_VALUE,
            parentId = TestData.PARENT_NODE_ID,
        )

        // When
        val result = cache.renameNode(TestData.TEMP_NODE_ID, TestData.MODIFIED_NODE_VALUE)

        // Then
        assertTrue(result)
        val node = cache.getNode(TestData.TEMP_NODE_ID)
        assertNotNull(node)
        assertEquals(TestData.MODIFIED_NODE_VALUE, node.value)
        assertEquals(NodeStatus.Added, node.status) // Should remain Added
    }

    @Test
    fun `renameNode with unchanged status EXPECT status changed to modified`() {
        // Given
        cache.moveNode(
            id = TestData.CHILD_NODE_ID,
            value = TestData.child1Node.value,
            parentId = TestData.child1Node.parentId,
        )

        // When
        val result = cache.renameNode(TestData.CHILD_NODE_ID, TestData.MODIFIED_NODE_VALUE)

        // Then
        assertTrue(result)
        val node = cache.getNode(TestData.CHILD_NODE_ID)
        assertNotNull(node)
        assertEquals(TestData.MODIFIED_NODE_VALUE, node.value)
        assertEquals(NodeStatus.Modified, node.status)
    }

    @Test
    fun `renameNode with non-existing id EXPECT return false`() {
        // When
        val result = cache.renameNode("non_existing_id", "New Value")

        // Then
        assertFalse(result)
    }

    @Test
    fun `getRootNodes EXPECT return nodes without parent or orphaned nodes`() {
        // Given
        cache.addNode(id = "root1", value = "Root 1", parentId = null)
        cache.addNode(id = "root2", value = "Root 2", parentId = null)
        cache.addNode(id = "child", value = "Child", parentId = "root1")
        cache.addNode(id = "orphan", value = "Orphan", parentId = "non_existing_parent")

        // When
        val rootNodes = cache.getRootNodes()

        // Then
        assertEquals(3, rootNodes.size)
        assertTrue(rootNodes.any { it.id == "root1" })
        assertTrue(rootNodes.any { it.id == "root2" })
        assertTrue(rootNodes.any { it.id == "orphan" }) // Orphan should be treated as root
    }

    @Test
    fun `getChildren EXPECT return direct children only`() {
        // Given
        cache.addNode(id = "parent", value = "Parent", parentId = null)
        cache.addNode(id = "child1", value = "Child 1", parentId = "parent")
        cache.addNode(id = "child2", value = "Child 2", parentId = "parent")
        cache.addNode(id = "grandchild", value = "Grandchild", parentId = "child1")

        // When
        val children = cache.getChildren("parent")

        // Then
        assertEquals(2, children.size)
        assertTrue(children.any { it.id == "child1" })
        assertTrue(children.any { it.id == "child2" })
        assertFalse(children.any { it.id == "grandchild" }) // Should not include grandchild
    }

    @Test
    fun `hasNode EXPECT return correct boolean value`() {
        // Given
        cache.addNode(
            id = TestData.TEMP_NODE_ID,
            value = TestData.NEW_NODE_VALUE,
            parentId = TestData.PARENT_NODE_ID,
        )

        // Then
        assertTrue(cache.hasNode(TestData.TEMP_NODE_ID))
        assertFalse(cache.hasNode("non_existing_id"))
    }

    @Test
    fun `size and isEmpty EXPECT return correct values`() {
        // Initially empty
        assertTrue(cache.isEmpty())
        assertEquals(0, cache.size())

        // Add node
        cache.addNode(
            id = TestData.TEMP_NODE_ID,
            value = TestData.NEW_NODE_VALUE,
            parentId = TestData.PARENT_NODE_ID,
        )

        // Not empty anymore
        assertFalse(cache.isEmpty())
        assertEquals(1, cache.size())
    }

    @Test
    fun `clear EXPECT remove all nodes`() {
        // Given
        cache.addNode(id = "node1", value = "Node 1", parentId = null)
        cache.addNode(id = "node2", value = "Node 2", parentId = null)

        // When
        cache.clear()

        // Then
        assertTrue(cache.isEmpty())
        assertEquals(0, cache.size())
        assertNull(cache.getNode("node1"))
        assertNull(cache.getNode("node2"))
    }

    @Test
    fun `updateNodes EXPECT update node statuses correctly`() {
        // Given
        cache.addNode(id = "node1", value = "Node 1", parentId = null)
        cache.moveNode(id = "node2", value = "Node 2", parentId = null)

        val dbNodes = mapOf(
            "node1" to TestData.rootNode.copy(id = "node1", isDeleted = true),
            "node2" to TestData.child1Node.copy(id = "node2", isDeleted = false),
        )

        // When
        cache.updateNodes(dbNodes)

        // Then
        assertEquals(NodeStatus.Deleted, cache.getNode("node1")?.status)
        assertEquals(NodeStatus.Unchanged, cache.getNode("node2")?.status)
    }

    @Test
    fun `nodesFlow EXPECT emit changes when nodes are modified`() = runTest {
        cache.nodesFlow.test {
            // Initial empty state
            assertEquals(emptyMap(), awaitItem())

            // Add node
            cache.addNode(
                id = TestData.TEMP_NODE_ID,
                value = TestData.NEW_NODE_VALUE,
                parentId = TestData.PARENT_NODE_ID,
            )

            val nodesAfterAdd = awaitItem()
            assertEquals(1, nodesAfterAdd.size)
            assertTrue(nodesAfterAdd.containsKey(TestData.TEMP_NODE_ID))

            // Clear cache
            cache.clear()

            val nodesAfterClear = awaitItem()
            assertEquals(emptyMap(), nodesAfterClear)
        }
    }
}