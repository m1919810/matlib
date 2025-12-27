package me.matl114.matlib.utils.registry.impl;

import java.util.stream.Stream;
import me.matl114.matlib.utils.registry.Content;
import me.matl114.matlib.utils.registry.Group;
import me.matl114.matlib.utils.registry.Registry;
import org.bukkit.NamespacedKey;

public record ContentImpl<T>(T value, String namespace, String id) implements Content<T> {
    @Override
    public Registry<T> owner() {
        return null;
    }

    @Override
    public boolean isIn(Registry<?> registry) {
        return false;
    }

    @Override
    public boolean is(NamespacedKey key) {
        return false;
    }

    @Override
    public boolean is(String key) {
        return false;
    }

    @Override
    public Stream<Group<T>> groups() {
        return Stream.empty();
    }

    @Override
    public String keyStr() {
        return id;
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(namespace, id);
    }

    @Override
    public String getId() {
        return namespace + ":" + id;
    }
}
