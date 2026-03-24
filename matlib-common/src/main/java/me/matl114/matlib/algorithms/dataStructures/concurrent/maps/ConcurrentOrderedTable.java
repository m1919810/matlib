package me.matl114.matlib.algorithms.dataStructures.concurrent.maps;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentOrderedTable<K, V> {
    private final ConcurrentHashMap<K, V> delegate;

    private final ConcurrentLinkedQueue<K> order;

    public ConcurrentOrderedTable() {
        this.delegate = new ConcurrentHashMap<>();
        this.order = new ConcurrentLinkedQueue<>();
    }

    public void remove(K key) {
        this.delegate.computeIfPresent(key, (k, v) -> {
            this.order.remove(key);
            return null;
        });
    }

    public void put(K key, V value) {
        if (value != null) {
            putNonnull(key, value);
        } else {
            remove(key);
        }
    }

    private void putNonnull(K key, V value) {
        this.delegate.compute(key, (k, v) -> {
            if (v == null) {
                this.order.add(k);
                return value;
            } else {
                return value;
            }
        });
    }

    public V get(K key) {
        return this.delegate.get(key);
    }

    private class Itr {
        public Itr(Iterator<K> iterator) {
            this.iterator = iterator;
        }

        K last;
        Iterator<K> iterator;
        V lastV;
        boolean nextRead;

        public boolean updateNext() {
            // iterator.next() != null is true
            if (nextRead) {
                return true;
            }
            boolean hasNext;
            while ((hasNext = iterator.hasNext()) && (last = iterator.next()) != null && (lastV = get(last)) == null) {}

            nextRead = hasNext;
            return hasNext;
        }

        public void markRead() {
            if (!nextRead) throw new IllegalStateException("read before hasNext");
            this.nextRead = false;
        }
    }

    public class KeyItr extends Itr implements Iterator<K> {

        public KeyItr(Iterator<K> iterator) {
            super(iterator);
        }

        @Override
        public boolean hasNext() {
            return updateNext();
        }

        @Override
        public K next() {
            markRead();
            return last;
        }

        @Override
        public void remove() {
            if (last == null) throw new IllegalStateException("remove before hasNext");
            if (nextRead) throw new IllegalStateException("remove before read");
            ConcurrentOrderedTable.this.remove(last);
        }
    }

    public Iterator<K> iterator() {
        Iterator<K> iterator = this.order.iterator();
        return new KeyItr(iterator);
    }
}
