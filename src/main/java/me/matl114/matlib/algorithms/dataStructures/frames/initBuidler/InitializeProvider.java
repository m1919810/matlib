package me.matl114.matlib.algorithms.dataStructures.frames.initBuidler;

import java.util.function.Supplier;

public class InitializeProvider<T extends Object> {
    T value;

    public InitializeProvider(Supplier<T> provider) {
        value = provider.get();
    }

    public T v() {
        return value;
    }
}
