package me.matl114.matlib.utils.reflect.descriptor;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.classBuild.ClassBuildingUtils;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorBuildException;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.proxy.invocation.FastRemappingInvocation;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodIndex;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodSignature;

@SuppressWarnings("all")
public class DescriptorProxyBuilder {
    private static final Map<Pair<Class<?>, Class<? extends TargetDescriptor>>, TargetDescriptor> CACHE =
            new HashMap<>();
    private static final Map<Class<? extends TargetDescriptor>, TargetDescriptor> MULTI_CACHE = new HashMap<>();

    public static synchronized <T extends TargetDescriptor> T createHelperImplAt(
            Class<?> targetClass, Class<T> descriptiveInterface) {
        synchronized (CACHE) {
            return (T) CACHE.computeIfAbsent(Pair.of(targetClass, descriptiveInterface), k -> {
                try {
                    return createSingleInternel(k.getA(), k.getB(), DescriptorProxyBuilder.class.getClassLoader());
                } catch (DescriptorBuildException e) {
                    throw e;
                } catch (Throwable e) {
                    throw new DescriptorBuildException(e);
                }
            });
        }
    }

    public static <T extends TargetDescriptor> T createHelperImpl(Class<T> descriptiveInterface) {
        // using @Descriptive target to target class
        var re = descriptiveInterface.getAnnotation(Descriptive.class);
        Preconditions.checkNotNull(re, "No descriptor annotation found!");
        String val = re.target();
        try {
            Class<?> clazz = Class.forName(val);
            return createHelperImplAt(clazz, descriptiveInterface);
        } catch (Throwable e) {
            throw DescriptorBuildException.warp(e);
        }
    }

    public static synchronized <T extends TargetDescriptor> T createMultiHelper(Class<T> descriptiveInterface) {
        var re = descriptiveInterface.getAnnotation(MultiDescriptive.class);
        Preconditions.checkNotNull(re, "No descriptor annotation found!");
        String val = re.targetDefault();
        return (T) MULTI_CACHE.computeIfAbsent(descriptiveInterface, k -> {
            Class<?> clazz;
            try {
                clazz = Class.forName(val);
            } catch (Throwable e) {
                clazz = null;
            }
            try {
                return createMultiInternel(clazz, descriptiveInterface, DescriptorProxyBuilder.class.getClassLoader());
            } catch (DescriptorBuildException e) {
                throw e;
            } catch (Throwable e) {
                throw new DescriptorBuildException(e);
            }
        });
    }

    private static <T extends TargetDescriptor> T createMultiInternel(
            @Nullable Class<?> defaultClass, Class<T> descriptiveInterface, ClassLoader customLoader) throws Throwable {
        Preconditions.checkArgument(descriptiveInterface.isInterface(), "Descriptor should be a interface!");
        Preconditions.checkNotNull(
                descriptiveInterface.getAnnotation(MultiDescriptive.class), "No descriptor annotation found!");
        List<Method> uncompletedMethod = new ArrayList<>();
        Map<Method, Field> fieldGetDescrip = new LinkedHashMap<>();
        Map<Method, Field> fieldSetDescrip = new LinkedHashMap<>();
        Map<Method, Method> methodDescrip = new LinkedHashMap<>();
        Map<Method, Constructor<?>> constructorDescrip = new LinkedHashMap<>();
        Map<Method, Class<?>> castCheckDescrip = new LinkedHashMap<>();
        // Map<String, Method> cdToOrigin = new HashMap<>();
        // collect targets
        collectDescriptorMethodMappingsNoObf(
                descriptiveInterface,
                defaultClass,
                true,
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                uncompletedMethod);
        return buildProxyForDescriptor(
                defaultClass,
                descriptiveInterface,
                Object.class,
                new Class[0],
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                uncompletedMethod,
                customLoader);
    }
    // @VisibleForTesting
    private static <T extends TargetDescriptor> T createSingleInternel(
            Class<?> targetClass, Class<T> descriptiveInterface, ClassLoader customLoader) throws Throwable {
        Preconditions.checkArgument(descriptiveInterface.isInterface(), "Descriptor should be a interface!");
        Preconditions.checkNotNull(
                descriptiveInterface.getAnnotation(Descriptive.class), "No descriptor annotation found!");
        Map<Method, Field> fieldGetDescrip = new LinkedHashMap<>();
        Map<Method, Field> fieldSetDescrip = new LinkedHashMap<>();
        Map<Method, Method> methodDescrip = new LinkedHashMap<>();
        Map<Method, Constructor<?>> constructorDescrip = new LinkedHashMap<>();
        List<Method> uncompletedMethod = new ArrayList<>();
        Map<Method, Class<?>> castCheckDescrip = new LinkedHashMap<>();
        // Map<String, Method> cdToOrigin = new HashMap<>();
        // collect targets
        collectDescriptorMethodMappingsNoObf(
                descriptiveInterface,
                targetClass,
                false,
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                uncompletedMethod);
        return buildProxyForDescriptor(
                targetClass,
                descriptiveInterface,
                Object.class,
                new Class[0],
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                uncompletedMethod,
                customLoader);
    }

