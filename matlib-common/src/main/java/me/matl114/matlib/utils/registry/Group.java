package me.matl114.matlib.utils.registry;

import javax.annotation.Nonnull;

/**
 * Represents a group of related registry content that can be categorized together.
 * Groups provide a way to organize and categorize registered values within a registry,
 * similar to tags in Minecraft's registry system.
 *
 * <p>Groups can contain multiple Content objects and provide methods to check
 * membership and iterate over contained content.</p>
 *
 * @param <T> The type of values that can be contained in this group
 */
public interface Group<T> extends Iterable<Content<T>>, Content<Group<T>>, IdHolder {
    /**
     * Gets this Group as its own value.
     * This method is provided for consistency with the Content interface.
     *
     * @return This Group object
     */
    @Nonnull
    default Group<T> value() {
        return this;
    }

    /**
     * Checks if this Group contains the specified Content object.
     * This method delegates to contains() with the Content's value.
     *
     * @param value The Content object to check
     * @return true if this Group contains the Content, false otherwise
     */
    default boolean containContent(Content<T> value) {
        return contains(value.value());
    }

    /**
     * Checks if this Group contains the specified value.
     *
     * @param value The value to check for membership
     * @return true if this Group contains the value, false otherwise
     */
    boolean contains(T value);

    /**
     * Gets the registry that this Group belongs to.
     *
     * @return The registry that owns this Group
     */
    Registry<T> type();
}
