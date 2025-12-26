package me.matl114.matlib.common.functions.core;

import java.util.function.Function;
import lombok.val;

public interface UnsafeFunction<W, T> {
    public T applyUnsafe(W val) throws Throwable;

    default Function<W, T> wrapToFunction() {
        return (val) -> {
            try {
                return this.applyUnsafe(val);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
