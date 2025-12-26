package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;

public class AbstractReadRemoveCollection<DELEGATE extends Collection<?>, E>
        extends AbstractReadWriteCollection<DELEGATE, E> {
    public AbstractReadRemoveCollection(DELEGATE delegate) {
        super(delegate);
    }

    public final boolean add(E e) {
        throw new UnsupportedOperationException();
    }

    public final boolean addAll(Collection<? extends E> coll) {
        throw new UnsupportedOperationException();
    }
}
