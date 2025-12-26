package me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import lombok.val;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.ReadRemoveMappingCollection;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.AbstractReadWriteMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class KeyListenedMap<K, V> extends AbstractReadWriteMap<Map<K, V>, K, V> {
    public KeyListenedMap(Map<K, V> delegate) {
        super(delegate);
    }

    public abstract void onUpdate(K val, boolean add);

    @Nullable @Override
    public V put(K key, V value) {
        onUpdate(key, true);
        return super.put(key, value);
    }

    @Override
    public V remove(Object key) {
        onUpdate((K) key, false);
        return (V) ((Map) this.delegate).remove(key);
    }

    public boolean remove(Object key, Object value) {

        if (this.delegate.remove(key, value)) {
            onUpdate((K) key, false);
            return true;
        }
        return false;
    }

    public V putIfAbsent(K key, V value) {
        return this.delegate.computeIfAbsent(key, (k) -> {
            onUpdate(k, true);
            return value;
        });
    }

    public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
        return this.delegate.computeIfAbsent(key, (key0) -> {
            V val = mappingFunction.apply(key0);
            if (val != null) onUpdate(key0, true);
            return val;
        });
    }

    public V computeIfPresent(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return this.delegate.computeIfPresent(key, (key0, val0) -> {
            V val = remappingFunction.apply(key0, val0);
            if (val == null) onUpdate(key0, false);
            return val;
        });
    }

    public V compute(K key, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
        return compute(key, ((k, v) -> {
            V vv = remappingFunction.apply(k, v);
            if (v == null && vv != null) onUpdate(k, true);
            else if (v != null && vv == null) onUpdate(k, false);
            return vv;
        }));
    }

    public V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
        return this.delegate.compute(key, (key0, val) -> {
            if (val == null) {
                if (value != null) onUpdate(key0, true);
                return value;
            } else {
                V vp = remappingFunction.apply(val, value);
                if (vp == null) {
                    onUpdate(key0, false);
                }
                return vp;
            }
        });
    }

    @Override
    public void putAll(@NotNull Map<? extends K, ? extends V> m) {
        for (var entry : m.entrySet()) {
            if (this.delegate.put(entry.getKey(), entry.getValue()) == null) {
                onUpdate(entry.getKey(), true);
            }
        }
    }

    @NotNull @Override
    public Set<K> keySet() {
        return new ListenedCollectionImpl<>(this.delegate.keySet(), false) {
            @Override
            public void onUpdate(K val, boolean val2) {
                KeyListenedMap.this.onUpdate(val, val2);
            }
        };
    }

    @NotNull @Override
    public Collection<V> values() {
        Collection<Map.Entry<K, V>> entries = entrySet();
        return new ReadRemoveMappingCollection<>(entries, Entry::getValue);
    }

    @NotNull @Override
    public Set<Entry<K, V>> entrySet() {
        return new ListenedCollectionImpl<>(this.delegate.entrySet(), false) {
            @Override
            public void onUpdate(Map.Entry<K, V> val, boolean val2) {
                KeyListenedMap.this.onUpdate(val.getKey(), val2);
            }
        };
    }
}
