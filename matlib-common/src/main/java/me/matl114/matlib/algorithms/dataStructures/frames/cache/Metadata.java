package me.matl114.matlib.algorithms.dataStructures.frames.cache;

import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nonnull;
import org.bukkit.NamespacedKey;

@SuppressWarnings("all")
public class Metadata extends ConcurrentHashMap<String, Object> {
    public Metadata() {
        super(3);
    }

    public <T> T getData(NamespacedKey key) {
        return (T) get(key.toString());
    }

    public <T> T getDataOrSet(NamespacedKey key, @Nonnull T value) {
        return (T) this.compute(key.toString(), (keyStr, val) -> {
            Class cls = value.getClass();
            if (cls.isInstance(val)) {
                return val;
            } else {
                return value;
            }
        });
    }

    public <T> T getDataOrDefault(NamespacedKey key, @Nonnull T value) {
        return (T) getOrDefault(key.toString(), value);
    }

    public <T> void putData(NamespacedKey key, T value) {
        put(key.toString(), value);
    }

    public <T> T removeData(NamespacedKey key) {
        return (T) remove(key.toString());
    }
}
