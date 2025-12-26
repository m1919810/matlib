package me.matl114.matlib;

import static org.junit.jupiter.api.Assertions.*;

import it.unimi.dsi.fastutil.BidirectionalIterator;
import me.matl114.matlib.algorithms.dataStructures.struct.LinkNode;
import org.junit.jupiter.api.Test;

public class LinkNodeTest {
    private void log(String val) {
        System.out.println(val);
    }
    // ==================== 基础功能测试 ====================
    @Test
    public void testBasicNodeFunctionality() {
        // 创建三个节点: A <-> B <-> C
        LinkNode<String> nodeA = new LinkNode<>("A");
        LinkNode<String> nodeB = nodeA.insertAfter("B");
        LinkNode<String> nodeC = nodeB.insertAfter("C");

        // 验证值存储
        assertEquals("A", nodeA.value);
        assertEquals("B", nodeB.value);
        assertEquals("C", nodeC.value);

        // 验证前后连接
        assertNull(nodeA.prev);
        assertEquals(nodeB, nodeA.next);

        assertEquals(nodeA, nodeB.prev);
        assertEquals(nodeC, nodeB.next);

        assertEquals(nodeB, nodeC.prev);
        assertNull(nodeC.next);

        // 验证迭代器
        BidirectionalIterator<String> it = LinkNode.iterator(nodeA);
        assertTrue(it.hasNext());
        assertEquals("B", it.next());
        assertTrue(it.hasNext());
        assertEquals("C", it.next());
        assertFalse(it.hasNext());

        // 反向迭代
        assertTrue(it.hasPrevious());
        assertEquals("C", it.previous());
        assertTrue(it.hasPrevious());
        assertEquals("B", it.previous());
        assertFalse(it.hasPrevious());
        log("Node test1 success");
    }

    // ==================== 头节点功能测试 ====================
    @Test
    public void testHeadNode() {
        // 创建头节点和两个数据节点
        LinkNode.Head head = (LinkNode.Head) LinkNode.createHead();
        LinkNode<String> node1 = head.insertAfter("Node1");
        LinkNode<String> node2 = node1.insertAfter("Node2");

        // 验证头节点属性
        assertNull(head.value);
        assertNull(head.prev);
        assertEquals(node1, head.next);

        // 验证数据节点连接
        assertEquals(head, node1.prev);
        assertEquals(node2, node1.next);

        assertEquals(node1, node2.prev);
        assertNull(node2.next);

        // 验证迭代器从第一个数据节点开始
        BidirectionalIterator<String> it = LinkNode.iterator(head);
        assertTrue(it.hasNext());
        assertEquals("Node1", it.next());
        assertTrue(it.hasNext());
        assertEquals("Node2", it.next());
        assertFalse(it.hasNext());
        log("Node test2 success");
    }

    // ==================== 循环双向链表测试 ====================
    @Test
    public void testCycleLinkedList() {
        // 创建循环头节点
        LinkNode.Cycle head = (LinkNode.Cycle) LinkNode.createBiDirection();

        // 插入三个节点
        LinkNode<String> node1 = head.insertAfter("First");
        LinkNode<String> node2 = node1.insertAfter("Second");
        LinkNode<String> node3 = head.insertAtLast("Last");

        /* 预期结构：
           head ⇄ First ⇄ Second ⇄ head
           head ⇄ Last ⇄ head  // 错误! 需要修正
           正确结构应为：
           head ⇄ First ⇄ Second ⇄ Last ⇄ head
        */

        // 验证头节点连接
        assertEquals(node1, head.next);
        assertEquals(node3, head.prev); // Last 应成为尾节点

        // 验证第一个节点
        assertEquals(head, node1.prev);
        assertEquals(node2, node1.next);

        // 验证第二个节点
        assertEquals(node1, node2.prev);
        assertEquals(node3, node2.next); // 应连接到Last

        // 验证尾节点
        assertEquals(node2, node3.prev);
        assertEquals(head, node3.next); // 应循环回头节点

        // 验证迭代器（完整循环）
        BidirectionalIterator<String> it = LinkNode.iterator(head);
        assertTrue(it.hasNext());
        assertEquals("First", it.next());
        assertTrue(it.hasNext());
        assertEquals("Second", it.next());
        assertTrue(it.hasNext());
        assertEquals("Last", it.next());
        assertFalse(it.hasNext()); // 遇到头节点停止

        // 反向迭代
        assertTrue(it.hasPrevious());
        assertEquals("Last", it.previous());
        assertTrue(it.hasPrevious());
        assertEquals("Second", it.previous());
        assertTrue(it.hasPrevious());
        assertEquals("First", it.previous());
        assertFalse(it.hasPrevious()); // 遇到头节点停止
        log("Node test3 success");
    }

    // ==================== 删除操作测试 ====================
    @Test
    public void testIteratorRemove() {
        // 创建基础链表: Head -> A -> B -> C
        LinkNode.Head head = (LinkNode.Head) LinkNode.createHead();
        LinkNode<String> nodeA = head.insertAfter("A");
        LinkNode<String> nodeB = nodeA.insertAfter("B");
        LinkNode<String> nodeC = nodeB.insertAfter("C");

        // 获取迭代器并删除B
        BidirectionalIterator<String> it = LinkNode.iterator(head);
        it.next(); // 移动到A
        it.next(); // 移动到B
        it.remove(); // 删除B

        // 验证链表结构: Head -> A -> C
        assertEquals(nodeA, head.next);
        assertEquals(nodeC, nodeA.next);
        assertEquals(nodeA, nodeC.prev);
        assertNull(nodeC.next);

        // 验证迭代器状态
        assertEquals("C", it.next()); // 删除后应能继续访问C
        log("Node test4 success");
    }

    // ==================== 边界测试 ====================
    @Test
    public void testEdgeCases() {
        // 空头节点测试
        LinkNode.Head head = (LinkNode.Head) LinkNode.createHead();
        BidirectionalIterator<String> it = LinkNode.iterator(head);
        assertFalse(it.hasNext());
        assertFalse(it.hasPrevious());

        // 单节点循环链表
        LinkNode.Cycle singleHead = (LinkNode.Cycle) LinkNode.createBiDirection();
        LinkNode<String> singleNode = singleHead.insertAfter("Alone");

        assertEquals(singleHead, singleNode.next);
        assertEquals(singleHead, singleNode.prev);
        assertEquals(singleNode, singleHead.next);
        assertEquals(singleNode, singleHead.prev);

        // 迭代器测试
        BidirectionalIterator<String> singleIt = LinkNode.iterator(singleHead);
        assertTrue(singleIt.hasNext());
        assertEquals("Alone", singleIt.next());
        assertFalse(singleIt.hasNext());

        // 反向迭代
        assertTrue(singleIt.hasPrevious());
        assertEquals("Alone", singleIt.previous());
        assertFalse(singleIt.hasPrevious());
        log("Node test5 success");
    }

    // ==================== 异常测试 ====================
    @Test()
    public void testInvalidInsertAtLast() {
        LinkNode.Head head = (LinkNode.Head) LinkNode.createHead();
        // 头节点不应支持insertAtLast
        try {
            head.insertAtLast("ShouldFail");
        } catch (Throwable e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
        log("Node test6 success");
    }
}
