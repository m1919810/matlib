package me.matl114.matlib.utils.registry.impl;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import java.util.*;
import me.matl114.matlib.utils.registry.Content;
import me.matl114.matlib.utils.registry.NamespacedRegistry;
import me.matl114.matlib.utils.registry.Registry;
import net.kyori.adventure.key.Namespaced;
import org.bukkit.NamespacedKey;

public class NamespacedRegistryImpl<T, W extends Namespaced> extends RegistryImpl<T>
        implements NamespacedRegistry<T, W> {
    public NamespacedRegistryImpl(String namespace, String name, boolean restrictContent) {
        super(namespace, name, restrictContent);
    }

    public NamespacedRegistryImpl(String namespace, String name, boolean restrictContent, NamespacedKey defaultValue) {
        super(namespace, name, restrictContent, defaultValue);
    }

    final Object2ReferenceMap<String, W> namespaceHolder = new Object2ReferenceOpenHashMap<>();

    final Object2ReferenceMap<String, RegistryImpl<? extends T>> subRegistry = new Object2ReferenceOpenHashMap<>();

    protected void registerRecursively(NamespacedKey key, T value, RegistryContent<T> content) {
        super.registerRecursively(key, value, content);
        if (value == defaultValue) {
            // broadcast to all child
            broadCastDefault();
        }
    }

    private void broadCastDefault() {
        for (var child : subRegistry.values()) {
            child.defaultKey = this.defaultKey;
            if (child instanceof NamespacedRegistryImpl<?, ?> named) {
                named.broadCastDefault();
            }
        }
    }

    public <R extends T> Registry<R> getSubRegistry(String namespace) {
        return (Registry<R>) subRegistry.get(namespace);
    }

    @Override
    public <R extends T> Registry<R> getSubRegistry(W holder) {
        String namespace = holder.namespace();
        Preconditions.checkArgument(holder == namespaceHolder.get(namespace), "Namespace holder mismatch");
        return getSubRegistry(namespace);
    }

    @Override
    public Set<String> getNamespaces() {
        return Collections.unmodifiableSet(subRegistry.keySet());
    }

    public Collection<W> getHolders() {
        return Collections.unmodifiableCollection(namespaceHolder.values());
    }

    public <R extends T> Registry<R> createSubRegistry(W holder) {
        Objects.requireNonNull(holder);
        String namespace = holder.namespace();
        Preconditions.checkArgument(
                !subRegistry.containsKey(namespace), "Sub Registry with namespace " + namespace + " already exists");
        RegistryImpl<R> subRegistryInstance =
                new RegistryImpl<>(namespace, this.keyStr(), this.restrict, this.defaultKey).cast();
        subRegistryInstance.owner = this;
        subRegistryInstance.defaultValue = (Content<R>) this.defaultValue;
        subRegistry.put(namespace, subRegistryInstance);
        namespaceHolder.put(namespace, holder);
        return subRegistryInstance;
    }

    protected void onlyClearRecursivelyAndDoNotUnregister() {
        super.onlyClearRecursivelyAndDoNotUnregister();
        Set<RegistryImpl> subs = new HashSet<>(subRegistry.values());
        for (var sub : subs) {
            sub.onlyClearRecursivelyAndDoNotUnregister();
        }
        subRegistry.clear();
    }

    private final void removeSubKeys(RegistryImpl<? extends T> removal) {
        Preconditions.checkArgument(removal.owner == this, "Owner mismatch");
        Set<? extends T> values = new HashSet<>(removal.registryValues.keySet());
        for (var val : values) {
            // unregister and  remove
            this.registryValues.compute(val, (key, holder) -> {
                if (holder instanceof RegistryContent<T> content) {
                    content.unregister(this);
                }
                returnedRegistryContentToTheMap(key, holder);
                return null;
            });
        }
        Set<String> ids = removal.byIds.keySet();
        for (var str : ids) {
            this.byIds.remove(str);
        }
        Set<NamespacedKey> nsk = removal.byNsk.keySet();
        for (var ns : nsk) {
            this.byNsk.remove(ns);
        }
    }

    public boolean removeSubRegistry(W holder) {
        Objects.requireNonNull(holder);
        String namespace = holder.namespace();
        if (!namespaceHolder.remove(namespace, holder)) {
            throw new IllegalArgumentException(
                    "Attempting to remove sub registry with namespace " + namespace + " using unknown holder");
        }
        RegistryImpl<? extends T> removal = subRegistry.remove(namespace);
        Objects.requireNonNull(removal);
        removeSubKeys(removal);
        removal.onlyClearRecursivelyAndDoNotUnregister();
        return true;
    }

    Registry<T> registryView;

    @Override
    public Registry<T> asRegistryView() {
        if (registryView == null) {
            registryView =
                    new AbstractRegistry<T>(this.namespace, this.keyName, this.registryValues, this.byIds, this.byNsk) {
                        @Override
                        public Content<T> registerThis(String rawName, T value) {
                            throw new IllegalStateException("Could not register value in registry view");
                        }

                        @Override
                        public Content<T> register(NamespacedKey namespacedKey, T value) {
                            throw new IllegalStateException("Could not register value in registry view");
                        }

                        @Override
                        public boolean unregister(T value) {
                            throw new IllegalStateException("Could not unregister value in registry view");
                        }

                        @Override
                        public Registry<T> freezeView() {
                            return this;
                        }

                        @Override
                        public void unfreeze() {}

                        @Override
                        public Content<T> createRegistryContent(String value, T newValue) {
                            return NamespacedRegistryImpl.this.createRegistryContent(value, newValue);
                        }
                    };
        }
        return registryView;
    }
}
