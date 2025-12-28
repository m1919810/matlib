package me.matl114.matlib.utils.reflect.proxy;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.Reference2ReferenceOpenHashMap;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import me.matl114.matlibAdaptor.proxy.utils.AnnotationUtils;

/**
 * ASM-based proxy builder for creating high-performance method adaptors.
 * This class provides functionality to create method mappings and build adaptors
 * using ASM bytecode generation for optimal performance.
 *
 * <p>This builder is designed to work with interfaces marked with {@code @AdaptorInterface}
 * and creates efficient method mappings between the target interface and the proxy class.</p>
 *
 * <p>Note: This implementation is currently incomplete and throws {@link NotImplementedYet}
 * when attempting to build adaptors.</p>
 */
public class ASMProxyBuilder {

    /**
     * Creates a mapping between methods of a target interface and a proxy class.
     * This method analyzes the target interface and proxy class to establish
     * method correspondences for efficient invocation.
     *
     * <p>The mapping is created by matching method names and parameter types
     * between the target interface and the proxy class.</p>
     *
     * @param targetInterface The interface whose methods need to be mapped
     * @param proxyClass The class that will provide the method implementations
     * @return A map containing the method mappings (currently returns null - not implemented)
     */
    public static Map<Method, Method> createMapping(Class<?> targetInterface, Class<?> proxyClass) {
        return null;
    }

    /**
     * Builds an adaptor instance for the specified target interface using the given invoke target.
     * This method creates a proxy that implements the target interface and delegates
     * method calls to the invoke target object.
     *
     * <p>The target interface must be marked with {@code @AdaptorInterface} annotation,
     * and the invoke target class must contain a matching interface with the same name.</p>
     *
     * @param <T> The type of the target interface
     * @param targetInterface The interface to adapt
     * @param invokeTarget The object that will handle the method invocations
     * @return A proxy instance implementing the target interface
     * @throws Throwable if the adaptor cannot be created or if the target interface
     *         is not properly annotated
     * @throws IllegalArgumentException if required methods are not found in the invoke target
     * @throws NotImplementedYet if the ASM-based implementation is not yet complete
     */
    public static <T> T buildAdaptorOf(Class<T> targetInterface, Object invokeTarget) throws Throwable {
        return buildAdaptorOf(targetInterface, invokeTarget, true);
    }

    /**
     * Builds an adaptor instance for the specified target interface using the given invoke target.
     * This method creates a proxy that implements the target interface and delegates
     * method calls to the invoke target object, with optional adaptor validation.
     *
     * <p>The target interface must be marked with {@code @AdaptorInterface} annotation.
     * If ensureAdaptor is true, the method will validate that the invoke target class
     * contains a matching interface with the same name.</p>
     *
     * <p>This method performs the following steps:</p>
     * <ol>
     *   <li>Validates that the target interface is properly annotated</li>
     *   <li>Optionally ensures the adaptor relationship is valid</li>
     *   <li>Creates a method mapping between the interface and target class</li>
     *   <li>Builds the proxy instance (currently throws NotImplementedYet)</li>
     * </ol>
     *
     * @param <T> The type of the target interface
     * @param targetInterface The interface to adapt
     * @param invokeTarget The object that will handle the method invocations
     * @param ensureAdaptor Whether to validate the adaptor relationship
     * @return A proxy instance implementing the target interface
     * @throws Throwable if the adaptor cannot be created or if the target interface
     *         is not properly annotated
     * @throws IllegalArgumentException if required methods are not found in the invoke target
     * @throws NotImplementedYet if the ASM-based implementation is not yet complete
     */
    public static <T> T buildAdaptorOf(Class<T> targetInterface, Object invokeTarget, boolean ensureAdaptor)
            throws Throwable {
        var annotation = AnnotationUtils.getAdaptorInstance(targetInterface);
        Preconditions.checkState(annotation.isPresent(), "Illegal Adaptor class!This is not an AdaptorInterface!");
        Class proxyClass = invokeTarget.getClass();
        if (ensureAdaptor) {
            ProxyBuilder.ensureAdaptor(targetInterface, proxyClass);
        }
        // save the mapping from targetInterface to invokeTarget fastAccess index;
        Map<Method, Method> methodMapper = new Reference2ReferenceOpenHashMap<>();
        Set<Method> methodMustRequired = AnnotationUtils.getAdaptedMethods(targetInterface);
        Iterator<Method> iter = methodMustRequired.iterator();
        int indexCnt = 0;
        while (iter.hasNext()) {
            Method m = iter.next();
            try {
                var params = m.getParameterTypes();
                Method mapped = proxyClass.getMethod(m.getName(), params);
                methodMapper.put(m, mapped);
            } catch (Throwable e) {
                throw new IllegalArgumentException("Method " + m + " not found in invoke target class! using"
                        + targetInterface + " as Adaptor and " + proxyClass + " as invoke target");
            }
        }
        // todo mark as not complete
        throw new NotImplementedYet();
    }
}
