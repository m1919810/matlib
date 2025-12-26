package me.matl114.matlib.utils.registry.impl;

import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceMap;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.ReadRemoveMappingCollection;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.registry.Content;
import me.matl114.matlib.utils.registry.Group;
import me.matl114.matlib.utils.registry.Registry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
public abstract class AbstractRegistry<T> implements Registry<T> {
    // ******************************************** name and key
    protected AbstractRegistry(
            String namespace,
            String id,
            Reference2ReferenceMap<T, Content<T>> registryValues,
            Object2ReferenceMap<String, Content<T>> byIds,
            Object2ReferenceMap<NamespacedKey, Content<T>> byNsk) {
        this.namespace = namespace;
        this.keyName = id;
        this.idName = namespace + ":" + id;
        this.nsKey = new NamespacedKey(namespace, id);
        this.registryValues = registryValues;
        this.byIds = byIds;
        this.byNsk = byNsk;
        // immutable views cache
        this.keysetView = Collections.unmodifiableCollection(byNsk.keySet());
        this.entrysetView = Collections.unmodifiableCollection(byNsk.object2ReferenceEntrySet());
        this.contentView = Collections.unmodifiableCollection(registryValues.keySet());
        this.idSetView = Collections.unmodifiableCollection(byIds.keySet());
    }

    @Getter
    String namespace;

    String keyName;
    String idName;
    NamespacedKey nsKey;

    @Override
    public final String keyStr() {
        return keyName;
    }

    @Override
    public final @NotNull NamespacedKey getKey() {
        return nsKey;
    }

    @Override
    public final String getId() {
        return idName;
    }
    // ******************************************* ownership

    @Override
    public @Nullable Registry<? super T> owner() {
        return owner;
    }

    // ******************************************* data storage
    final Reference2ReferenceMap<T, Content<T>> registryValues;
    final Object2ReferenceMap<String, Content<T>> byIds;
    final Object2ReferenceMap<NamespacedKey, Content<T>> byNsk;
    //    final Set<Group<T>> groups = new ReferenceOpenHashSet<>();

    // ******************************************* data visitor
    static final Content<?> EMPTY = new ContentImpl<>(null, null, null);

    Content<T> defaultValue = (Content<T>) EMPTY;
    NamespacedKey defaultKey;

    @Setter(AccessLevel.PACKAGE)
    Registry<? super T> owner;

    @Override
    public @Nullable NamespacedKey getKey(T value) {
        return registryValues.getOrDefault(value, defaultValue).getKey();
    }

    @Override
    public @Nullable Content<T> getContent(T value) {
        return registryValues.get(value);
    }

    @Override
    public boolean containsKey(NamespacedKey key) {
        return false;
    }

    @Override
    public boolean containsValue(Content<T> value) {
        return this.registryValues.containsValue(value);
    }

    @Override
    public @Nullable T getByKey(NamespacedKey key) {
        return byNsk.getOrDefault(key, defaultValue).value();
    }

    @Override
    public @Nullable T getById(String id) {
        return this.byIds.getOrDefault(id, defaultValue).value();
    }

    @Override
    public @Nullable String getId(T value) {
        return this.registryValues.getOrDefault(value, defaultValue).getId();
    }

    private Collection<NamespacedKey> keysetView;

    @Override
    public Collection<NamespacedKey> keySet() {
        return keysetView;
    }

    private final Collection<Map.Entry<NamespacedKey, Content<T>>> entrysetView;

    @Override
    public final Collection<Map.Entry<NamespacedKey, Content<T>>> entrySet() {
        return entrysetView;
    }

    private final Collection<T> contentView;

    @Override
    public Collection<T> contents() {
        return contentView;
    }

    private final Collection<String> idSetView;

    @Override
    public Collection<String> idSet() {
        return idSetView;
    }

    @Override
    public @NotNull Iterator<T> iterator() {
        return contentView.iterator();
    }

    @Override
    public Stream<T> getByGroup(Group<T> group) {
        return contentView.stream().filter(group::contains);
    }

    @Override
    public Iterable<Content<T>> asContentSet() {
        return Collections.unmodifiableCollection(registryValues.values());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + (defaultValue != null ? ("default:" + defaultValue + ", ") : "")
                + "idMap: "
                + new ReadRemoveMappingCollection<>(
                        this.byIds.entrySet(),
                        (entry) -> Pair.of(entry.getKey(), entry.getValue().value()))
                + "}";
    }
}
