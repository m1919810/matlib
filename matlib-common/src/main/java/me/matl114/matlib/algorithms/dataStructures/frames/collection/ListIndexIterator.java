package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Iterator;
import java.util.List;

public class ListIndexIterator<T> implements Iterator<T> {
    List<T> delegate;
    int index;

    public ListIndexIterator(List<T> delegate) {
        this.delegate = delegate;
        this.index = 0;
    }

    @Override
    public boolean hasNext() {
        return index < this.delegate.size();
    }

    @Override
    public T next() {
        return this.delegate.get(index++);
    }

    @Override
    public void remove() {
        this.delegate.remove(--index);
    }
}
