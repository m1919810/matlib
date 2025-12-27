package me.matl114.matlib.algorithms.dataStructures.frames.initBuidler;

import java.util.function.Supplier;

public class InitializeSafeProvider<T extends Object> {
    boolean noError = true;
    T value;

    public InitializeSafeProvider(Class<T> clazz, Supplier<T> value) {
        this(ofSafe(value));
    }

    public InitializeSafeProvider(SuppilerWithError<T> provider) {
        this(provider, (T) null);
    }

    public InitializeSafeProvider(SuppilerWithError<T> provider, T defaultValue) {
        try {
            value = provider.get();
        } catch (Throwable allUnhandledException) {
            value = defaultValue;
            noError = false;
        }
    }

    public T v() {
        return value;
    }

    public static interface SuppilerWithError<T extends Object> {
        public T get() throws Throwable;
    }

    public static <W extends Object> SuppilerWithError<W> ofSafe(Supplier<W> supplier) {
        return (SuppilerWithError<W>) supplier::get;
        //        new SuppilerWithError<W>() {
        //            @Override
        //            public W get() throws Throwable {
        //                return supplier.get();
        //            }
        //        };
    }

    public InitializeSafeProvider<T> runNonnullAndNoError(Runnable runnable) {
        if (noError && value != null) runnable.run();
        return this;
    }

    public InitializeSafeProvider<T> runNullOrError(Runnable runnable) {
        if (!noError || value == null) runnable.run();
        return this;
    }

    public InitializeSafeProvider<T> runError(Runnable runnable) {
        if (!noError) runnable.run();
        return this;
    }
}
