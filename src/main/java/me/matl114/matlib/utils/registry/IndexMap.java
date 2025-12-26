package me.matl114.matlib.utils.registry;

import me.matl114.matlib.common.lang.annotations.Internal;

/**
 * Internal interface providing index-based access to registry content.
 * This interface allows retrieving Content objects by their numerical index
 * and getting the index of Content objects within the registry.
 *
 * <p>This interface is marked as internal and is primarily used by the
 * registry implementation for efficient indexing and iteration.</p>
 *
 * @param <T> The type of values stored in the index map
 */
@Internal
public interface IndexMap<T> {
    /**
     * Gets the Content object at the specified index.
     *
     * @param index The numerical index of the Content to retrieve
     * @return The Content object at the specified index
     */
    Content<T> getByIndex(int index);

    /**
     * Gets the numerical index of the specified Content object.
     *
     * @param value The Content object to get the index for
     * @return The numerical index of the Content object
     */
    int getIndex(Content<T> value);
}
