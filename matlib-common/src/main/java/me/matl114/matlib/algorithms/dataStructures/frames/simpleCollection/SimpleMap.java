package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Map;

public class SimpleMap<MAP extends Map<K, V>, K, V> extends AbstractReadWriteMap<MAP, K, V> {
    public SimpleMap(MAP delegate) {
        super(delegate);
    }
}
