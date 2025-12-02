package me.matl114.matlib.algorithms.algorithm;

import com.google.common.primitives.Bytes;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Utility class for collection operations and manipulations.
 * This class provides methods for set operations, map transformations,
 * list mapping, and map comparison operations.
 */
public class CollectionUtils {
    
    /**
     * Computes the intersection of two sets.
     * This method creates a new set containing all elements that are present in both input sets.
     * 
     * @param <T> The type of elements in the sets
     * @param set1 The first set
     * @param set2 The second set
     * @return A new set containing the intersection of the two input sets
     */
    public static <T extends Object> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Set<T> result = new HashSet<>(set1);
        result.retainAll(set2);
        return result;
    }

    /**
     * Creates a map from a set where all values are set to null.
     * This method is useful for initializing maps with keys but no values.
     * 
     * @param <K> The type of keys in the map
     * @param <V> The type of values in the map
     * @param value The set of keys to create the map from
     * @return A new map with the set elements as keys and null as values
     */
    public static <K,V> Map<K,V> mapToNullsFromSet(Set<K> value){
        Map<K,V> map0 = new HashMap<>();
        for (K val: value){
            map0.put(val, null);
        }
        return map0;
    }

    /**
     * Maps elements from an origin list to a target list using a mapping function.
     * This method efficiently updates the target list by either setting existing elements
     * or adding new ones as needed. If the origin list is null or empty, the target list is cleared.
     * 
     * @param <T> The type of elements in the origin list
     * @param <W> The type of elements in the target list
     * @param originList The source list to map from
     * @param mapper The function to transform elements
     * @param target The target list to populate with mapped elements
     */
    public static <T,W> void mapAndSet(List<T> originList, Function<T,W> mapper, @Nonnull List<W> target){
        if(originList == null || originList.isEmpty()){
            target.clear();
        }else {
            int size= target.size();
            int len = originList.size();
            if(size > len){
                for (int i=0; i<len; ++i){
                    target.set(i, mapper.apply(originList.get(i)));
                }
                target.subList(len, size).clear();
            }else {
                for (int i=0; i<size; ++i){
                    target.set(i, mapper.apply(originList.get(i)));
                }
                for (int i= size; i<len;++i){
                    target.add(mapper.apply(originList.get(i)));
                }
            }
        }
    }
    
    /**
     * Merges two maps by copying entries from the source map to the target map.
     * This method performs a deep merge for nested maps. If a key exists in both maps
     * and both values are maps, they are merged recursively. Otherwise, the value from
     * the source map overwrites the target map's value.
     * 
     * @param to The target map to merge into
     * @param from The source map to merge from
     */
    public static void mergeMapNoCopy(Map<?,?> to, Map<?,?> from){
       for (var entry: from.entrySet()){
           Object val = to.get(entry.getKey());
           if(val instanceof Map<?,?> map0){
               if(entry.getValue() instanceof Map<?,?> map1){
                   mergeMapNoCopy(map1, map0);
               }
               else {
                   ((Map.Entry)entry).setValue(map0);
               }
           }else {
               ((Map.Entry)entry).setValue(val);
           }
       }
    }
    
    /**
     * Compares two HashMaps for equality.
     * This method handles null maps and performs a deep comparison of map contents.
     * The comparison removes entries from map2 as it processes them to ensure
     * both maps have the same entries.
     * 
     * @param map1 The first map to compare
     * @param map2 The second map to compare
     * @return true if the maps are equal, false otherwise
     */
    public static boolean compareMap(HashMap map1, HashMap map2){
        if(map1 == map2){return map1==map2;
        }else if(map1==null){return map2==null||map2.isEmpty();}
        else if(map2==null){return map1.isEmpty();}
        else if(map1.size() != map2.size()){return false;}
        else{
            for(Object key : map1.keySet()){
                if(!map2.containsKey(key)){return false;}
                Object val = map1.get(key);
                if(!Objects.equals(val, map2.remove(key))){return false;}
            }
            return map2.isEmpty();
        }
    }

    /**
     * Copied from com.google.common.primitives.Bytes
     * Returns an array containing each value of {@code collection}, converted to a {@code byte} value
     * in the manner of {@link Number#byteValue}.
     *
     * <p>Elements are copied from the argument collection as if by {@code collection.toArray()}.
     * Calling this method is as thread-safe as calling that method.
     *
     * @param collection a collection of {@code Number} instances
     * @return an array containing the same values as {@code collection}, in the same order, converted
     *     to primitives
     * @throws NullPointerException if {@code collection} or any of its elements is null
     * @since 1.0 (parameter was {@code Collection<Byte>} before 12.0)
     */

    public static byte[] toByteArray(Collection<? extends Number> collection){
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        byte[] array = new byte[len];
        for (int i = 0; i < len; i++) {
            // checkNotNull for GWT (do not optimize)
            array[i] = ((Number) checkNotNull(boxedArray[i])).byteValue();
        }
        return array;
    }
    /**
     * Copied from com.google.common.primitives.Ints
     * Returns an array containing each value of {@code collection}, converted to a {@code int} value
     * in the manner of {@link Number#intValue}.
     *
     * <p>Elements are copied from the argument collection as if by {@code collection.toArray()}.
     * Calling this method is as thread-safe as calling that method.
     *
     * @param collection a collection of {@code Number} instances
     * @return an array containing the same values as {@code collection}, in the same order, converted
     *     to primitives
     * @throws NullPointerException if {@code collection} or any of its elements is null
     * @since 1.0 (parameter was {@code Collection<Integer>} before 12.0)
     */

    public static int[] toIntArray(Collection<? extends Number> collection){
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        int[] array = new int[len];
        for (int i = 0; i < len; i++) {
            // checkNotNull for GWT (do not optimize)
            array[i] = ((Number) checkNotNull(boxedArray[i])).intValue();
        }
        return array;
    }
    /**
     * Copied from com.google.common.primitives.Longs
     * Returns an array containing each value of {@code collection}, converted to a {@code long} value
     * in the manner of {@link Number#longValue}.
     *
     * <p>Elements are copied from the argument collection as if by {@code collection.toArray()}.
     * Calling this method is as thread-safe as calling that method.
     *
     * @param collection a collection of {@code Number} instances
     * @return an array containing the same values as {@code collection}, in the same order, converted
     *     to primitives
     * @throws NullPointerException if {@code collection} or any of its elements is null
     * @since 1.0 (parameter was {@code Collection<Long>} before 12.0)
     */

    public static long[] toLongArray(Collection<? extends Number> collection){
        Object[] boxedArray = collection.toArray();
        int len = boxedArray.length;
        long[] array = new long[len];
        for (int i = 0; i < len; i++) {
            // checkNotNull for GWT (do not optimize)
            array[i] = ((Number) checkNotNull(boxedArray[i])).longValue();
        }
        return array;
    }
}
