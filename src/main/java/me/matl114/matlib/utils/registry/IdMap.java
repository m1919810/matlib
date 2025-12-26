package me.matl114.matlib.utils.registry;

import java.util.Collection;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;

/**
 * Interface providing ID-based lookup functionality for registry components.
 * This interface allows retrieving values by their string identifiers and
 * provides utility methods for ID management.
 *
 * <p>The IdMap interface is implemented by Registry to provide ID-based access
 * to registered content alongside NamespacedKey-based access.</p>
 *
 * @param <T> The type of values stored in this ID map
 */
public interface IdMap<T> {
    /**
     * Retrieves a value by its string identifier.
     * The identifier should be in the format "namespace:key".
     *
     * @param id The string identifier of the value to retrieve
     * @return The value associated with the given ID, or null if not found
     */
    @Nullable T getById(String id);

    /**
     * Retrieves a value by its string identifier, returning an Optional.
     * This is a convenience method that wraps getById() in an Optional.
     *
     * @param id The string identifier of the value to retrieve
     * @return An Optional containing the value if found, empty otherwise
     */
    @Nonnull
    default Optional<T> getByIdOptional(String id) {
        return Optional.ofNullable(getById(id));
    }

    /**
     * Gets the string identifier for a given value.
     *
     * @param value The value to get the identifier for
     * @return The string identifier of the value, or null if not found
     */
    @Nullable String getId(T value);

    /**
     * Gets the string identifier for a given value, returning an Optional.
     * This is a convenience method that wraps getId() in an Optional.
     *
     * @param value The value to get the identifier for
     * @return An Optional containing the identifier if found, empty otherwise
     */
    @Nonnull
    default Optional<String> getOptionalId(T value) {
        return Optional.ofNullable(getId(value));
    }

    /**
     * Gets a collection of all string identifiers in this map.
     *
     * @return A collection containing all string identifiers
     */
    Collection<String> idSet();

    /**
     * Checks if this map contains a value with the given string identifier.
     *
     * @param id The string identifier to check
     * @return true if the identifier exists in this map, false otherwise
     */
    default boolean containsId(String id) {
        return idSet().contains(id);
    }
}
