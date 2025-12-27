package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.Map;
import java.util.function.BiConsumer;
import lombok.AllArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
public class MutableEntry<K, V> implements Map.Entry<K, V> {
    public MutableEntry(BiConsumer<K, V> callback) {
        this.modification = callback;
    }

    public MutableEntry() {
        this((k, v) -> {
            throw new UnsupportedOperationException("Setting map entry value is not supported here");
        });
    }

    @Setter
    K key;

    @Setter
    V value0;

    BiConsumer<K, V> modification;

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value0;
    }

    @Override
    public V setValue(V value) {
        V oldValue = value0;
        modification.accept(key, value);
        value0 = value;
        return oldValue;
    }
}
