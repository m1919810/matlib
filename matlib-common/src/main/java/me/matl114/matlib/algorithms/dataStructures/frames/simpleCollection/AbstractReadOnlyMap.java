package me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AbstractReadOnlyMap<MAP extends Map<?, ?>, K, V> extends AbstractReadWriteMap<MAP, K, V>
        implements Map<K, V> {
    public AbstractReadOnlyMap(MAP delegate) {
        super(delegate);
    }

    @Nullable @Override
    public final V put(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void putAll(@NotNull Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final void clear() {
        throw new UnsupportedOperationException();
    }

    @NotNull @Override
    public Set<K> keySet() {
        return new AbstractReadOnlyCollection<>(this.delegate.keySet());
    }

    @NotNull @Override
    public Collection<V> values() {
        return new AbstractReadOnlyCollection<>(this.delegate.values());
    }

    @NotNull @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractReadOnlyCollection<>(this.delegate.entrySet());
    }
}
