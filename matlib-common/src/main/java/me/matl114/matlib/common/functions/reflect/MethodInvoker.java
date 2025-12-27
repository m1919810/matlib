package me.matl114.matlib.common.functions.reflect;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public interface MethodInvoker<T> {
    public T invokeInternal(Object obj, Object... args) throws Throwable;

    default T invoke(Object obj, Object... args) {
        try {
            return invokeInternal(obj, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    static final Object[] NO_ARGUMENT = new Object[0];

    default T invokeNoArg(Object obj) {
        return invoke(obj, NO_ARGUMENT);
    }

    public static <T> MethodInvoker<T> ofSafe(BiFunction<Object, Object[], T> con) {
        return new MethodInvoker<T>() {
            @Override
            public T invokeInternal(Object obj, Object... args) throws Throwable {
                throw new IllegalStateException("Method shouldn't be called");
            }

            @Override
            public T invoke(Object obj, Object... args) {
                return con.apply(obj, args);
            }
        };
    }

    public static <T> MethodInvoker<T> ofNoArgs(Function<?, T> func) {
        Function<Object, T> fuck = (Function<Object, T>) func;
        return new MethodInvoker<T>() {
            @Override
            public T invokeInternal(Object obj, Object... args) throws Throwable {
                throw new IllegalStateException("Method shouldn't be called");
            }

            @Override
            public T invoke(Object obj, Object... args) {
                return fuck.apply(obj);
            }

            @Override
            public T invokeNoArg(Object obj) {
                return fuck.apply(obj);
            }
        };
    }

    public static <T> MethodInvoker<T> staticMethodAsFunc(Function<?, T> func) {
        Function<Object, T> fuck = (Function<Object, T>) func;
        return new MethodInvoker<T>() {
            @Override
            public T invokeInternal(Object obj, Object... args) throws Throwable {
                throw new IllegalStateException("Method shouldn't be called");
            }

            @Override
            public T invoke(Object obj, Object... args) {
                return fuck.apply(args[0]);
            }

            @Override
            public T invokeNoArg(Object obj) {
                return fuck.apply(null);
            }
        };
    }

    public static MethodInvoker<Void> ofNoArgsNoReturn(Consumer<?> task) {
        Consumer<Object> task0 = (Consumer<Object>) task;
        return new MethodInvoker<Void>() {
            @Override
            public Void invokeInternal(Object obj, Object... args) throws Throwable {
                throw new IllegalStateException("Method shouldn't be called");
            }

            @Override
            public Void invoke(Object obj, Object... args) {
                task0.accept(obj);
                return null;
            }

            @Override
            public Void invokeNoArg(Object obj) {
                task0.accept(obj);
                return null;
            }
        };
    }

    default <W> MethodInvoker<W> cast() {
        return (MethodInvoker<W>) this;
    }
}
