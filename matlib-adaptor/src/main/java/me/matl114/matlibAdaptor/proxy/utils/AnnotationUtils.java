package me.matl114.matlibAdaptor.proxy.utils;

import com.google.common.base.Preconditions;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

public class AnnotationUtils {
    public static String ADAPTOR_ANNOTATION_IDENTIFIER = "AdaptorInterface";
    public static String INTERNEL_ANNOTATION_IDENTIFIER = "InternalMethod";
    public static String DEFAULT_ANNOTATION_IDENTIFIER = "DefaultMethod";

    @Nonnull
    public static Optional<Annotation> getAdaptorInstance(Class<?> interfaceClass) {
        Preconditions.checkArgument(interfaceClass.isInterface(), "Argument is not an interface");
        Annotation[] annotations = interfaceClass.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().endsWith(ADAPTOR_ANNOTATION_IDENTIFIER)) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }

    @Nonnull
    public static Set<Method> getAdaptedMethods(Class<?> interfaceClass) {
        // fixme should not collect multiple method if override
        Preconditions.checkArgument(interfaceClass.isInterface(), "Argument is not an interface");
        // Set<Method> methods = new HashSet<>();
        return Arrays.stream(interfaceClass.getMethods())
                .filter(method -> !Arrays.stream(method.getAnnotations())
                        .anyMatch(a -> a.annotationType().getName().endsWith(INTERNEL_ANNOTATION_IDENTIFIER)))
                .collect(Collectors.toSet());
        // collectMethods0(interfaceClass, methods);
        // remove all internal method
        //        methods.removeIf(method ->
        // Arrays.stream(method.getAnnotations()).anyMatch(a->a.annotationType().getName().endsWith(
        // INTERNEL_ANNOTATION_IDENTIFIER )));
        //        return methods;
    }

    public static Optional<Annotation> getDefaultAnnotation(Method method) {
        Annotation[] annotations = method.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getName().endsWith(DEFAULT_ANNOTATION_IDENTIFIER)) {
                return Optional.of(annotation);
            }
        }
        return Optional.empty();
    }

    private static void collectMethods0(Class<?> clazz, Collection<Method> methods) {
        Arrays.stream(clazz.getInterfaces()).forEach(i -> collectMethods0(i, methods));
        // only collect public and default method
        methods.addAll(Arrays.asList(clazz.getMethods()));
    }

    private static void collectInterface0(Class<?> clazz, Collection<Class> methods) {
        Arrays.stream(clazz.getInterfaces()).forEach(i -> collectInterface0(i, methods));
        if (clazz.isInterface()) {
            methods.add(clazz);
        } else {
            clazz = clazz.getSuperclass();
            if (clazz != null) {
                collectInterface0(clazz, methods);
            }
        }
    }

    public static Class<?> getTargetInterface(Class<?> orginClass, String adaptorSimpleName) {
        Deque<Class<?>> queue = new ArrayDeque<>();
        queue.add(orginClass);
        while (!queue.isEmpty()) {
            Class<?> clazz = queue.poll();
            if (clazz.isInterface()) {
                if (clazz.getSimpleName().equals(adaptorSimpleName)) {
                    return clazz;
                }
            } else {
                // add at last,check later
                Class<?> superClass = clazz.getSuperclass();
                if (superClass != null) {
                    queue.add(superClass);
                }
            }
            // check interfaces first
            for (Class<?> iface : clazz.getInterfaces()) {
                queue.addFirst(iface);
            }
        }
        return null;
    }
}
