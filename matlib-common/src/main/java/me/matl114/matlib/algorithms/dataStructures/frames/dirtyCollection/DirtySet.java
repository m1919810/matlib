package me.matl114.matlib.algorithms.dataStructures.frames.dirtyCollection;

import java.util.HashSet;
import java.util.Set;

public class DirtySet<V> extends DirtyCollectionImpl<Set<V>, V> implements Set<V>, DirtyCollection<Set<V>> {
    public DirtySet(int value) {
        this(new HashSet<>(value));
    }

    public DirtySet(Set<V> va) {
        super((va));
    }
}
