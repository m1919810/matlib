package me.matl114.matlib.utils.registry;

import java.util.Set;
import net.kyori.adventure.key.Namespaced;

/**
 * Extended registry interface that supports hierarchical organization through sub-registries.
 * A NamespacedRegistry can contain multiple sub-registries, each associated with a specific
 * namespace holder. This allows for organizing registry content by different namespaces
 * while maintaining a unified view of all content.
 *
 * <p>Sub-registries inherit the type and restrictions of their parent registry but
 * operate within their own namespace context.</p>
 *
 * @param <T> The type of values that can be registered in this registry
 * @param <W> The type of namespace holders that can own sub-registries
 */
public interface NamespacedRegistry<T, W extends Namespaced> extends Registry<T> {
    /**
     * Creates a new sub-registry for the specified namespace holder.
     * The sub-registry will inherit the type and restrictions of this registry
     * but operate within the holder's namespace.
     *
     * @param <R> The type parameter for the sub-registry (must extend T)
     * @param holder The namespace holder that will own the sub-registry
     * @return A new sub-registry for the specified holder
     */
    public <R extends T> Registry<R> createSubRegistry(W holder);

    /**
     * Removes the sub-registry associated with the specified namespace holder.
     * This will unregister all content in the sub-registry and remove it from
     * the parent registry's management.
     *
     * @param holder The namespace holder whose sub-registry should be removed
     * @return true if the sub-registry was successfully removed, false otherwise
     */
    public boolean removeSubRegistry(W holder);

    /**
     * Gets the sub-registry for the specified namespace.
     *
     * @param <R> The type parameter for the sub-registry (must extend T)
     * @param namespace The namespace to get the sub-registry for
     * @return The sub-registry for the namespace, or null if not found
     */
    public <R extends T> Registry<R> getSubRegistry(String namespace);

    /**
     * Gets the sub-registry for the specified namespace holder.
     *
     * @param <R> The type parameter for the sub-registry (must extend T)
     * @param holder The namespace holder to get the sub-registry for
     * @return The sub-registry for the holder, or null if not found
     */
    public <R extends T> Registry<R> getSubRegistry(W holder);

    /**
     * Gets a set of all namespace strings that have associated sub-registries.
     *
     * @return A set of namespace strings
     */
    public Set<String> getNamespaces();

    /**
     * Gets a registry view that includes all content from this registry and its sub-registries.
     * This provides a unified view of all registered content across the hierarchy.
     *
     * @param <R> The type parameter for the registry view (must extend T)
     * @return A registry view containing all content from this registry and sub-registries
     */
    public <R extends T> Registry<R> asRegistryView();
}
