package me.matl114.matlib.common.functions.core;

public interface UnsafeBiFunction<T, W, R> {
    public R applyUnsafe(T var1, W var2) throws Throwable;
}
