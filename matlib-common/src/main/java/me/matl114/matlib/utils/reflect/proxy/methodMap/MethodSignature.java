package me.matl114.matlib.utils.reflect.proxy.methodMap;

import java.lang.reflect.Method;
import java.util.Arrays;

public record MethodSignature(String methodName, Class<?>[] parameterTypes) {
    public static MethodSignature getSignature(Method method) {
        return new MethodSignature(method.getName(), method.getParameterTypes());
    }

    public int getParameterCount() {
        return parameterTypes.length;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MethodSignature sig
                && methodName.equals(sig.methodName)
                && Arrays.equals(parameterTypes, sig.parameterTypes);
    }

    @Override
    public int hashCode() {
        return methodName.hashCode() * 67 + parameterTypes.length;
    }

    public static int getHash(Method method) {
        return method.getName().hashCode() * 67 + method.getParameterCount();
    }

    public boolean ofSameSignature(Method method) {
        return methodName.equals(method.getName()) && Arrays.equals(parameterTypes, method.getParameterTypes());
    }

    @Override
    public String toString() {
        return new StringBuilder("MethodSignature{ method = ")
                .append(methodName)
                .append(", parameters = ")
                .append(Arrays.toString(parameterTypes))
                .append(" }")
                .toString();
    }
}
