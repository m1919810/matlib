package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Iterator;
import java.util.NoSuchElementException;
import lombok.AllArgsConstructor;
import lombok.val;
import me.matl114.matlib.algorithms.dataStructures.struct.LinkNode;
import org.jetbrains.annotations.NotNull;

public class SimpleLinkList<T> implements Stack<T>, Iterable<T> {
    protected LinkNode<T> head;

    public SimpleLinkList() {}

    @Override
    public T peek() {
        return head == null ? null : head.value;
    }

    @Override
    public T pop() {
        if (head != null) {
            T val = head.value;
            this.head = head.next;
            return val;
        } else {
            throw new NoSuchElementException("There is no element in the Stack");
        }
    }

    @Override
    public T poll() {
        if (head != null) {
            T val = head.value;
            this.head = head.next;
            return val;
        } else return null;
    }

    @Override
    public void push(T val) {
        LinkNode<T> newNode = new LinkNode<>(val, head);
        newNode.next = head;
        //        newNode.value = val;
        this.head = newNode;
    }

    @Override
    public boolean isEmpty() {
        return head == null;
    }

    @Override
    public void clear() {
        this.head = null;
    }

    public String toString() {
        var sb = new StringBuilder();
        sb.append('[');
        if (head != null) {
            LinkNode<T> iter = head;
            while (iter != null) {
                sb.append(iter.value);
                sb.append(',');
                iter = iter.next;
            }
        }
        sb.append(']');
        return sb.toString();
    }

    public void addFirst(T value) {
        push(value);
        //        LinkNode<T> newNode = LinkNode.getInstance();
        //        newNode.value= value;
        //        newNode.next = head;
        //        head = newNode;
    }

    public T getFirst() {
        return peek();
        // return this.head == null ? null: this.head.value;
    }

    public void moveToFirst(SimpleLinkList<T> other) {
        LinkNode<T> otherLast = other.head;
        LinkNode<T> next;
        // if other not empty
        if (otherLast != null) {
            while ((next = otherLast.next) != null) {
                otherLast = next;
            }
            otherLast.next = this.head;
            this.head = other.head;
            // remove other's record
            other.head = null;
        }
    }

    @NotNull @Override
    public Iterator<T> iterator() {
        return new NodeIter<>(this.head);
    }

    @AllArgsConstructor
    protected static class NodeIter<T> implements Iterator<T> {

        public LinkNode<T> node;

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public T next() {
            T val = node.value;
            node = node.next;
            return val;
        }
    }
}
