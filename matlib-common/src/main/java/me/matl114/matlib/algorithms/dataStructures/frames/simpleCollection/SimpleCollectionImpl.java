package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;

public class SimpleCollectionImpl<S extends Collection<V>, V> extends AbstractReadWriteCollection<S, V>
        implements Collection<V>, SimpleCollection<S, V> {
    public SimpleCollectionImpl(S value) {
        super(value);
    }

    @Override
    public void flush() {}
}
