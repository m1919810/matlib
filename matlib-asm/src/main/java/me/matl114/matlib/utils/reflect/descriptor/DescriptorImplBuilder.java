package me.matl114.matlib.utils.reflect.descriptor;

import static me.matl114.matlib.utils.reflect.classBuild.ClassBuilder.*;

import com.google.common.base.Preconditions;
import java.lang.reflect.*;
import java.util.*;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.reflect.*;
import me.matl114.matlib.utils.reflect.classBuild.ClassBuildingUtils;
import me.matl114.matlib.utils.reflect.classBuild.annotation.*;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.DescriptorBuildException;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

@SuppressWarnings("all")
public class DescriptorImplBuilder {
    private static final Map<Pair<Class<?>, Class<? extends TargetDescriptor>>, TargetDescriptor> CACHE =
            new HashMap<>();
    private static final Map<Class<? extends TargetDescriptor>, TargetDescriptor> MULTI_CACHE = new HashMap<>();
    /**
     * create a impl for descriptive interface at targetClass
     * @param targetClass
     * @param descriptiveInterface
     * @return
     * @param <T>
     */
    public static synchronized <T extends TargetDescriptor> T createHelperImplAt(
            Class<?> targetClass, Class<T> descriptiveInterface) {
        synchronized (CACHE) {
            return (T) CACHE.computeIfAbsent(Pair.of(targetClass, descriptiveInterface), k -> {
                try {
                    return createSingleInternel(k.getA(), k.getB());
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
            Class<?> clazz = ObfManager.getManager().reobfClass(val);
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
                clazz = ObfManager.getManager().reobfClass(val);
            } catch (Throwable e) {
                clazz = null;
            }
            try {
                return createMultiInternel(clazz, descriptiveInterface);
            } catch (DescriptorBuildException e) {
                throw e;
            } catch (Throwable e) {
                throw new DescriptorBuildException(e);
            }
        });
    }

    private static <T extends TargetDescriptor> T createMultiInternel(
            @Nullable Class<?> defaultClass, Class<T> descriptiveInterface) throws Throwable {
        Preconditions.checkArgument(descriptiveInterface.isInterface(), "Descriptor should be a interface!");
        Preconditions.checkNotNull(
                descriptiveInterface.getAnnotation(MultiDescriptive.class), "No descriptor annotation found!");
        //        List<Method> fieldTarget = new ArrayList<>();
        //        List<Method> methodTarget = new ArrayList<>();
        //        List<Method> constructorTarget = new ArrayList<>();
        List<Method> uncompletedMethod = new ArrayList<>();
        //        List<Method> typeCastTarget = new ArrayList<>();

        Map<Method, Field> fieldGetDescrip = new LinkedHashMap<>();
        Map<Method, Field> fieldSetDescrip = new LinkedHashMap<>();
        Map<Method, Method> methodDescrip = new LinkedHashMap<>();
        Map<Method, Constructor<?>> constructorDescrip = new LinkedHashMap<>();
        Map<Method, Class<?>> castCheckDescrip = new LinkedHashMap<>();
        Map<Method, Class<?>> getTypeDescrip = new LinkedHashMap<>();
        // Map<String, Method> cdToOrigin = new HashMap<>();
        // collect targets
        collectDescriptorMethodMappings(
                descriptiveInterface,
                defaultClass,
                true,
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                getTypeDescrip,
                uncompletedMethod);
        return buildTargetFlattenInvokeImpl(
                defaultClass,
                descriptiveInterface,
                Object.class,
                new Class[0],
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                getTypeDescrip,
                uncompletedMethod);
    }

    private static <T extends TargetDescriptor> T createSingleInternel(
            Class<?> targetClass, Class<T> descriptiveInterface) throws Throwable {
        Preconditions.checkArgument(descriptiveInterface.isInterface(), "Descriptor should be a interface!");
        Preconditions.checkNotNull(
                descriptiveInterface.getAnnotation(Descriptive.class), "No descriptor annotation found!");
        Map<Method, Field> fieldGetDescrip = new LinkedHashMap<>();
        Map<Method, Field> fieldSetDescrip = new LinkedHashMap<>();
        Map<Method, Method> methodDescrip = new LinkedHashMap<>();
        Map<Method, Constructor<?>> constructorDescrip = new LinkedHashMap<>();
        List<Method> uncompletedMethod = new ArrayList<>();
        Map<Method, Class<?>> castCheckDescrip = new LinkedHashMap<>();
        Map<Method, Class<?>> getTypeDescrip = new LinkedHashMap<>();
        // Map<String, Method> cdToOrigin = new HashMap<>();
        // collect targets
        collectDescriptorMethodMappings(
                descriptiveInterface,
                targetClass,
                false,
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                getTypeDescrip,
                uncompletedMethod);
        return buildTargetFlattenInvokeImpl(
                targetClass,
                descriptiveInterface,
                Object.class,
                new Class[0],
                fieldGetDescrip,
                fieldSetDescrip,
                methodDescrip,
                constructorDescrip,
                castCheckDescrip,
                getTypeDescrip,
                uncompletedMethod);
    }

    static void collectDescriptorMethodMappings(
            Class<?> descriptiveInterface,
            @Nullable Class<?> targetClass,
            boolean multi,
            Map<Method, Field> fieldGetDescrip,
            Map<Method, Field> fieldSetDescrip,
            Map<Method, Method> methodDescrip,
            Map<Method, Constructor<?>> constructorDescrip,
            Map<Method, Class<?>> castCheckDescrip,
            Map<Method, Class<?>> getTypeDescrip,
            List<Method> uncompletedMethod) {
        List<Method> fieldTarget = new ArrayList<>();
        List<Method> methodTarget = new ArrayList<>();
        List<Method> constructorTarget = new ArrayList<>();
        List<Method> typeCastTarget = new ArrayList<>();
        List<Method> getTypeTarget = new ArrayList<>();
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
                    var a1 = m.getAnnotation(FieldTarget.class);
                    if (a1 != null) {
                        fieldTarget.add(m);
                        return;
                    }
                    var a2 = m.getAnnotation(MethodTarget.class);
                    if (a2 != null) {
                        methodTarget.add(m);
                        return;
                    }
                    var a3 = m.getAnnotation(ConstructorTarget.class);
                    if (a3 != null) {
                        constructorTarget.add(m);
                        return;
                    }
                    var a4 = m.getAnnotation(CastCheck.class);
                    if (a4 != null) {
                        typeCastTarget.add(m);
                        return;
                    }
                    var a5 = m.getAnnotation(GetType.class);
                    if (a5 != null) {
                        getTypeTarget.add(m);
                        return;
                    }
                    // only collect uncompleted abstract methods
                    if (!m.isSynthetic() && !m.isBridge() && Modifier.isAbstract(m.getModifiers())) {
                        uncompletedMethod.add(m);
                        return;
                    }
                });

