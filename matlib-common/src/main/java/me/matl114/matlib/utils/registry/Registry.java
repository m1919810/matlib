package me.matl114.matlib.utils.registry;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import me.matl114.matlib.common.lang.annotations.Note;
import net.kyori.adventure.key.Namespaced;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Core interface for a registry system that manages named values with namespaced identifiers.
 * A Registry provides functionality to register, unregister, and retrieve values by their
 * NamespacedKey or string identifiers.
 *
 * <p>The Registry interface combines functionality from multiple interfaces:
 * <ul>
 *   <li>{@link IdHolder} - Provides namespace and key management</li>
 *   <li>{@link Namespaced} - Provides namespaced key functionality</li>
 *   <li>{@link IdMap} - Provides ID-based lookup</li>
 *   <li>{@link Iterable} - Allows iteration over registered values</li>
 * </ul></p>
 *
 * <p>Registries can have parent registries, allowing for hierarchical organization
 * of registered content. They also support grouping and content management.</p>
 *
 * @param <T> The type of values that can be registered in this registry
 */
public interface Registry<T> extends IdHolder, Namespaced, IdMap<T>, Iterable<T> {
    /**
     * Gets the parent registry of this registry, if any.
     * Parent registries allow for hierarchical organization of registries.
     *
     * @return The parent registry, or null if this is a root registry
     */
    @Nullable Registry<? super T> owner();

    /**
     * Gets the NamespacedKey associated with the specified value.
     *
     * @param value The value to get the key for
     * @return The NamespacedKey of the value, or null if not found
     */
    @Nullable NamespacedKey getKey(T value);

    @Nullable Content<T> getContentByKey(NamespacedKey key);

    @Nullable Content<T> getContentById(String key);

    /**
     * Gets the Content object associated with the specified value.
     *
     * @param value The value to get the Content for
     * @return The Content object, or null if not found
     */
    @Nullable Content<T> getContent(T value);

    /**
     * Gets a value by its NamespacedKey.
     *
     * @param key The NamespacedKey to look up
     * @return The value associated with the key, or null if not found
     */
    @Nullable T getByKey(NamespacedKey key);

    /**
     * Gets a value by its NamespacedKey, returning an Optional.
     * This is a convenience method that wraps getByKey() in an Optional.
     *
     * @param key The NamespacedKey to look up
     * @return An Optional containing the value if found, empty otherwise
     */
    default Optional<T> getByKeyOptional(NamespacedKey key) {
        return Optional.ofNullable(getByKey(key));
    }

    /**
     * Gets a collection of all NamespacedKeys in this registry.
     *
     * @return A collection of all NamespacedKeys
     */
    Collection<NamespacedKey> keySet();

    /**
     * Gets a collection of all key-value pairs in this registry.
     * Each entry contains a NamespacedKey and its associated Content object.
     *
     * @return A collection of Map.Entry objects
     */
    Collection<Map.Entry<NamespacedKey, Content<T>>> entrySet();

    /**
     * Gets a collection of all registered values in this registry.
     *
     * @return A collection of all registered values
     */
    Collection<T> contents();

    /**
     * Gets an iterator over all registered values in this registry.
     *
     * @return An iterator of registered values
     */
    @NotNull Iterator<T> iterator();

    /**
     * Gets a stream of values that belong to the specified group.
     *
     * @param group The group to filter by
     * @return A stream of values that are members of the group
     */
    Stream<T> getByGroup(Group<T> group);

    /**
     * Gets an iterable view of all Content objects in this registry.
     *
     * @return An iterable of Content objects
     */
    Iterable<Content<T>> asContentSet();

    /**
     * Registers a value using this registry's namespace and the specified raw name.
     * The full key will be constructed as "this.namespace:rawName".
     *
     * @param rawName The raw name (key) for the value
     * @param value The value to register
     * @return The Content object representing the registered value
     */
    @Note("using this namespace as namespace")
    Content<T> registerThis(String rawName, T value);

    /**
     * Registers a value with the specified NamespacedKey.
     *
     * @param namespacedKey The NamespacedKey to register the value under
     * @param value The value to register
     * @return The Content object representing the registered value
     */
    Content<T> register(NamespacedKey namespacedKey, T value);

    /**
     * Unregisters a value by its string identifier.
     * This is a convenience method that looks up the value by ID and then unregisters it.
     *
     * @param id The string identifier of the value to unregister
     * @return true if the value was successfully unregistered, false otherwise
     */
    default boolean unregisterThis(String id) {
        return getByIdOptional(id).map((Function<T, Boolean>) this::unregister).orElse(false);
    }

    /**
     * Unregisters the specified value from this registry.
     *
     * @param value The value to unregister
     * @return true if the value was successfully unregistered, false otherwise
     */
    boolean unregister(T value);

    /**
     * Unregisters a value by its NamespacedKey.
     * This is a convenience method that looks up the value by key and then unregisters it.
     *
     * @param namespacedKey The NamespacedKey of the value to unregister
     * @return true if the value was successfully unregistered, false otherwise
     */
    default boolean unregister(NamespacedKey namespacedKey) {
        return getByKeyOptional(namespacedKey)
                .map((Function<T, Boolean>) this::unregister)
                .orElse(false);
    }

    /**
     * Freezes this registry, preventing further modifications.
     * After freezing, registration and unregistration operations will throw exceptions.
     *
     * @return This registry (for method chaining)
     */
    Registry<T> freezeView();

    /**
     * Unfreezes this registry, allowing modifications again.
     * This method reverses the effect of freezeView().
     */
    void unfreeze();

    /**
     * Checks if this registry contains a value with the specified NamespacedKey.
     *
     * @param key The NamespacedKey to check
     * @return true if the key exists in this registry, false otherwise
     */
    boolean containsKey(NamespacedKey key);

    /**
     * Checks if this registry contains the specified Content object.
     *
     * @param value The Content object to check
     * @return true if the Content is in this registry, false otherwise
     */
    boolean containsValue(Content<T> value);

    /**
     * Creates a new Content object for the specified value.
     * This method is typically used in restricted registries where Content objects
     * must be pre-created before registration.
     *
     * @param value The raw name for the Content
     * @param newValue The value to create Content for
     * @return A new Content object
     */
    Content<T> createRegistryContent(String value, T newValue);

    /**
     * Casts this registry to a different type.
     * This method provides a convenient way to cast registries when the type
     * relationship is known to be safe.
     *
     * @param <R> The target type
     * @param <W> The target registry type
     * @return This registry cast to the target type
     */
    default <R, W extends Registry<R>> W cast() {
        return (W) this;
    }
}
