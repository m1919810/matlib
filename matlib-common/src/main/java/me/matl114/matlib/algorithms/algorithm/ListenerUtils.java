package me.matl114.matlib.algorithms.algorithm;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class for working with listener methods and event handling.
 * This class provides methods for discovering and collecting listener methods
 * from classes based on various criteria such as parameter types and annotations.
 */
public class ListenerUtils {

    /**
     * Collects all public listener methods from a class that match specific criteria.
     * This method filters methods based on:
     * <ul>
     *   <li>Not being synthetic or bridge methods</li>
     *   <li>Having exactly one parameter</li>
     *   <li>The parameter type being assignable from the specified argument type</li>
     *   <li>Having the specified annotation</li>
     * </ul>
     *
     * @param listenerClass The class to search for listener methods
     * @param argumentType The expected parameter type for the listener methods
     * @param annotationClass The annotation class that listener methods should have
     * @return A list of methods that match all the specified criteria
     */
    public static List<Method> collectPublicListenerMethods(
            Class<?> listenerClass, Class<?> argumentType, Class annotationClass) {
        return Arrays.stream(listenerClass.getMethods())
                .filter(m -> !m.isSynthetic() && !m.isBridge())
                .filter(m -> m.getParameterCount() == 1)
                .filter(m -> argumentType.isAssignableFrom(m.getParameterTypes()[0]))
                .filter(m -> m.getAnnotation(annotationClass) != null)
                .toList();
    }
}