        // resolve target
        // collect fields first
        List<Field> fields;
        if (!multi) fields = ReflectUtils.getAllFieldsRecursively(targetClass);
        else fields = null;
        // resolve fields
        for (Method fieldAccess : fieldTarget) {
            if (multi) {
                fields = null;
                Class<?> targetClass0;
                var redirectClass = fieldAccess.getAnnotation(RedirectClass.class);
                if (redirectClass != null) {
                    targetClass0 = resolveClassNameOrJvmName(redirectClass.value());
                } else {
                    targetClass0 = targetClass;
                }
                if (targetClass0 == null) {
                    uncompletedMethod.add(fieldAccess);
                    continue;
                }
                fields = ReflectUtils.getAllFieldsRecursively(targetClass0);
            }
            var tar = matchFields(fieldAccess, fields);
            // get tar here
            if (tar == null) {
                uncompletedMethod.add(fieldAccess);
            } else {
                if (tar.getB()) {
                    fieldGetDescrip.put(fieldAccess, tar.getA());
                } else {
                    fieldSetDescrip.put(fieldAccess, tar.getA());
                }
            }
        }
        List<Method> methods;
        if (!multi) {
            methods = ReflectUtils.getAllMethodsRecursively(targetClass).stream()
                    .filter(m -> !m.isBridge() && !m.isSynthetic())
                    .toList();
        } else {
            methods = null;
        }

