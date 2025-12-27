package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Iterator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReadOnlyIterator<W> implements Iterator<W> {
    protected Iterator<W> delegate;

    @Override
    public boolean hasNext() {
        return this.delegate.hasNext();
    }

    @Override
    public W next() {
        return this.delegate.next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
