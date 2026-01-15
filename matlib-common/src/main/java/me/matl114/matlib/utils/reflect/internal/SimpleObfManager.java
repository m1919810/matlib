package me.matl114.matlib.utils.reflect.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import me.matl114.matlib.algorithms.dataStructures.struct.LazyInitValue;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;

public interface SimpleObfManager {
    static SimpleObfManager getManager() {
        return manager.get();
    }

    static LazyInitValue<SimpleObfManager> manager = LazyInitValue.ofLazy(() -> {
        try {
            // use ObfManager if preset
            Class<?> clazz = Class.forName(SimpleObfManager.class.getPackageName() + ".ObfManager");
            Method m = clazz.getDeclaredMethod("getManager");
            return (SimpleObfManager) m.invoke(null);
        } catch (Throwable e0) {
            try {
                return new SimpleObfManagerImpl();
            } catch (Throwable e) {
                var e1 = e.getCause();
                Debug.logger(e1 != null ? e1 : e, "Error while creating SimpleObfManagerImpl:");
                Debug.logger("Using Default Simple Obf Impl");
                return new DefaultImpl();
            }
        }
    });
    /**
     * this method should return the deobf class name of a optional-obf class, you should check whether the class is in the mapping
     * @param obfName
     * @return
     */
    public String deobfClassName(String obfName);
    /**
     * used for deobf clazz types in params and returnType where there exists fucking  primitive type and fucking arrays
     * @param clazz
     * @return
     */
    default String deobfToJvm(Class<?> clazz) {
        var re = ByteCodeUtils.getComponentType(clazz);
        String deobfOrigin = deobfClassName(re.getB());
        String jvmType = ByteCodeUtils.toJvmType(deobfOrigin);
        return re.getA() + jvmType;
    }

    //    default String deobfNameToJvm(String clazz){
    //
    //    }
    /**
     * this method should return the reobf class name of a runtime class, you should check whether the class is in the mapping
     * @param mojangName
     * @return
     */
    public String reobfClassName(String mojangName);

    default Class<?> reobfClass(String mojangName) throws Throwable {
        String obfName = reobfClassName(mojangName);
        try {
            // try check obf
            return Class.forName(obfName);
        } catch (NoClassDefFoundError error) {
            if (Objects.equals(mojangName, obfName)) {
                // nmd 没有obf,就是不对
                throw error;
            }
            // no obf present
            return Class.forName(mojangName);
        }
    }
    /**
     * this method return the mojang methodName of a obf method descriptor, you should check mapping and return deobf value or self
     * make sure that argument and return value are also deobf
     *
     * this method could not relocate craftbukkit class in parameters, we are sorry about that
     * but no craftbukkit method will be obf, yeeeeeeee mother fucker
     *
     * @param mojangClassName
     * @param obfMethodDescriptor ,should be in jvm method format, you can use ByteCodeUtils to generate these string
     * @return
     */
    public String deobfMethodInClass(String mojangClassName, String obfMethodDescriptor);

    default String deobfMethod(Method method0) {
        return deobfMethodInClass(
                deobfClassName(method0.getDeclaringClass().getName()), ByteCodeUtils.getMethodDescriptor(method0));
    }

    default boolean isMethodNameMatchAfterDeobf(String reobfClassName, String targetDescriptor, String methodName) {
        return Objects.equals(deobfMethodInClass(reobfClassName, targetDescriptor), methodName);
    }

    default String deobfField(Field field0) {
        return field0.getName();
    }

    static class DefaultImpl implements SimpleObfManager {

        @Override
        public String deobfClassName(String currentName) {
            return currentName;
        }

        @Override
        public String reobfClassName(String mojangName) {
            return mojangName;
        }

        @Override
        public String deobfMethodInClass(String reobfClassName, String methodDescriptor) {
            return ByteCodeUtils.parseMethodNameFromDescriptor(methodDescriptor);
        }

        public String deobfFieldInClass(String mojangClassName, String obfMethodDescriptor) {
            return ByteCodeUtils.parseFieldNameFromDescriptor(obfMethodDescriptor);
        }
    }
}
