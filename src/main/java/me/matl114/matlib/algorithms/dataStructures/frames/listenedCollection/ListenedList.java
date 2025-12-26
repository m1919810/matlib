package me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection;

import java.util.*;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.SimpleList;
import org.jetbrains.annotations.NotNull;

public abstract class ListenedList<T> extends ListenedCollectionImpl<List<T>, T> implements List<T>, SimpleList<T> {

    public ListenedList(List<T> delegate, boolean applyOnInit) {
        super(delegate, applyOnInit);
    }

    @Override
    public boolean addAll(int index, @NotNull Collection<? extends T> c) {
        c.forEach(this::onAdd);
        return this.delegate.addAll(index, c);
    }

    @Override
    public T get(int index) {
        return this.delegate.get(index);
    }

    @Override
    public T set(int index, T element) {
        T val = this.delegate.get(index);
        onRemove(val);
        onAdd(element);
        return this.delegate.set(index, element);
    }

    @Override
    public void add(int index, T element) {
        onAdd(element);
        this.delegate.add(index, element);
    }

    @Override
    public T remove(int index) {
        T val = this.delegate.remove(index);
        onRemove(val);
        return val;
    }

    @Override
    public int indexOf(Object o) {
        return this.delegate.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return this.delegate.lastIndexOf(o);
    }

    @NotNull @Override
    public ListIterator<T> listIterator() {
        return new ListenedListIterator(this.delegate.listIterator());
    }

    @NotNull @Override
    public ListIterator<T> listIterator(int index) {
        return new ListenedListIterator(this.delegate.listIterator(index));
    }

    protected class ListenedListIterator extends ListenedIterator<ListIterator<T>> implements ListIterator<T> {

        ListenedListIterator(ListIterator<T> delegate) {
            super(delegate);
        }

        @Override
        public boolean hasPrevious() {
            return this.delegate.hasPrevious();
        }

        @Override
        public T previous() {
            return (next = this.delegate.previous());
        }

        @Override
        public int nextIndex() {
            return this.delegate.nextIndex();
        }

        @Override
        public int previousIndex() {
            return this.delegate.previousIndex();
        }

        @Override
        public void set(T t) {
            onRemove(next);
            next = t;
            onAdd(t);
            this.delegate.set(t);
        }

        @Override
        public void add(T t) {
            onAdd(t);
            this.delegate.add(t);
        }
    }

    @NotNull @Override
    public List<T> subList(int fromIndex, int toIndex) {
        return new ListenedSubList(this.delegate.subList(fromIndex, toIndex));
    }

    private class ListenedSubList extends ListenedList<T> {

        public ListenedSubList(List<T> delegate) {
            super(delegate, false);
            // just a view of this (delegate), no need to trigger update for elements
        }

        @Override
        public void onUpdate(T val, boolean val2) {
            ListenedList.this.onUpdate(val, val2);
        }
    }
}
