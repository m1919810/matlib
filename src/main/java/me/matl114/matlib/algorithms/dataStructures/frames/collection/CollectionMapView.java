package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Collection;

/**
 * this interface means that this object maps some thing (W) to viewable T,
 * @param <W>
 * @param <T>
 */
public interface CollectionMapView<W, T> extends Collection<T> {
    /**
     * because transformation costs much, they holds a cache or sth , or they are viewing Immutable collections or something hard to modify, so when all modification is done, we sees them as batch modification and when you invoke batchWriteback, they will apply these patches to the origin source
     */
    public void batchWriteback();

    /**
     *whether batchwriteback will take any action
     * @return
     */
    public boolean isDelayWrite();

    /**
     * //when flush, delete cache and read new data from source
     */
    public void flush();
}
