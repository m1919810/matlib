package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.experimental.Delegate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AllArgsConstructor
@SuppressWarnings("all")
public class AbstractReadWriteMap<MAP extends Map, K, V> implements Map<K, V> {
    @Delegate(types = {Map.class})
    protected MAP delegate;

    public MAP getHandle() {
        return delegate;
    }

    @Override
    public V get(Object key) {
        return (V) ((Map) this.delegate).get(key);
    }

    @Nullable @Override
    public V put(K key, V value) {
        return (V) ((Map) this.delegate).put(key, value);
    }

    @Override
    public V remove(Object key) {
        return (V) ((Map) this.delegate).remove(key);
    }

    public boolean remove(Object key, Object value) {
        return this.delegate.remove(key, value);
    }

    public V putIfAbsent(K key, V value) {
        return (V) ((Map) this.delegate).putIfAbsent(key, value);
    }

    public boolean replace(K key, V oldValue, V newValue) {
        return ((Map) this.delegate).replace(key, oldValue, newValue);
    }

    public V replace(K key, V value) {
        return (V) ((Map) this.delegate).replace(key, value);
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return (V) ((Map) this.delegate).computeIfAbsent(key, mappingFunction);
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return (V) ((Map) this.delegate).computeIfPresent(key, remappingFunction);
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return (V) ((Map) this.delegate).compute(key, remappingFunction);
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return (V) ((Map) this.delegate).merge(key, value, remappingFunction);
    }

    public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
        this.delegate.replaceAll(function);
    }

    public V getOrDefault(Object key, V defaultValue) {
        return (V) ((Map) this.delegate).getOrDefault(key, defaultValue);
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        ((Map) this.delegate).putAll((Map) m);
    }

    @NotNull @Override
    public Set<K> keySet() {
        return (Set<K>) this.delegate.keySet();
    }

    @NotNull @Override
    public Collection<V> values() {
        return (Collection<V>) this.delegate.values();
    }

    @NotNull @Override
    public Set<Entry<K, V>> entrySet() {
        return (Set<Entry<K, V>>) this.delegate.entrySet();
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }
}
