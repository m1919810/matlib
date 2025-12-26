package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Collection;
import java.util.List;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.SimpleListImpl;

public interface ListMapView<W, T> extends CollectionMapView<W, T>, List<T> {
    public static <R> ListMapView<R, R> identity(List<R> list) {
        return new SimpleListImpl<>(list);
    }

    public boolean removeAll(Collection<?> c);
}
