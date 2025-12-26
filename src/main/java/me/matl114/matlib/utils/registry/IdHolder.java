package me.matl114.matlib.utils.registry;

import org.bukkit.Keyed;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

/**
 * Base interface for all registry components that have a namespaced identifier.
 * This interface extends Bukkit's Keyed interface and provides methods for accessing
 * the namespace, key, and full identifier of registry components.
 *
 * <p>All registry components (Registry, Content, Group) implement this interface
 * to ensure they have consistent identifier handling.</p>
 *
 * @param <T> The type parameter is not used in this interface but is maintained for consistency
 */
public interface IdHolder extends Keyed {
    /**
     * Gets the namespace of this registry component.
     * The namespace is typically the plugin name or a domain identifier.
     *
     * @return The namespace string
     */
    String namespace();

    /**
     * Gets the key string (without namespace) of this registry component.
     * This is the local identifier within the namespace.
     *
     * @return The key string
     */
    String keyStr();

    /**
     * Gets the full NamespacedKey of this registry component.
     * This combines the namespace and key into a single identifier.
     *
     * @return The NamespacedKey representing this component
     */
    @Override
    @NotNull NamespacedKey getKey();

    /**
     * Gets the full identifier string in the format "namespace:key".
     * This is equivalent to calling getKey().toString().
     *
     * @return The full identifier string
     */
    String getId();
}
