package me.matl114.matlib.utils.registry.impl;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.registry.Content;
import me.matl114.matlib.utils.registry.Group;
import me.matl114.matlib.utils.registry.Registry;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Accessors(fluent = true)
public final class RegistryContent<T> implements Content<T> {
    Registry<? super T> owner;

    @Nullable(value = "when not register") String fullId;

    @Nonnull
    final String keyId;

    @Nullable(value = "when not register") String namespace;

    @Nullable(value = "when not register") NamespacedKey nsKey;

    final WeakReference<T> valueRefenrece;
    T value;

    @Setter(AccessLevel.PACKAGE)
    @Getter
    boolean onRegister;

    @Setter
    Set<Group<T>> tags = Set.of();

    RegistryContent(Registry<T> owner, NamespacedKey key, T value) {
        this.owner = owner;
        this.fullId = key.toString();
        this.namespace = key.getNamespace();
        this.keyId = key.getKey();
        this.nsKey = key;
        this.valueRefenrece = new WeakReference<>(value);
        this.onRegister = true;
    }

    void registerToOwner(Registry<? super T> owner) {
        if (this.owner == owner) return;
        Preconditions.checkArgument(!onRegister, "This registry content already has a owner");
        this.onRegister = true;
        this.owner = owner;
        this.namespace = owner.namespace();
        this.nsKey = new NamespacedKey(this.namespace, this.keyId);
        this.fullId = this.nsKey.toString();
        this.value = Objects.requireNonNull(this.valueRefenrece.get());
    }

    void unregister(Registry<? super T> owner) {
        Preconditions.checkArgument(onRegister, "This registry hasn't been registered");
        Preconditions.checkArgument(this.isIn(owner));
        this.onRegister = false;
        this.owner = null;
        this.namespace = null;
        this.nsKey = null;
        this.fullId = null;
        this.value = null;
    }

    @Note("create via outer access")
    public RegistryContent(String name, T value) {
        this.keyId = name;
        this.valueRefenrece = new WeakReference<>(value);
    }

    @NotNull @Override
    public T value() {
        return this.value;
    }

    @Override
    public Registry<? super T> owner() {
        return (Registry<? super T>) this.owner;
    }

    @Override
    public boolean isIn(Registry<?> registry) {
        return ((Registry) registry).containsValue(this);
    }

    @Override
    public boolean is(NamespacedKey key) {
        return this.nsKey.equals(key);
    }

    @Override
    public boolean is(String key) {
        return this.fullId.equals(key);
    }

    @Override
    public Stream<Group<T>> groups() {
        return tags.stream();
    }

    @Override
    public String namespace() {
        return namespace;
    }

    @Override
    public String keyStr() {
        return keyId;
    }

    public NamespacedKey getKey() {
        return this.nsKey;
    }

    public String getId() {
        return this.fullId;
    }

    public String toString() {
        return onRegister
                ? ("Content{reg= " + owner.getId() + ",id="
                        + this.owner.getOptionalId(value()).orElse(null) + "}")
                : ("Content{UNREGISTERED, id= " + this.keyId + "}");
    }
}
