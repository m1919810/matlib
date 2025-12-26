package me.matl114.matlib.algorithms.algorithm;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * General utility class providing common functional programming operations.
 * This class contains utility methods for null handling, conditional execution,
 * and value computation with fallbacks.
 */
public class Utils {

    /**
     * Executes a runnable if the given value is null, then returns the value.
     * This method is useful for performing side effects when a value is null
     * while preserving the original value for further processing.
     *
     * @param <T> The type of the value
     * @param val The value to check for null
     * @param function The runnable to execute if the value is null
     * @return The original value (may be null)
     */
    public static <T extends Object> T runIfNull(T val, Runnable function) {
        if (val == null) function.run();
        return val;
    }

    /**
     * Applies a function to a value if it is not null, otherwise returns null.
     * This method provides a safe way to transform values without null pointer exceptions.
     *
     * @param <T> The type of the input value
     * @param <R> The type of the result
     * @param val The value to transform
     * @param function The function to apply to the value
     * @return The result of applying the function, or null if the input was null
     */
    public static <T, R extends Object> R computeIfPresent(T val, Function<T, R> function) {
        if (val == null) {
            return null;
        } else {
            return function.apply(val);
        }
    }

    /**
     * Applies multiple functions to a value until one returns a non-null result.
     * This method tries each function in sequence and returns the first non-null result.
     * If all functions return null, the last result (null) is returned.
     *
     * @param <T> The type of the input value
     * @param <R> The type of the result
     * @param val The value to transform
     * @param function The functions to try in sequence
     * @return The first non-null result, or null if all functions return null
     */
    public static <T, R extends Object> R computeTilPresent(T val, Function<T, R>... function) {
        R ret = null;
        for (Function<T, R> f : function) {
            ret = f.apply(val);
            if (ret != null) {
                return ret;
            }
        }
        return ret;
    }

    /**
     * Returns the given value if it is not null, otherwise returns the default value.
     * This method provides a simple way to provide fallback values for null inputs.
     *
     * @param <T> The type of the value
     * @param val The value to check
     * @param defaultVal The default value to return if val is null
     * @return The original value if not null, otherwise the default value
     */
    public static <T extends Object> T orDefault(T val, T defaultVal) {
        if (val == null) {
            return defaultVal;
        } else {
            return val;
        }
    }

    /**
     * Returns the given value if it is not null, otherwise computes and returns a default value.
     * This method is similar to orDefault but uses a supplier to compute the default value
     * only when needed, which can be more efficient for expensive default value computations.
     *
     * @param <T> The type of the value
     * @param val The value to check
     * @param supplier The supplier to compute the default value if val is null
     * @return The original value if not null, otherwise the computed default value
     */
    public static <T extends Object> T orCompute(T val, Supplier<T> supplier) {
        if (val == null) {
            return supplier.get();
        } else {
            return val;
        }
    }
}