    /**
     * we don't support other proxied descriptive target except method, because it will be in low efficiency
     */
    private static synchronized <T extends TargetDescriptor> T buildProxyForDescriptor(
            @Nullable Class<?> targetClass,
            Class<T> mainInterfaceImpl,
            Class<?> superClass,
            Class<?>[] appendedInterfaces,
            Map<Method, Field> fieldGetDescrip,
            Map<Method, Field> fieldSetDescrip,
            Map<Method, Method> methodDescrip,
            Map<Method, Constructor<?>> constructorDescrip,
            Map<Method, Class<?>> typeCastDescrip,
            List<Method> uncompletedMethod,
            ClassLoader customLoader)
            throws Throwable {
        List<Method> copiedUncompleted = List.copyOf(uncompletedMethod);
        ClassBuildingUtils.checkUncompleted(uncompletedMethod, mainInterfaceImpl);
        Set<MethodIndex> methodIndexs = new ReferenceArraySet<>();
        // create base method indexs
        Method[] baseMethod = Object.class.getMethods();
        for (var me : baseMethod) {
            methodIndexs.add(new MethodIndex(
                    null, MethodSignature.getSignature(me), ReflectUtils.getBaseMethodIndex(me), false));
        }
        AtomicInteger counter = new AtomicInteger(0);
        // method with default val should be called using invokeSpecial
        // checkUncompleted will remove all default method
        List<MethodIndex> methodWithFallback = copiedUncompleted.stream()
                .filter(m -> !Modifier.isAbstract(m.getModifiers()))
                .map(m -> new MethodIndex(m, MethodSignature.getSignature(m), counter.getAndIncrement(), true))
                .toList();

        methodIndexs.addAll(methodWithFallback);
        for (var entry : methodDescrip.entrySet()) {
            methodIndexs.add(new MethodIndex(
                    entry.getValue(), MethodSignature.getSignature(entry.getKey()), counter.getAndIncrement(), false));
        }
        Class[] interfaces = new Class[appendedInterfaces.length + 1];
        System.arraycopy(appendedInterfaces, 0, interfaces, 1, appendedInterfaces.length);
        interfaces[0] = mainInterfaceImpl;

        DescriptorMapperProxy proxy = new DescriptorMapperProxy(methodIndexs, counter.incrementAndGet());
        T val = (T) Proxy.newProxyInstance(customLoader, interfaces, proxy.bindTo(new Object()));
        return val;
    }

    public static class DescriptorMapperProxy extends FastRemappingInvocation {

        private DescriptorMapperProxy(Set<MethodIndex> rawData, int size) throws IllegalAccessException {
            super(rawData);
        }

