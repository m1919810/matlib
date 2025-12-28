package me.matl114.matlib.utils.reflect.internel;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.matl114.matlib.algorithms.dataStructures.frames.initBuidler.InitializeProvider;
import me.matl114.matlib.algorithms.dataStructures.struct.LazyInitValue;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import me.matl114.matlib.utils.reflect.internal.SimpleObfManager;
import me.matl114.matlib.utils.reflect.internal.SimpleObfManagerImpl;

@SuppressWarnings("all")
public interface ObfManager extends SimpleObfManager {
    static ObfManager getManager() {
        return manager.get();
    }

    static LazyInitValue<ObfManager> manager = LazyInitValue.ofLazy(() -> {
        try {
            ObfManager manager1 = new ObfManagerImpl();
            //inject into SimpleObfManager
            SimpleObfManager.manager.set(manager1);
            return manager1;
        } catch (Throwable e) {
            var e1 = e.getCause();
            Debug.logger(e1 != null ? e1 : e, "Error while creating ObfManagerImpl:");
            Debug.logger("Using Default Simple Obf Impl");
            return new DefaultImpl();
        }
    });


//    ObfManager manager = new InitializeProvider<>(() -> {
//                try {
//                    return new ObfManagerImpl();
//                } catch (Throwable e) {
//                    var e1 = e.getCause();
//                    Debug.logger(e1 != null ? e1 : e, "Error while creating ObfManagerImpl:");
//                    Debug.logger("Using Default Impl");
//                    return new DefaultImpl();
//                }
//            })
//            .v();

    public String deobfFieldInClass(String mojangClassName, String obfFieldDescriptor);

    default String deobfField(Field field0) {
        return deobfFieldInClass(
                deobfClassName(field0.getDeclaringClass().getName()),
                ByteCodeUtils.getFieldDescriptor(field0.getName(), field0.getType()));
    }

    default boolean isFieldSameAfterDeobf(String mojangName, String targetDescriptor, String fieldName) {
        return Objects.equals(deobfFieldInClass(mojangName, targetDescriptor), fieldName);
    }

    default Field matchFieldOrThrow(List<Field> fields, String name) {
        if (fields.isEmpty()) {
            return null;
        }
        try {
            return fields.stream()
                    .filter(f -> deobfField(f).equals(name))
                    .peek(f -> f.setAccessible(true))
                    .findFirst()
                    .orElseThrow();
        } catch (Throwable e) {
            Debug.logger(
                    e,
                    "Exception while reflecting field",
                    name,
                    "in",
                    fields.get(0).getDeclaringClass().getSimpleName() + ":");
            return null;
        }
    }

    default Field lookupFieldInClass(Class<?> clazz, String name) {
        Field[] fields = clazz.getDeclaredFields();
        if (fields.length == 0) return null;

        String mojangName = deobfClassName(clazz.getName());

        try {
            return Arrays.stream(fields)
                    .filter(f -> {
                        return deobfFieldInClass(mojangName, ByteCodeUtils.getFieldDescriptor(f.getName(), f.getType()))
                                .equals(name);
                    })
                    .peek(f -> f.setAccessible(true))
                    .findFirst()
                    .orElseThrow();
        } catch (Throwable e) {
            Debug.logger(
                    e,
                    "Exception while reflecting field",
                    name,
                    "in",
                    fields[0].getDeclaringClass().getSimpleName() + ":");
            return null;
        }
    }
    //    default String deobfFieldInClass(String reobf){
    //        throw new  NotImplementedYet();
    //    }
    static class DefaultImpl extends SimpleObfManager.DefaultImpl implements ObfManager {
        public String deobfFieldInClass(String mojangClassName, String obfMethodDescriptor) {
            return ByteCodeUtils.parseFieldNameFromDescriptor(obfMethodDescriptor);
        }
    }
}
