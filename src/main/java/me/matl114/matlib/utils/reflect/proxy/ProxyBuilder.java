package me.matl114.matlib.utils.reflect.proxy;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.lang.reflect.*;
import java.util.*;
import java.util.function.Function;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.proxy.invocation.InvocationCreator;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodIndex;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodSignature;
import me.matl114.matlibAdaptor.proxy.utils.AnnotationUtils;

/**
 * Lightweight proxy builder for creating code bridges without ASM.
 * This class provides functionality to create method mappings and build adaptors
 * using Java's built-in reflection and proxy mechanisms.
 *
 * <p>This builder is designed to work with interfaces marked with {@code @AdaptorInterface}
 * and creates efficient method mappings between the target interface and the proxy class.
 * It uses Java's {@link Proxy} class to create dynamic proxies that delegate method calls
 * to the target object.</p>
 *
 * <p>The builder supports both base methods (like toString, hashCode) and custom methods
 * defined in the target interface, with special handling for methods marked with
 * {@code @DefaultMethod} annotation.</p>
 */
@SuppressWarnings("all")
public class ProxyBuilder {

    /**
     * Creates a mapping proxy between a target interface and a proxy class.
     * This method analyzes the target interface and proxy class to establish
     * method correspondences for efficient invocation, with adaptor validation enabled.
     *
     * <p>The mapping includes both base methods (from Object class) and custom methods
     * defined in the target interface. Each method is assigned an index for fast access.</p>
     *
     * @param targetInterface The interface whose methods need to be mapped
     * @param proxyClass The class that will provide the method implementations
     * @return A set of MethodIndex objects containing the method mappings
     * @throws IllegalArgumentException if the target interface is not properly annotated
     *         or if required methods are not found in the proxy class
     */
    public static Set<MethodIndex> createMappingProxy(Class<?> targetInterface, Class<?> proxyClass) {
        return createMappingProxy(targetInterface, proxyClass, true);
    }

    /**
     * Ensures that the adaptor relationship is valid between the target interface and proxy class.
     * This method validates that the proxy class contains a matching interface with the same name
     * as the target interface, and that the interface is properly marked with {@code @AdaptorInterface}.
     *
     * <p>The validation process searches through the proxy class hierarchy (including interfaces
     * and superclasses) to find a matching interface with the same simple name as the target interface.</p>
     *
     * @param targetInterface The target interface to validate
     * @param proxyClass The proxy class to check for the matching interface
     * @throws IllegalArgumentException if no matching interface is found in the proxy class
     * @throws IllegalStateException if the matching interface is not properly annotated
     */
    public static void ensureAdaptor(Class<?> targetInterface, Class<?> proxyClass) {
        String simpleName = targetInterface.getSimpleName();
        var targetClass = AnnotationUtils.getTargetInterface(proxyClass, simpleName);
        if (targetClass == null) {
            throw new IllegalArgumentException(
                    "Illegal Adaptor class!No matching interface present in " + proxyClass + "!");
        }
        Preconditions.checkState(
                AnnotationUtils.getAdaptorInstance(targetClass).isPresent(),
                "Illegal Adaptor class! Interface {0} in invoke target class is not marked as AdaptorInterface!",
                targetClass);
        Preconditions.checkNotNull(targetClass, "Invoke target does not contains Interface named {0}!", simpleName);
    }