        @Override
        public Object invoke0(Object proxy, Object target, MethodIndex methodIndex, Object[] args) {
            int index1 = methodIndex.index();
            try {
                if (staticFlag.getBoolean(methodIndex)) {
                    return methodIndex.target().invoke(null, args);
                } else {
                    Object[] argument = new Object[args.length - 1];
                    System.arraycopy(args, 1, argument, 0, argument.length);
                    return methodIndex.target().invoke(args[0], argument);
                }
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    // ** using no-asm ObfImpl

    static void collectDescriptorMethodMappingsNoObf(
            Class<?> descriptiveInterface,
            @Nullable Class<?> targetClass,
            boolean multi,
            Map<Method, Field> fieldGetDescrip,
            Map<Method, Field> fieldSetDescrip,
            Map<Method, Method> methodDescrip,
            Map<Method, Constructor<?>> constructorDescrip,
            Map<Method, Class<?>> castCheckDescrip,
            List<Method> uncompletedMethod) {
        List<Method> fieldTarget = new ArrayList<>();
        List<Method> methodTarget = new ArrayList<>();
        List<Method> constructorTarget = new ArrayList<>();
        List<Method> typeCastTarget = new ArrayList<>();
        Arrays.stream(descriptiveInterface.getMethods())
                .filter(m -> !(m.getName().equals("getTargetClass")
                        && m.getParameterCount() == 0
                        && m.getReturnType() == Class.class))
                .filter(m -> {
                    var mod = m.getModifiers();
                    // can complete default methods
                    return !Modifier.isStatic(mod) && !Modifier.isPrivate(mod);
                })
                // .filter(m -> )
                .forEach(m -> {
                    //  Debug.logger(m);
                    //                var a1 =  m.getAnnotation(FieldTarget.class);
                    //                if(a1 != null){
                    //                    fieldTarget.add(m);
                    //                    return;
                    //                }
                    // we only support MethodTarget first
                    var a2 = m.getAnnotation(MethodTarget.class);
                    if (a2 != null) {
                        methodTarget.add(m);
                        return;
                    }
                    //                var a3 = m.getAnnotation(ConstructorTarget.class);
                    //                if(a3 != null){
                    //                    constructorTarget.add(m);
                    //                    return;
                    //                }
                    //                var a4 = m.getAnnotation(CastCheck.class);
                    //                if(a4 != null){
                    //                    typeCastTarget.add(m);
                    //                    return;
                    //                }
                    // only collect uncompleted abstract methods
                    if (!m.isSynthetic() && !m.isBridge() && Modifier.isAbstract(m.getModifiers())) {
                        uncompletedMethod.add(m);
                        return;
                    }
                });

        // resolve target
        // collect fields first
        // resolve fields
        //        for (Method fieldAccess: fieldTarget){
        //            Class<?> targetClass0;
        //            if(multi){
        //                var redirectClass = fieldAccess.getAnnotation(RedirectClass.class);
        //                if(redirectClass != null){
        //                    try{
        //                        targetClass0 = Class.forName(redirectClass.value());
        //                    }catch (Throwable e){
        //                        targetClass0 = targetClass;
        //                    }
        //                }else {
        //                    targetClass0 = targetClass;
        //                }
        //                if(targetClass0 == null){
        //                    uncompletedMethod.add(fieldAccess);
        //                    continue;
        //                }
        //            }
        //            var tar = ReflectUtils.getMethodsRecursively(targetClass0, fiel)
        //            //get tar here
        //            if(tar == null){
        //                uncompletedMethod.add(fieldAccess);
        //            }else {
        //                if(tar.getB()){
        //                    fieldGetDescrip.put(fieldAccess, tar.getA());
        //                }else {
        //                    fieldSetDescrip.put(fieldAccess, tar.getA());
        //                }
        //            }
        //        }
        //        List<Method> methods;
        //        if(!multi){
        //            methods = ReflectUtils.getAllMethodsRecursively(targetClass).stream().filter(m-> !m.isBridge() &&
        // !m.isSynthetic()).toList();
        //        }else {
        //            methods = null;
        //        }

        for (Method methodAccess : methodTarget) {
            Class<?> targetClass0 = targetClass;
            if (multi) {
                var redirectClass = methodAccess.getAnnotation(RedirectClass.class);
                if (redirectClass != null) {
                    try {
                        targetClass0 = Class.forName(redirectClass.value());
                    } catch (Throwable ignored) {

                    }
                }
                if (targetClass0 == null) {
                    uncompletedMethod.add(methodAccess);
                    continue;
                }
                ;
            }
            //  Debug.logger(targetClass0);
            MethodTarget annotation = methodAccess.getAnnotation(MethodTarget.class);
            boolean isStatic = annotation.isStatic();
            RedirectName nameRedirect = methodAccess.getAnnotation(RedirectName.class);
            String name = nameRedirect != null ? nameRedirect.value() : methodAccess.getName();
            Class[] arguments = new Class[methodAccess.getParameterCount() - (isStatic ? 0 : 1)];
            int x = isStatic ? 0 : 1;
            Parameter[] params = methodAccess.getParameters();
            for (int i = x; i < params.length; ++i) {
                Parameter parameter = params[i];
                RedirectType type = parameter.getAnnotation(RedirectType.class);
                if (type != null) {
                    try {
                        arguments[i - x] = Class.forName(ByteCodeUtils.fromJvmType(type.value()));
                    } catch (Throwable e) {
                        Debug.logger(e, "Resolve RedirectType failed:");
                        arguments[i - x] = parameter.getType();
                    }
                } else {
                    arguments[i - x] = parameter.getType();
                }
            }
            var method = ReflectUtils.getMethodsRecursively(targetClass0, name, arguments);
            if (method == null) {
                uncompletedMethod.add(methodAccess);
            } else {
                methodDescrip.put(methodAccess, method.getA());
            }
        }
        // resolve methods
        //        for (Method constructorAccess : constructorTarget){
        //            Class<?> targetClass0 = targetClass;
        //            if(multi){
        //                var redirectClass = constructorAccess.getAnnotation(RedirectClass.class);
        //                if(redirectClass != null){
        //                    try{
        //                        targetClass0 = ObfManager.getManager().reobfClass(redirectClass.value());
        //                    }catch (Throwable ignored){
        //                    }
        //                }
        //                if(targetClass0 == null){
        //                    uncompletedMethod.add(constructorAccess);
        //                    continue;
        //                }
        //            }
        //            List<Constructor<?>> constructors1 = matchConstructors(constructorAccess,
        // targetClass0.getDeclaredConstructors());
        //            if(constructors1.isEmpty()){
        //                uncompletedMethod.add(constructorAccess);
        //            }else {
        //                constructorDescrip.put(constructorAccess, constructors1.getFirst());
        //            }
        //
        //        }
        //        for (Method typeCast: typeCastTarget){
        //            Class<?> castCls = null;
        //            var cast = typeCast.getAnnotation(CastCheck.class);
        //            try{
        //                castCls = ObfManager.getManager().reobfClass(cast.value());
        //            }catch (Throwable e){
        //            }
        //            if(castCls != null){
        //                castCheckDescrip.put(typeCast, castCls);
        //            }else {
        //                uncompletedMethod.add(typeCast);
        //            }
        //        }
    }
}
