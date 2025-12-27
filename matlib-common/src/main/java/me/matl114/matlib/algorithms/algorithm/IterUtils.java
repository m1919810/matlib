package me.matl114.matlib.algorithms.algorithm;

import java.util.Iterator;
import java.util.function.Function;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.MappingIterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Zipping2Iterator;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Zipping3Iterator;
import me.matl114.matlib.algorithms.dataStructures.struct.IndexEntry;
import me.matl114.matlib.algorithms.dataStructures.struct.IndexFastEntry;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.algorithms.dataStructures.struct.Triplet;
import org.jetbrains.annotations.NotNull;

/**
 * Utility class for iterator operations and functional programming with iterables.
 * This class provides methods for enumerating iterables with indices, zipping multiple
 * iterables together, and stream operations.
 */
public class IterUtils {

    /**
     * Creates an iterable that provides both index and value for each element.
     * This method creates a new IndexEntry for each element, which can be memory-intensive
     * for large collections.
     *
     * @param <T> The type of elements in the iterable
     * @param val The iterable to enumerate
     * @return An iterable of IndexEntry objects containing index and value pairs
     */
    public static <T> Iterable<IndexEntry<T>> enumerate(Iterable<T> val) {
        return new Iterable<IndexEntry<T>>() {
            @NotNull @Override
            public Iterator<IndexEntry<T>> iterator() {
                return new MappingIterator<>(val.iterator(), new Function<T, IndexEntry<T>>() {
                    int index;

                    @Override
                    public IndexEntry<T> apply(T t) {
                        return new IndexFastEntry<>(index++, t);
                    }
                });
            }
        };
    }

    /**
     * Creates an iterable that provides both index and value for each element with optimized memory usage.
     * This method reuses a single mutable IndexFastEntry object, making it more memory-efficient
     * than the regular enumerate method.
     *
     * @param <T> The type of elements in the iterable
     * @param val The iterable to enumerate
     * @return An iterable of IndexEntry objects containing index and value pairs
     */
    public static <T> Iterable<IndexEntry<T>> fastEnumerate(Iterable<T> val) {
        return () -> {
            IndexFastEntry<T> mutableEntry = new IndexFastEntry<>(-1, null);
            return new MappingIterator<>(val.iterator(), (k) -> {
                mutableEntry.addIndex();
                mutableEntry.setValue(k);
                return mutableEntry;
            });
        };
    }

    /**
     * Zips two iterables together, creating pairs of elements from both iterables.
     * The resulting iterable will have the length of the shorter input iterable.
     *
     * @param <T> The type of elements in the first iterable
     * @param <W> The type of elements in the second iterable
     * @param val1 The first iterable
     * @param val2 The second iterable
     * @return An iterable of Pair objects containing elements from both iterables
     */
    public static <T, W> Iterable<Pair<T, W>> zip(Iterable<T> val1, Iterable<W> val2) {
        return () -> {
            return new Zipping2Iterator<>(val1.iterator(), val2.iterator());
        };
    }

    /**
     * Zips two iterables together with optimized memory usage.
     * This method reuses a single mutable Pair object, making it more memory-efficient
     * than the regular zip method.
     *
     * @param <T> The type of elements in the first iterable
     * @param <W> The type of elements in the second iterable
     * @param val1 The first iterable
     * @param val2 The second iterable
     * @return An iterable of Pair objects containing elements from both iterables
     */
    public static <T, W> Iterable<Pair<T, W>> fastZip(Iterable<T> val1, Iterable<W> val2) {
        return () -> {
            Pair<T, W> pair = Pair.of(null, null);
            return new Zipping2Iterator<>(val1.iterator(), val2.iterator()) {
                @Override
                public Pair<T, W> next() {
                    pair.setA(a != null ? a.next() : null);
                    pair.setB(b != null ? b.next() : null);
                    return pair;
                }
            };
        };
    }

    /**
     * Zips three iterables together, creating triplets of elements from all three iterables.
     * The resulting iterable will have the length of the shortest input iterable.
     *
     * @param <T> The type of elements in the first iterable
     * @param <W> The type of elements in the second iterable
     * @param <R> The type of elements in the third iterable
     * @param val1 The first iterable
     * @param val2 The second iterable
     * @param val3 The third iterable
     * @return An iterable of Triplet objects containing elements from all three iterables
     */
    public static <T, W, R> Iterable<Triplet<T, W, R>> zip(Iterable<T> val1, Iterable<W> val2, Iterable<R> val3) {
        return () -> {
            return new Zipping3Iterator<>(val1.iterator(), val2.iterator(), val3.iterator());
        };
    }

    /**
     * Zips three iterables together with optimized memory usage.
     * This method reuses a single mutable Triplet object, making it more memory-efficient
     * than the regular zip method.
     *
     * @param <T> The type of elements in the first iterable
     * @param <W> The type of elements in the second iterable
     * @param <R> The type of elements in the third iterable
     * @param val1 The first iterable
     * @param val2 The second iterable
     * @param val3 The third iterable
     * @return An iterable of Triplet objects containing elements from all three iterables
     */
    public static <T, W, R> Iterable<Triplet<T, W, R>> fastZip(Iterable<T> val1, Iterable<W> val2, Iterable<R> val3) {
        return () -> {
            Triplet<T, W, R> pair = Triplet.of(null, null, null);
            return new Zipping3Iterator<>(val1.iterator(), val2.iterator(), val3.iterator()) {
                @Override
                public Triplet<T, W, R> next() {
                    pair.setA(a != null ? a.next() : null);
                    pair.setB(b != null ? b.next() : null);
                    pair.setC(c != null ? c.next() : null);
                    return pair;
                }
            };
        };
    }

    /**
     * Concatenates two streams into a single stream.
     * This is a convenience method that delegates to Stream.concat().
     *
     * @param <T> The type of elements in the streams
     * @param a The first stream
     * @param b The second stream
     * @return A stream containing all elements from both input streams
     */
    public static <T> Stream<T> append(Stream<T> a, Stream<T> b) {
        return Stream.concat(a, b);
    }
}
