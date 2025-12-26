package me.matl114.matlib.utils.persistentDataContainer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

/**
 * Utility class for working with Bukkit's PersistentDataContainer system.
 * This class provides convenience methods for common operations on persistent data containers,
 * including tag management, default value handling, and conditional setting/removal.
 */
public class PdcUtils {

    /**
     * Gets an existing tag container or creates a new one if it doesn't exist.
     * This method checks if a tag container exists at the specified key and returns it.
     * If no tag container exists, a new empty one is created and returned.
     *
     * @param container The persistent data container to operate on
     * @param key The NamespacedKey where the tag container should be located
     * @return An existing tag container or a new empty one
     */
    @Nonnull
    public static PersistentDataContainer getOrCreateTag(PersistentDataContainer container, NamespacedKey key) {
        PersistentDataContainer re = null;
        if (container.has(key, PersistentDataType.TAG_CONTAINER)) {
            re = container.get(key, PersistentDataType.TAG_CONTAINER);
        }
        if (re == null) {
            re = container.getAdapterContext().newPersistentDataContainer();
        }
        return re;
    }

    /**
     * Gets a tag container from the specified key, or null if it doesn't exist.
     * This method is a simple wrapper around the container's get method for tag containers.
     *
     * @param container The persistent data container to get the tag from
     * @param key The NamespacedKey where the tag container is located
     * @return The tag container at the specified key, or null if not found
     */
    @Nullable public static PersistentDataContainer getTag(PersistentDataContainer container, NamespacedKey key) {
        return container.get(key, PersistentDataType.TAG_CONTAINER);
    }

    /**
     * Sets a tag container at the specified key or removes the key if the tag is empty.
     * If the provided tag container has no keys, the key is removed from the container.
     * Otherwise, the tag container is set at the specified key.
     *
     * @param container The persistent data container to modify
     * @param key The NamespacedKey where the tag should be set
     * @param tag The tag container to set, or null to remove
     */
    public static void setTagOrRemove(
            PersistentDataContainer container, NamespacedKey key, PersistentDataContainer tag) {
        if (tag.getKeys().isEmpty()) {
            container.remove(key);
        } else {
            container.set(key, PersistentDataType.TAG_CONTAINER, tag);
        }
    }

    /**
     * Gets a value from the container with a default fallback.
     * This method checks if the container has a value at the specified key and returns it.
     * If the value doesn't exist or the container is null, the default value is returned.
     *
     * @param <T> The type of the value to retrieve
     * @param <W> The primitive type used by the PersistentDataType
     * @param container The persistent data container to get the value from
     * @param key The NamespacedKey where the value is located
     * @param type The PersistentDataType for the value
     * @param defaultValue The default value to return if the value doesn't exist
     * @return The value at the specified key, or the default value if not found
     */
    public static <T extends Object, W> T getOrDefault(
            PersistentDataContainer container, NamespacedKey key, PersistentDataType<W, T> type, T defaultValue) {
        if (container != null && container.has(key, type)) {
            return container.get(key, type);
        } else {
            return defaultValue;
        }
    }

    /**
     * Sets a value in the container or removes the key if the value is null.
     * If the provided value is not null, it is set at the specified key.
     * If the value is null, the key is removed from the container.
     *
     * @param <T> The type of the value to set
     * @param <W> The primitive type used by the PersistentDataType
     * @param container The persistent data container to modify
     * @param key The NamespacedKey where the value should be set
     * @param type The PersistentDataType for the value
     * @param value The value to set, or null to remove the key
     */
    public static <T extends Object, W> void setOrRemove(
            PersistentDataContainer container, NamespacedKey key, PersistentDataType<W, T> type, T value) {
        if (value != null) {
            container.set(key, type, value);
        } else {
            container.remove(key);
        }
    }
}
