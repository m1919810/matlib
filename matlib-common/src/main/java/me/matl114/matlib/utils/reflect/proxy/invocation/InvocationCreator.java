package me.matl114.matlib.utils.reflect.proxy.invocation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public interface InvocationCreator {
    default InvocationHandler bindTo(Object delegate) {
        return ((proxy, method, args) -> invoke(proxy, delegate, method, args));
    }

    Object invoke(Object proxy, Object delegate, Method method, Object[] args) throws Throwable;
}
