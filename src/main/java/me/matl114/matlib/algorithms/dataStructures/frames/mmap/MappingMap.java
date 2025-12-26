package me.matl114.matlib.algorithms.dataStructures.frames.mmap;

import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.util.*;
import java.util.function.Function;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.MappingIterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.MutableEntry;
import me.matl114.matlib.algorithms.dataStructures.frames.lazyCollection.DefaultedMap;
import me.matl114.matlib.algorithms.dataStructures.frames.listenedCollection.KeyListenedMap;
import me.matl114.matlib.algorithms.dataStructures.frames.simpleCollection.AbstractReadWriteMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MappingMap<MAP extends Map<K, R>, K, R, V> extends AbstractReadWriteMap<MAP, K, V> {
    public MappingMap<MAP, K, R, V> withValueRemapper(Function<V, R> remapper) {
        this.valueRemapper = remapper;
        return this;
    }

    private KeyListenedMap<R, V> valueMapper;
    private Function<V, R> valueRemapper;

    protected MappingMap(MAP delegate, DefaultedMap<?, R, V> defaultedMap) {
        super(delegate);
        this.valueMapper = new KeyListenedMap<>(defaultedMap) {
            @Override
            public void onUpdate(R val, boolean add) {
                if (!add) {
                    MappingMap.this.delegate.values().remove(val); // .remove(val.getKey());
                }
            }
        };
    }

    @Override
    public V get(Object key) {
        R val = this.delegate.get(key);
        return val == null ? null : this.valueMapper.get(val);
    }

    @Nullable @Override
    public V put(K k, V v) {
        if (MappingMap.this.valueRemapper == null)
            throw new UnsupportedOperationException(
                    "Cannot set value from a Mapping Map because value remapper is absent here!");
        R valueRemap = this.valueRemapper.apply(v);
        R originKey = this.delegate.get(k);

        if (originKey != valueRemap) {
            V originMapping = MappingMap.this.valueMapper.remove(originKey);
            this.valueMapper.put(valueRemap, v);
            this.delegate.put(k, valueRemap);
            return originMapping;
        } else {
            return v;
        }
    }

    @Override
    public V remove(Object key) {
        R val = this.delegate.remove(key);
        return val == null ? null : this.valueMapper.get(val);
    }

    public boolean remove(Object key, Object value) {
        R val = this.delegate.get(key);
        return val == null
                ? false
                : (Objects.equals(this.valueMapper.get(val), value) && this.delegate.remove(key, val));
    }

    public V getOrDefault(Object key, V defaultValue) {
        R val = this.delegate.get(key);
        return val == null ? defaultValue : this.valueMapper.get(val);
    }

    @NotNull @Override
    public Set<K> keySet() {
        return (Set<K>) this.delegate.keySet();
    }

    @NotNull @Override
    public Collection<V> values() {
        return new ReadRemoveMappingCollection<>(this.delegate.entrySet(), (i) -> this.valueMapper.get(i));
    }

    @NotNull @Override
    public Set<Entry<K, V>> entrySet() {
        // how?

        return new EntrySet();
    }

    public class EntrySet extends AbstractSet<Entry<K, V>> {
        Set<Entry<K, R>> value = MappingMap.this.delegate.entrySet();
        MutableEntry<K, V> mutableEntry;

        {
            mutableEntry = new MutableEntry<>(MappingMap.this::put);
        }

        public final int size() {
            return value.size();
        }

        public final void clear() {
            value.clear();
        }

        public final Iterator<Entry<K, V>> iterator() {
            return new MappingIterator<>(this.value.iterator(), (entry) -> {
                mutableEntry.setKey(entry.getKey());
                mutableEntry.setValue0(MappingMap.this.valueMapper.get(entry.getValue()));
                return mutableEntry;
            });
        }
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    public boolean equals(Object obj) {
        return this.delegate.equals(obj);
    }

    public static <K, V, R> Map<K, R> valueMapping(Map<K, V> map, Function<V, R> mapper) {
        // Set<Map.Entry<K,V>> mapEntry =
        DefaultedMap<?, V, R> defaultMap =
                new DefaultedMap<>(new Reference2ReferenceOpenHashMap<V, R>(map.size()), mapper);
        return new MappingMap<>(map, defaultMap);
    }
}