    /**
     * Creates a mapping proxy between a target interface and a proxy class.
     * This method analyzes the target interface and proxy class to establish
     * method correspondences for efficient invocation, with optional adaptor validation.
     *
     * <p>The mapping process includes:</p>
     * <ol>
     *   <li>Validation that the target interface is properly annotated with {@code @AdaptorInterface}</li>
     *   <li>Optional validation of the adaptor relationship</li>
     *   <li>Collection of base methods (toString, hashCode, etc.) from the proxy class</li>
     *   <li>Mapping of custom methods from the target interface to the proxy class</li>
     *   <li>Assignment of indices for fast method access</li>
     * </ol>
     *
     * <p>Each method in the resulting set includes:</p>
     * <ul>
     *   <li>The target method from the proxy class</li>
     *   <li>The method signature for identification</li>
     *   <li>An index for fast access</li>
     *   <li>A flag indicating if the method has a default implementation</li>
     * </ul>
     *
     * @param targetInterface The interface whose methods need to be mapped
     * @param proxyClass The class that will provide the method implementations
     * @param ensureAdaptor Whether to validate the adaptor relationship
     * @return A set of MethodIndex objects containing the method mappings
     * @throws IllegalArgumentException if the target interface is not properly annotated
     *         or if required methods are not found in the proxy class
     */
    public static Set<MethodIndex> createMappingProxy(
            Class<?> targetInterface, Class<?> proxyClass, boolean ensureAdaptor) {
        var annotation = AnnotationUtils.getAdaptorInstance(targetInterface);
        Preconditions.checkState(annotation.isPresent(), "Illegal Adaptor class!This is not an AdaptorInterface!");
        if (ensureAdaptor) {
            ensureAdaptor(targetInterface, proxyClass);
        }
        // save the mapping from targetInterface to invokeTarget fastAccess index;
        Set<MethodIndex> methodSnapshot = new ReferenceArraySet<>();
        Set<Method> methodMustRequired = AnnotationUtils.getAdaptedMethods(targetInterface);
        for (Method method : proxyClass.getMethods()) {
            // add base method invoke access
            if (ReflectUtils.isBaseMethod(method)) {
                // require base method like toString and sth
                methodSnapshot.add(new MethodIndex(
                        method, MethodSignature.getSignature(method), ReflectUtils.getBaseMethodIndex(method), false));
            }
        }
        Iterator<Method> iter = methodMustRequired.iterator();
        int indexCnt = 0;
        while (iter.hasNext()) {
            Method m = iter.next();
            try {
                var params = m.getParameterTypes();
                int index = ++indexCnt; // fastAccess.getIndex(m.getName(),params);
                // find matched target
                methodSnapshot.add(new MethodIndex(
                        proxyClass.getMethod(m.getName(), params),
                        MethodSignature.getSignature(m),
                        index,
                        AnnotationUtils.getDefaultAnnotation(m).isPresent()));
            } catch (Throwable e) {
                throw new IllegalArgumentException("Method " + m + " not found in invoke target class! using"
                        + targetInterface + " as Adaptor and " + proxyClass + " as invoke target");
            }
        }
        return methodSnapshot;
    }

    /**
     * Builds a Matlib adaptor instance for the specified interface using the given invoke target.
     * This method creates a dynamic proxy that implements the target interface and delegates
     * method calls to the invoke target object using a custom invocation creator.
     *
     * <p>The method performs the following steps:</p>
     * <ol>
     *   <li>Creates a method mapping between the interface and target class</li>
     *   <li>Uses the provided adaptor builder function to create an InvocationCreator</li>
     *   <li>Binds the invocation creator to the invoke target</li>
     *   <li>Creates a dynamic proxy using Java's Proxy.newProxyInstance</li>
     * </ol>
     *
     * <p>The resulting proxy will handle method invocations according to the logic defined
     * in the InvocationCreator, allowing for custom behavior such as method remapping,
     * caching, or other optimizations.</p>
     *
     * @param <T> The type of the target interface
     * @param interfaceClass The interface to adapt
     * @param invokeTarget The object that will handle the method invocations
     * @param adaptorBuilder A function that creates an InvocationCreator from the method mapping
     * @return A proxy instance implementing the target interface
     * @throws Throwable if the adaptor cannot be created or if the target interface
     *         is not properly annotated
     * @throws IllegalArgumentException if required methods are not found in the invoke target
     */
    public static <T> T buildMatlibAdaptorOf(
            Class<T> interfaceClass, Object invokeTarget, Function<Set<MethodIndex>, InvocationCreator> adaptorBuilder)
            throws Throwable {
        Set<MethodIndex> context = createMappingProxy(interfaceClass, invokeTarget.getClass());
        InvocationCreator invocation =
                adaptorBuilder.apply(context); // AdaptorInvocation.create(interfaceClass, invokeTarget.getClass());
        T proxy = (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(), new Class[] {interfaceClass}, invocation.bindTo(invokeTarget));
        return proxy;
    }
}
