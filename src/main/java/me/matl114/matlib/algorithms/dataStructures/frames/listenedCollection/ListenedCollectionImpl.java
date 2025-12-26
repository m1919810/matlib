package me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lombok.val;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.SimpleCollectionImpl;
import org.jetbrains.annotations.NotNull;

public abstract class ListenedCollectionImpl<S extends Collection<V>, V> extends SimpleCollectionImpl<S, V>
        implements Collection<V>, ListenedCollection<S, V> {
    public ListenedCollectionImpl(S delegate, boolean applyOnInit) {
        super(delegate);
        if (applyOnInit) {
            this.delegate.forEach(this::onAdd);
        }
    }

    @Override
    public abstract void onUpdate(V val, boolean val2);

    protected void onRemove(V val) {
        onUpdate(val, false);
    }

    protected void onAdd(V val) {
        onUpdate(val, true);
    }

    @Override
    public boolean add(V v) {
        if (this.delegate.add(v)) {
            onAdd(v);
            return true;
        }
        return false;
    }

    @Override
    public boolean remove(Object o) {
        if (this.delegate.remove(o)) {
            onRemove((V) o);
            return true;
        }
        return false;
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends V> c) {
        if (this.delegate.addAll(c)) {
            c.forEach(this::onAdd);
            return true;
        }
        return false;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        // todo this method needs rewrite, or set to DO-NOT-CALL
        Set<V> sets = new HashSet<>(delegate);
        if (this.delegate.retainAll(c)) {
            for (V set : sets) {
                if (!contains(set)) {
                    onRemove(set);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        boolean has = false;
        for (var re : c) {
            if (remove(re)) {
                has = true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        this.delegate.forEach(this::onRemove);
        this.delegate.clear();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Set<?> val && val.equals(this.delegate);
    }

    @NotNull @Override
    public Iterator<V> iterator() {
        return new ListenedCollectionImpl.ListenedIterator(this.delegate.iterator());
    }

    protected class ListenedIterator<W extends Iterator<V>> implements Iterator<V> {
        final W delegate;
        volatile V next;

        ListenedIterator(W delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean hasNext() {
            return delegate.hasNext();
        }

        @Override
        public V next() {
            return (next = delegate.next());
        }

        public void remove() {
            // save current ,
            // using next() and remove() concurrently may cause unpredictable exception, so this way should be safe
            V toRemove = next;
            this.delegate.remove();
            ListenedCollectionImpl.this.onRemove(toRemove);
        }
    }
}
