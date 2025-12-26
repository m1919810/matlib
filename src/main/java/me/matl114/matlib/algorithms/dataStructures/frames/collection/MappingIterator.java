package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Iterator;
import java.util.function.Function;

public class MappingIterator<T, W> implements Iterator<W> {
    protected Iterator<T> delegate;
    protected Function<T, W> reader;

    public MappingIterator(Iterator<T> delegate, Function<T, W> reader) {
        this.delegate = delegate;
        this.reader = reader;
    }

    @Override
    public boolean hasNext() {
        return delegate.hasNext();
    }

    @Override
    public W next() {
        return reader.apply(delegate.next());
    }

    public void remove() {
        this.delegate.remove();
        ;
    }
}
