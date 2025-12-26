package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.List;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;

public interface SimpleList<V> extends SimpleCollection<List<V>, V>, ListMapView<V, V> {
    @Override
    default void flush() {}
}
