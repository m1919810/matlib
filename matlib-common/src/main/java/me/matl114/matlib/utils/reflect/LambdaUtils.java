package me.matl114.matlib.utils.reflect;

import com.google.common.base.Preconditions;
import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;

public class LambdaUtils {
    private static final ConcurrentHashMap<Pair<Class<?>, Method>, CallSite> lambdaCache = new ConcurrentHashMap<>();
    //    private static final ConcurrentHashMap<Field, FieldGetter> lambdaFieldGetter = new ConcurrentHashMap<>();
    //    private static final ConcurrentHashMap<Field, FieldSetter> lambdaFieldSetter = new ConcurrentHashMap<>();

    /**
     * Creates a lambda expression for a static method that implements the specified functional interface.
     * This method uses LambdaMetafactory to create a CallSite that can be invoked to produce
     * a lambda instance. The result is cached for performance.
     *
     * @param <T> The type of the functional interface
     * @param functionalInterface The functional interface class that the lambda should implement
     * @param method The static method to be wrapped in the lambda
     * @return A lambda instance implementing the functional interface
     * @throws Throwable if the lambda creation fails
     */
    public static <T> T createLambdaForStaticMethod(Class<T> functionalInterface, Method method) throws Throwable {
        CallSite callSite = lambdaCache.computeIfAbsent(Pair.of(functionalInterface, method), (p) -> {
            try {
                return createLambdaForMethodInternal(p.getA(), p.getB(), true, false);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        try {
            return (T) (callSite.getTarget().invoke());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a lambda binding that takes an object parameter and invokes the specified method on it.
     * This method creates a Function that can be used to invoke instance methods on objects.
     * The result is cached for performance.
     *
     * @param <T> The return type of the function
     * @param <W> The type of the object parameter
     * @param functionalInterface The functional interface class (typically Function)
     * @param method The method to be invoked on the object
     * @return A Function that invokes the method on its input object
     * @throws Throwable if the lambda creation fails
     */
    public static <T, W> Function<W, T> createLambdaBinding(Class<T> functionalInterface, Method method)
            throws Throwable {
        CallSite callSite = lambdaCache.computeIfAbsent(Pair.of(functionalInterface, method), (p) -> {
            try {
                return createLambdaForMethodInternal(p.getA(), p.getB(), false, false);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        return (obj) -> {
            try {
                return (T) (callSite.getTarget().invoke(obj));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Creates a lambda expression for an instance method that implements the specified functional interface.
     * This method creates a lambda that will be bound to an object when invoked.
     * The result is cached for performance.
     *
     * @param <T> The type of the functional interface
     * @param functionalInterface The functional interface class that the lambda should implement
     * @param method The instance method to be wrapped in the lambda
     * @return A lambda instance implementing the functional interface
     * @throws Throwable if the lambda creation fails
     */
    public static <T> T createLambdaForMethod(Class<T> functionalInterface, Method method) throws Throwable {
        CallSite callSite = lambdaCache.computeIfAbsent(Pair.of(functionalInterface, method), (p) -> {
            try {
                return createLambdaForMethodInternal(p.getA(), p.getB(), false, true);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        try {
            return (T) (callSite.getTarget().invoke());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates a MethodHandle for a lambda expression that includes extra arguments beyond those
     * required by the functional interface. This method is useful when the target method has
     * additional parameters that need to be bound at lambda creation time.
     *
     * <p>The method validates that the number of extra arguments matches the difference between
     * the target method's parameter count and the functional interface's parameter count.</p>
     *
     * @param <T> The type of the functional interface
     * @param functionalInterface The functional interface class that the lambda should implement
     * @param method The method to be wrapped in the lambda
     * @param extraArgs The number of extra arguments that the target method requires
     * @return A MethodHandle that can be used to create the lambda
     * @throws IllegalArgumentException if extraArgs is 0 or doesn't match the expected count
     */
    public static <T> MethodHandle createLambdaWithOuterArgument(
            Class<T> functionalInterface, Method method, int extraArgs) {
        Preconditions.checkArgument(extraArgs != 0, "If extra args is 0, you should use other method instead");
        Method functionalMethod = Arrays.stream(functionalInterface.getMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Illegal Argument for functional Interface! Abstract method not found in class"
                                + functionalInterface));
        Class<?> targetClass = method.getDeclaringClass();
        try {
            MethodHandles.Lookup lookup;
            // private lookup seems to not having a MODULE access which is required by lambda
            MethodHandle handle;
            if (Modifier.isPublic(method.getModifiers())) {
                lookup = MethodHandles.lookup();
            } else {
                var publicLoopup = MethodHandles.lookup();
                Preconditions.checkArgument(
                        publicLoopup.lookupClass().getModule() == targetClass.getModule(),
                        "Can not create lambda expression for package-private methods in another module");
                lookup = MethodHandles.privateLookupIn(targetClass, publicLoopup);
            }
            handle = lookup.unreflect(method);
            boolean isStatic = Modifier.isStatic(method.getModifiers());
            int totalArgs = method.getParameterCount();
            int extraArgOffset = (isStatic ? 0 : 1) + extraArgs;
            Preconditions.checkArgument(extraArgs == totalArgs - functionalMethod.getParameterCount());
            Class<?>[] realMatchingArgs = new Class[functionalMethod.getParameterCount()];
            System.arraycopy(method.getParameterTypes(), extraArgOffset, realMatchingArgs, 0, realMatchingArgs.length);
            MethodType type = MethodType.methodType(method.getReturnType(), realMatchingArgs);
            Class<?>[] extraArgType = new Class[extraArgOffset];
            if (!isStatic) {
                extraArgType[0] = targetClass;
            }
            System.arraycopy(method.getParameterTypes(), 0, extraArgType, isStatic ? 0 : 1, extraArgs);
            return LambdaMetafactory.metafactory(
                            lookup,
                            functionalMethod.getName(),
                            MethodType.methodType(functionalInterface, extraArgType),
                            getMethodType(functionalMethod),
                            handle,
                            MethodType.methodType(method.getReturnType(), type))
                    .getTarget();

        } catch (Throwable e) {
            throw new RuntimeException("Error while creating lambda expression!", e);
        }
    }

    private static <T> CallSite createLambdaForMethodInternal(
            Class<T> functionalInterface, Method method, boolean sta, boolean dynamicBind) {

        Method functionalMethod = Arrays.stream(functionalInterface.getMethods())
                .filter(m -> Modifier.isAbstract(m.getModifiers()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Illegal Argument for functional Interface! Abstract method not found in class"
                                + functionalInterface));
        Class<?> targetClass = method.getDeclaringClass();
        try {
            MethodHandles.Lookup lookup;
            // private lookup seems to not having a MODULE access which is required by lambda
            MethodHandle handle;
            if (Modifier.isPublic(method.getModifiers())) {
                lookup = MethodHandles.lookup();
            } else {
                var publicLoopup = MethodHandles.lookup();
                Preconditions.checkArgument(
                        publicLoopup.lookupClass().getModule() == targetClass.getModule(),
                        "Can not create lambda expression for package-private methods in another module");
                lookup = MethodHandles.privateLookupIn(targetClass, publicLoopup);
            }
            handle = lookup.unreflect(method);
            return LambdaMetafactory.metafactory(
                    lookup,
                    functionalMethod.getName(),
                    sta || dynamicBind
                            ? MethodType.methodType(functionalInterface)
                            : MethodType.methodType(functionalInterface, targetClass),
                    getMethodType(functionalMethod),
                    handle,
                    getMethodType(method, functionalMethod, dynamicBind));
        } catch (Throwable e) {
            throw new RuntimeException("Error while creating lambda expression!", e);
        }
    }
    //    private static <T> CallSite createLambdaForFieldInternal(Class<T> functionalInterface, Field field, boolean
    // getter){
    //        Method functionalMethod = Arrays.stream(functionalInterface.getMethods())
    //            .filter(m-> Modifier.isAbstract(m.getModifiers()))
    //            .findAny()
    //            .orElseThrow(()->new IllegalArgumentException("Illegal Argument for functional Interface! Abstract
    // method not found in class"+functionalInterface));
    //        Class<?> targetClass = field.getDeclaringClass();
    //        boolean sta = Modifier.isStatic(field.getModifiers());
    //        try{
    //            MethodHandles.Lookup privatelookup = MethodHandles.privateLookupIn(targetClass,
    // MethodHandles.lookup());
    //            return LambdaMetafactory.metafactory(
    //                privatelookup,
    //                functionalMethod.getName(),
    //                MethodType.methodType(functionalInterface),
    //                getMethodType(functionalMethod),
    //                getter? privatelookup.unreflectGetter(field): privatelookup.unreflectSetter(field),
    //                getter?
    //                    ( sta ? MethodType.methodType(field.getType()) :MethodType.methodType(field.getType(),
    // targetClass))
    //                    :(sta ? MethodType.methodType(void.class, field.getType()): MethodType.methodType(void.class,
    // targetClass, field.getType()))
    //            );
    //        }catch (Throwable e){
    //            throw new RuntimeException("Error while creating lambda expression!", e);
    //        }
    //    }
    //
    //    public static FieldGetter<?,?> createFieldGetterLambda(Field field) throws Throwable{
    //        return lambdaFieldGetter.computeIfAbsent(field, (f)->{
    //            boolean isStatic = Modifier.isStatic(f.getModifiers());
    //            if(isStatic){
    //                CallSite callSite = createLambdaForFieldInternal(FieldGetter.StaticFieldGetter.class, f, true);
    //                try{
    //                    return ((FieldGetter.StaticFieldGetter<?>)callSite.getTarget().invokeExact()).toCommon();
    //                }catch (Throwable e){
    //                    throw  new RuntimeException(e);
    //                }
    //            }else {
    //                CallSite callSite =createLambdaForFieldInternal(FieldGetter.class, f, true);
    //                try{
    //                    return (FieldGetter<?, ?>) callSite.getTarget().invokeExact();
    //                }catch (Throwable e){
    //                    throw new RuntimeException(e);
    //                }
    //            }
    //        });
    //    }
    //
    //    public static FieldSetter<?,?> createFieldSetterLambda(Field field) throws Throwable{
    //        return lambdaFieldSetter.computeIfAbsent(field, (f)->{
    //            boolean isStatic = Modifier.isStatic(f.getModifiers());
    //            if(isStatic){
    //                CallSite callSite = createLambdaForFieldInternal(FieldSetter.StaticFieldSetter.class, f, true);
    //                try{
    //                    return ((FieldSetter.StaticFieldSetter<?>)callSite.getTarget().invokeExact()).toCommon();
    //                }catch (Throwable e){
    //                    throw  new RuntimeException(e);
    //                }
    //            }else {
    //                CallSite callSite =createLambdaForFieldInternal(FieldSetter.class, f, true);
    //                try{
    //                    return (FieldSetter<?, ?>) callSite.getTarget().invokeExact();
    //                }catch (Throwable e){
    //                    throw new RuntimeException(e);
    //                }
    //            }
    //        });
    //    }

    private static Class<?>[] argumentTypesBoxing(Class<?>[] clazz, Class<?>[] targetMethod) {
        int length = Math.min(targetMethod.length, clazz.length);

        Class<?>[] result = new Class<?>[length];
        for (int i = 0; i < length; i++) {
            String cls1 = clazz[i].getName();
            String cls2 = targetMethod[i].getName();
            if (ReflectUtils.isPrimitiveType(cls1)) {
                if (!ReflectUtils.isPrimitiveType(cls2)) {
                    result[i] = ReflectUtils.getBoxedClass(cls1);
                    continue;
                }
            }
            if (ReflectUtils.isPrimitiveType(cls2)) {
                if (!ReflectUtils.isPrimitiveType(cls1)) {
                    result[i] = ReflectUtils.getUnboxedClass(cls1);
                    continue;
                }
            }
            result[i] = clazz[i];
        }
        return result;
    }
    /**
     * Creates a MethodType for the specified method.
     * This utility method creates a MethodType that matches the method's signature.
     *
     * @param method The method to create a MethodType for
     * @return A MethodType representing the method's signature
     */
    public static MethodType getMethodType(Method method) {
        return MethodType.methodType(method.getReturnType(), method.getParameterTypes());
    }

    public static MethodType getMethodType(Method method, Method functionalMethod) {
        return MethodType.methodType(
                method.getReturnType(),
                argumentTypesBoxing(method.getParameterTypes(), functionalMethod.getParameterTypes()));
    }

    /**
     * Creates a MethodType for the specified method with an additional 'self' parameter.
     * This method adds the declaring class as the first parameter type, which is useful
     * for instance method invocations where the object reference is passed explicitly.
     *
     * @param method The method to create a MethodType for
     * @return A MethodType with the declaring class as the first parameter
     */
    public static MethodType getMethodTypeWithSelf(Method method, Method functionalMethod) {
        Class[] param = method.getParameterTypes();
        Class[] newParam = new Class[param.length + 1];
        System.arraycopy(param, 0, newParam, 1, param.length);
        newParam[0] = method.getDeclaringClass();
        newParam = argumentTypesBoxing(newParam, functionalMethod.getParameterTypes());
        return MethodType.methodType(method.getReturnType(), newParam);
    }

    /**
     * Creates a MethodType for the specified method, optionally including a 'self' parameter.
     * This method delegates to either getMethodType() or getMethodTypeWithSelf() based on
     * the dynamic parameter.
     *
     * @param method The method to create a MethodType for
     * @param dynamic If true, includes the declaring class as the first parameter
     * @return A MethodType representing the method's signature
     */
    public static MethodType getMethodType(Method method, Method functionalMethod, boolean dynamic) {
        return dynamic ? getMethodTypeWithSelf(method, functionalMethod) : getMethodType(method, functionalMethod);
    }
}