        for (Method methodAccess : methodTarget) {
            if (multi) {
                methods = null;
                Class<?> targetClass0;
                var redirectClass = methodAccess.getAnnotation(RedirectClass.class);
                if (redirectClass != null) {
                    targetClass0 = resolveClassNameOrJvmName(redirectClass.value());
                } else {
                    targetClass0 = targetClass;
                }
                if (targetClass0 == null) {
                    uncompletedMethod.add(methodAccess);
                    continue;
                }
                methods = ReflectUtils.getAllMethodsRecursively(targetClass0).stream()
                        .filter(m -> !m.isBridge() && !m.isSynthetic())
                        .toList();
            }
            List<Method> filter1 = matchMethods(methodAccess, methods);
            if (filter1.isEmpty()) {
                uncompletedMethod.add(methodAccess);
            } else {
                methodDescrip.put(methodAccess, filter1.getFirst());
            }
        }
        // resolve methods
        for (Method constructorAccess : constructorTarget) {
            Class<?> targetClass0 = targetClass;
            if (multi) {
                var redirectClass = constructorAccess.getAnnotation(RedirectClass.class);
                if (redirectClass != null) {
                    targetClass0 = resolveClassNameOrJvmName(redirectClass.value());
                }
                if (targetClass0 == null) {
                    uncompletedMethod.add(constructorAccess);
                    continue;
                }
            }
            if (Modifier.isAbstract(targetClass0.getModifiers())) {
                // abstract class constructor can not be accessed
                uncompletedMethod.add(constructorAccess);
                continue;
            }
            List<Constructor<?>> constructors1 =
                    ClassBuildingUtils.matchConstructors(constructorAccess, targetClass0.getDeclaredConstructors());
            if (constructors1.isEmpty()) {
                uncompletedMethod.add(constructorAccess);
            } else {
                constructorDescrip.put(constructorAccess, constructors1.getFirst());
            }
        }
        for (Method typeCast : typeCastTarget) {
            Class<?> castCls = null;
            var cast = typeCast.getAnnotation(CastCheck.class);
            String value = cast.value();
            castCls = resolveClassNameOrJvmName(value);
            if (castCls != null) {
                castCheckDescrip.put(typeCast, castCls);
            } else {
                uncompletedMethod.add(typeCast);
            }
        }
        for (Method typeCast : getTypeTarget) {
            Class<?> castCls = null;
            var cast = typeCast.getAnnotation(GetType.class);
            String value = cast.value();
            castCls = resolveClassNameOrJvmName(value);
            if (castCls != null) {
                getTypeDescrip.put(typeCast, castCls);
            } else {
                uncompletedMethod.add(typeCast);
            }
        }
    }

    static Class resolveClassNameOrJvmName(String classPath) {
        if (classPath.endsWith(";")) {
            try {
                return ObfManager.getManager().reobfClass(ByteCodeUtils.fromJvmType(classPath));
            } catch (Throwable e) {
            }
        } else {
            try {
                return ObfManager.getManager().reobfClass(classPath);
            } catch (Throwable e) {
            }
        }
        return null;
    }

    static Pair<Field, Boolean> matchFields(Method fieldAccess, List<Field> fields) {
        return ClassBuildingUtils.matchFields(
                fieldAccess,
                fields,
                fieldAccess.getAnnotation(FieldTarget.class).isStatic());
    }

    static List<Method> matchMethods(Method methodAccess, List<Method> methods) {

        return ClassBuildingUtils.matchMethods(
                methodAccess,
                methods,
                methodAccess.getAnnotation(MethodTarget.class).isStatic(),
                true);
    }
}
