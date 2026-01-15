package me.matl114.matlib.utils.reflect.descriptor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

public class DescriptorBuilder {
    public static <T extends TargetDescriptor> T createASMHelperImpl(Class<T> type) {
        Class<?> clazz;
        try {
            clazz = Class.forName(DescriptorBuilder.class.getPackageName() + ".DescriptorImplBuilder");
        } catch (ClassNotFoundException e) {
            return null;
        }
        try {
            Method method = clazz.getMethod("createHelperImpl", Class.class);
            method.setAccessible(true);
            return (T) method.invoke(null, type);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException | NoSuchMethodException e) {
            return null;
        }
    }

    public static <T extends TargetDescriptor> T createASMMultiHelper(Class<T> descriptiveInterface) {
        Class<?> clazz;
        try {
            clazz = Class.forName(DescriptorBuilder.class.getPackageName() + ".DescriptorImplBuilder");
        } catch (ClassNotFoundException e) {
            return null;
        }
        try {
            Method method = clazz.getMethod("createMultiHelper", Class.class);
            method.setAccessible(true);
            return (T) method.invoke(null, descriptiveInterface);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getTargetException());
        } catch (IllegalAccessException | NoSuchMethodException e) {
            return null;
        }
    }

    public static <T extends TargetDescriptor> T createProxyHelperImpl(Class<T> type) {
        return DescriptorProxyBuilder.createHelperImpl(type);
    }

    public static <T extends TargetDescriptor> T createProxyMultiHelperImpl(Class<T> type) {
        return DescriptorProxyBuilder.createMultiHelper(type);
    }
}
