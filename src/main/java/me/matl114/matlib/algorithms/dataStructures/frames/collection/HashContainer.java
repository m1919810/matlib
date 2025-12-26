package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.*;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import me.matl114.matlib.common.lang.annotations.Experimental;
import me.matl114.matlib.common.lang.annotations.NotCompleted;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;

@NotCompleted
@Experimental
public class HashContainer<T extends Object> extends AbstractSet<T> implements Set<T> {
    private static final int DEFAULT_SIZE = 16;
    LinkNode<T>[] table;
    int size = 0;
    private ToIntFunction<T> hashFunction;

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean contains(Object o) {
        return findFirst(hashFunction.applyAsInt((T) o), o::equals) != null;
    }

    public T findFirst(int hashCode, Predicate<T> predicate) {
        int index = hashCode & (table.length - 1);
        LinkNode<T> node = table[index];
        T testData;
        while (node != null) {
            testData = node.data;
            if (predicate.test(testData)) {
                return testData;
            }
            node = node.next;
        }
        return null;
    }
    //
    private static class LinkNode<T extends Object> {
        final int hash;
        LinkNode<T> next;
        LinkNode<T> prev;
        final T data;

        public LinkNode(T data, int hash) {
            this.data = data;
            this.hash = hash;
        }
    }

    public HashContainer(int init, Collection<T> data, ToIntFunction<T> hashFunction) {
        this.hashFunction = hashFunction;
        int initialSize = DEFAULT_SIZE;
        do {
            initialSize <<= 1;
        } while (initialSize < init);
        initialSize <<= 1;
        this.table = new LinkNode[initialSize];
        this.addAll(data);
    }

    public HashContainer(int init, ToIntFunction<T> hashFunction) {
        this(Math.max(init, DEFAULT_SIZE), Set.of(), hashFunction);
    }

    public HashContainer(Collection<T> data, ToIntFunction<T> hashFunction) {
        this(Math.max(data.size(), DEFAULT_SIZE), data, hashFunction);
    }

    public HashContainer(Collection<T> data) {
        this(data, Objects::hashCode);
    }

    public HashContainer() {
        this(Set.of());
    }

    @Override
    public Iterator<T> iterator() {
        return new NodeIterator();
    }

    // todo not implement yet

    @Override
    public boolean add(T t) {
        int hash = hashFunction.applyAsInt(t);
        int index = hash & (table.length - 1);
        LinkNode<T> node = table[index];
        if (node == null) {
            table[index] = new LinkNode<>(t, hash);
        } else {
            while (node.next != null) {
                node = node.next;
            }
            node.next = new LinkNode<>(t, hash);
        }
        this.size += 1;
        return true;
    }

    @Override
    public boolean remove(Object o) {
        throw new NotImplementedYet();
        // return false;
    }

    public class NodeIterator implements Iterator<T> {
        int index = 0;
        LinkNode<T> next;

        public NodeIterator() {
            init();
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public T next() {
            LinkNode<T> node = next;
            if (next.next == null) {
                do {
                    next = table[index];
                    index++;
                } while (next == null && index < table.length);
            } else {
                next = next.next;
            }
            return node.data;
        }

        public void init() {
            do {
                next = table[index];
                index++;
            } while (next == null && index < table.length);
        }
    }
}
