package me.matl114.matlib.algorithms.dataStructures.struct;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.BidirectionalIterator;

public class LinkNode<TYPE> {
    public LinkNode(TYPE v) {
        this.value = v;
    }

    public LinkNode(TYPE v, LinkNode<TYPE> next) {
        this.value = v;
        this.next = next;
        if (next != null) {
            next.prev = this;
        }
    }

    public TYPE value;
    public LinkNode<TYPE> prev;
    public LinkNode<TYPE> next;

    public static <TYPE> LinkNode<TYPE> createHead() {
        return new Head();
    }

    public static <TYPE> LinkNode<TYPE> createBiDirection() {
        return new Cycle();
    }

    public static class Head extends LinkNode {
        public Head() {
            super(null);
        }
    }

    public static class Cycle extends LinkNode {

        public Cycle() {
            super(null);
            this.next = this.prev = this;
        }
    }

    public LinkNode<TYPE> insertAfter(TYPE value) {
        this.next = new LinkNode<>(value, this.next);
        this.next.prev = this;
        return this.next;
    }

    public LinkNode<TYPE> insertAtLast(TYPE value) {
        Preconditions.checkArgument(
                this instanceof LinkNode.Cycle bidir, "Link Head does not support insertion at last : ");
        var lastElement = this.prev;
        return lastElement.insertAfter(value);
        //        var node = new LinkNode<>(value, this);
        //        node.prev = this.prev;
        //        this.prev.next = node;
        //        this.prev = node;
        //        return node;
    }

    public static <TYPE> Iterable<TYPE> wrapIter(final LinkNode<TYPE> head) {
        return () -> iterator(head);
    }

    public static <TYPE> BidirectionalIterator<TYPE> iterator(final LinkNode<TYPE> curr) {
        return iter0(curr, curr.next);
    }

    private static <TYPE> BidirectionalIterator<TYPE> iter0(final LinkNode<TYPE> origin0, final LinkNode<TYPE> curr) {
        // 我们需要保证curr是head
        // curr.prev == null || curr.prev == last element

        return new BidirectionalIterator<TYPE>() {
            LinkNode<TYPE> lastRet;

            @Override
            public TYPE previous() {
                lastRet = currInternal;
                currInternal = currInternal.prev;
                if (currInternal == null) {
                    currInternal = origin0;
                }
                return lastRet.value;
            }

            @Override
            public boolean hasPrevious() {
                return currInternal != origin0;
            }

            LinkNode<TYPE> currInternal;

            {
                currInternal = origin0;
                currInternal.next = curr;
            }

            @Override
            public boolean hasNext() {
                // add support to cycle link list
                return currInternal.next != null && currInternal.next != origin0;
            }

            @Override
            public TYPE next() {
                return (lastRet = currInternal = currInternal.next).value;
            }

            public void remove() {
                LinkNode<TYPE> next = lastRet.next;
                if (next != null) {
                    // 考虑前一个是否是origin0，
                    // 由于origin0是独立的 我们不会把他引入任何ret值的prev中
                    next.prev = lastRet.prev;
                }
                LinkNode<TYPE> pre = lastRet.prev == null ? origin0 : lastRet.prev;
                if (currInternal == lastRet) {
                    // next remove,弹回cursor
                    currInternal = pre;
                }
                pre.next = next;
                lastRet.prev = lastRet.next = null;
                lastRet = null;
            }
        };
    }
}
