package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.CollectionMapView;

/**
 * this is just a interface holder,
 */
public interface SimpleCollection<S extends Collection<T>, T> extends CollectionMapView<T, T> {
    public S getHandle();

    default void batchWriteback() {}

    default boolean isDelayWrite() {
        return false;
    }
}
