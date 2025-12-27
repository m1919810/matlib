package me.matl114.matlib.utils.registry;

import java.util.stream.Stream;
import javax.annotation.Nonnull;
import me.matl114.matlib.utils.registry.impl.ContentImpl;
import org.bukkit.NamespacedKey;

/**
 * Represents a registered value in a registry with its associated metadata.
 * Content objects wrap registered values and provide access to their registry
 * information, including owner registry, namespace, and group associations.
 *
 * <p>Content objects are created when values are registered in a registry and
 * provide a way to access both the value and its registry metadata.</p>
 *
 * @param <T> The type of the registered value
 */
public interface Content<T> extends IdHolder {
    /**
     * Gets the registered value wrapped by this Content object.
     *
     * @return The registered value
     */
    @Nonnull
    T value();

    /**
     * Gets the registry that owns this Content object.
     *
     * @return The owner registry, or null if not registered
     */
    Registry<? super T> owner();

    /**
     * Gets the namespace of this Content object.
     * This is a convenience method that delegates to the owner registry.
     *
     * @return The namespace string
     */
    default String getNamespace() {
        return owner().namespace();
    }

    /**
     * Checks if this Content object is registered in the specified registry.
     *
     * @param registry The registry to check
     * @return true if this Content is registered in the given registry, false otherwise
     */
    boolean isIn(Registry<?> registry);

    /**
     * Checks if this Content object has the specified NamespacedKey.
     *
     * @param key The NamespacedKey to check against
     * @return true if this Content has the given key, false otherwise
     */
    boolean is(NamespacedKey key);

    /**
     * Checks if this Content object has the specified string identifier.
     *
     * @param key The string identifier to check against
     * @return true if this Content has the given key, false otherwise
     */
    boolean is(String key);

    /**
     * Checks if this Content object represents the same value as another Content.
     * This method compares the actual values, not the Content objects themselves.
     *
     * @param key The Content object to compare with
     * @return true if both Content objects wrap the same value, false otherwise
     */
    default boolean is(Content<?> key) {
        return value() == key.value();
    }

    /**
     * Checks if this Content object is contained in the specified group.
     *
     * @param group The group to check
     * @return true if this Content is in the given group, false otherwise
     */
    default boolean isIn(Group<T> group) {
        return group.containContent(this);
    }

    /**
     * Gets a stream of all groups that contain this Content object.
     *
     * @return A stream of Group objects that contain this Content
     */
    Stream<Group<T>> groups();

    /**
     * Creates a common Content object with the specified value, namespace, and ID.
     * This factory method creates a Content object that is not associated with any registry.
     *
     * @param <W> The type of the value
     * @param va The value to wrap
     * @param namespace The namespace for the Content
     * @param id The ID for the Content
     * @return A new Content object
     */
    static <W> Content<W> common(W va, String namespace, String id) {
        return new ContentImpl<>(va, namespace, id);
    }
}
