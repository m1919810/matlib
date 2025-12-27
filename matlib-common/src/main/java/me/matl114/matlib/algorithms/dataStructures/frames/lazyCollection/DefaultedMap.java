package me.matl114.matlib.algorithms.dataStructures.frames.lazyCollection;

import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.ReadRemoveMappingCollection;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.SimpleMap;
import org.jetbrains.annotations.NotNull;

public class DefaultedMap<MAP extends Map<K, V>, K, V> extends SimpleMap<MAP, K, V> {
    Function<K, V> defaultProvider;

    public DefaultedMap(MAP delegate, Function<K, V> defaultGenerator) {
        super(delegate);
        this.defaultProvider = defaultGenerator;
    }

    public static <R extends Map<T, W>, T, W> DefaultedMap<R, T, W> initWithKeyset(
            Set<T> keySet, Function<T, W> generator, IntFunction<R> mapConstructor) {
        R map0 = mapConstructor.apply(keySet.size());
        for (var key : keySet) {
            map0.put(key, null);
        }
        return new DefaultedMap<>(map0, generator);
    }

    @Override
    public V get(Object key) {
        return this.delegate.computeIfAbsent((K) key, defaultProvider);
    }

    @Override
    public boolean containsValue(Object value) {
        if (this.delegate.containsValue(null)) {
            // generate all null values
            for (var key : keySet()) {
                if (Objects.equals(value, get(key))) return true;
            }
            return false;
        } else {
            return this.delegate.containsValue(value);
        }
    }

    @Override
    public @NotNull Collection<V> values() {
        return new ReadRemoveMappingCollection<Map.Entry<K, V>, V>(this.delegate.entrySet(), (entry) -> {
            V val = entry.getValue();
            if (val == null) {
                entry.setValue((val = this.defaultProvider.apply(entry.getKey())));
            }
            return val;
        });
    }

    @Override
    public @NotNull Set<Entry<K, V>> entrySet() {
        return new ReadRemoveMappingCollection<Map.Entry<K, V>, Entry<K, V>>(this.delegate.entrySet(), (entry) -> {
            if (entry.getValue() == null) {
                entry.setValue(this.defaultProvider.apply(entry.getKey()));
            }
            return entry;
        });
    }
}
