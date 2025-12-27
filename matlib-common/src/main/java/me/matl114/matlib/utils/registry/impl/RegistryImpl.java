package me.matl114.matlib.utils.registry.impl;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.*;
import java.util.*;
import lombok.experimental.Accessors;
import me.matl114.matlib.utils.registry.Content;
import me.matl114.matlib.utils.registry.Registry;
import org.bukkit.NamespacedKey;

@Accessors(fluent = true)
public class RegistryImpl<T> extends AbstractRegistry<T> implements Registry<T> {
    public RegistryImpl(String namespace, String name, boolean restrictContent) {
        super(
                namespace,
                name,
                new Reference2ReferenceOpenHashMap<>(64),
                new Object2ReferenceOpenHashMap<>(64),
                new Object2ReferenceOpenHashMap<>(64));
        this.namespace = namespace;
        this.keyName = name;
        this.idName = namespace + ":" + name;
        this.nsKey = new NamespacedKey(namespace, name);
        this.restrict = restrictContent;
        if (this.restrict) this.restrictlyContentMap = new WeakHashMap<>(16);
    }

    public RegistryImpl(String namespace, String name, boolean restrictContent, NamespacedKey defaultValue) {
        this(namespace, name, restrictContent);
        this.defaultKey = defaultValue;
    }

    WeakHashMap<T, Content<T>> restrictlyContentMap;

    // ----------------------------------------- register and unregister

    final boolean restrict;
    boolean freeze = false;
    boolean removal = false;

    @Override
    public Content<T> registerThis(String name, T value) {

        NamespacedKey thisNamespaceKey = new NamespacedKey(namespace(), name);
        return register(thisNamespaceKey, value);
    }

    protected void validate(NamespacedKey key) {
        validateWrite();
        validateNamespace(key);
    }

    protected void validateWrite() {
        if (freeze) {
            throw new IllegalStateException("Registry is already frozen(trying to modify registry)");
        } else if (removal) {
            throw new IllegalStateException("Registry is already removed(trying to modify registry)");
        }
    }

    protected void validateNamespace(NamespacedKey key) {
        if (!Objects.equals(namespace(), key.getNamespace())) {
            throw new IllegalStateException("Adding different namespaced key!(This register is in namespace "
                    + namespace + " while adding " + key);
        }
    }

    protected void registerRecursively(NamespacedKey key, T value, RegistryContent<T> content) {
        if (Objects.equals(key, this.defaultKey)) {
            this.defaultValue = content;
        }
        this.registryValues.put(value, content);
        this.byIds.put(content.getId(), content);
        this.byNsk.put(content.getKey(), content);
        if (this.owner instanceof RegistryImpl impl) {
            impl.registerRecursively(key, value, content);
        }
    }

    protected RegistryContent<T> resolveRestrictContent(T value) {
        RegistryContent<T> holder = null;
        if (owner instanceof RegistryImpl impl) {
            holder = impl.resolveRestrictContent(value);
        }
        return holder == null ? (RegistryContent<T>) this.restrictlyContentMap.remove(value) : holder;
    }

    @Override
    public final Content<T> register(NamespacedKey namespacedKey, T value) {
        Objects.requireNonNull(namespacedKey);
        Objects.requireNonNull(value);
        validate(namespacedKey);
        Preconditions.checkArgument(!registryValues.containsKey(value), "Duplicate registry of value " + value);
        Preconditions.checkArgument(!byNsk.containsKey(namespacedKey), "Adding duplicate namespacedKey to");
        RegistryContent<T> valueHolder;

        if (this.restrict) {
            if ((valueHolder = resolveRestrictContent(value)) == null) {
                throw new IllegalArgumentException(
                        "Registry is restricted! Fail to find registryContent for value " + value);
            }
            Preconditions.checkArgument(
                    Objects.equals(valueHolder.keyStr(), namespacedKey.getKey()),
                    "Registry is restricted ! value's key mismatch");
        } else {
            valueHolder = new RegistryContent<>(this, namespacedKey, value);
        }
        valueHolder.registerToOwner(this);
        registerRecursively(namespacedKey, value, valueHolder);
        return valueHolder;
    }

    protected boolean unregisterInChildNamespace(NamespacedKey key, T value, Content<T> content) {
        throw new IllegalArgumentException("This registry does not have child namespace, so key:" + key + " with value"
                + value + " is invalid argument here!");
    }

    protected boolean unregisterRecursively(NamespacedKey key, T value, Content<T> content) {
        this.registryValues.remove(value);
        this.byIds.remove(content.getId());
        this.byNsk.remove(content.getKey());
        if (this.owner instanceof RegistryImpl impl) {
            return impl.unregisterRecursively(key, value, content);
        }
        return true;
    }

    @Override
    public final boolean unregister(T value) {
        Objects.requireNonNull(value);
        Content<T> holder = registryValues.get(value);
        Preconditions.checkNotNull(holder, "Attempting to remove value which does not exists in registry");
        NamespacedKey key = holder.getKey();
        Preconditions.checkNotNull(key, "Attempting to remove key " + key + " which does not exists in registry");
        // invoke unregister
        if (holder instanceof RegistryContent<T> impl) {
            impl.unregister(this);
        }

        // if this value is from child registry
        if (!Objects.equals(key.getNamespace(), namespace())) {
            unregisterInChildNamespace(key, value, holder);
        }
        boolean val = unregisterRecursively(key, value, holder);
        // return the value to the contentMap
        returnedRegistryContentToTheMap(value, holder);
        return val;
    }

    @Override
    public Content<T> createRegistryContent(String rawName, T newValue) {
        if (owner != null) {
            // only the root registry can create content
            return (Content<T>) owner.createRegistryContent(rawName, newValue);
        }
        if (!restrict) {
            throw new IllegalStateException("This registry is not strict and can not create registry content");
        } else {
            validateWrite();
            return this.restrictlyContentMap.computeIfAbsent(
                    newValue, newV -> (Content<T>) new RegistryContent<>(rawName, newV));
        }
    }

    protected void returnedRegistryContentToTheMap(T value, Content<T> content) {
        if (!restrict) {
            return;
        } else {
            if (this.owner instanceof RegistryImpl impl) {
                impl.returnedRegistryContentToTheMap(value, content);
            } else {
                this.restrictlyContentMap.put(value, content);
            }
        }
    }

    protected void onlyClearRecursivelyAndDoNotUnregister() {
        this.registryValues.clear();
        this.byIds.clear();
        this.byNsk.clear();
        this.owner = null;
    }

    // ***************************************************** freeze and unfreeze
    @Override
    public Registry<T> freezeView() {
        freeze = true;
        return this;
    }

    @Override
    public void unfreeze() {
        freeze = false;
    }
}
