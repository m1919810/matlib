package me.matl114.matlib.algorithms.dataStructures.struct;

import java.util.function.Supplier;

@SuppressWarnings("all")
public class LazyInitValue<T extends Object> implements Cloneable {
    private T value;
    private boolean initialized;
    private Supplier<T> supplier;
    private static LazyInitValue INSTANCE = new LazyInitValue();
    private static Supplier<?> SUPPLIER = () -> null;

    private LazyInitValue() {
        value = null;
        initialized = false;
        supplier = (Supplier<T>) SUPPLIER;
    }

    public static <W extends Object> LazyInitValue<W> ofDirect(W valuy) {
        LazyInitValue<W> value = INSTANCE.clone();
        value.initialized = true;
        value.value = valuy;
        return value;
    }

    public static <W extends Object> LazyInitValue<W> ofLazy(Supplier<W> valueGetter) {
        LazyInitValue<W> value = INSTANCE.clone();
        value.supplier = valueGetter;
        return value;
    }

    public LazyInitReference<T> toReference() {
        return initialized ? LazyInitReference.ofValue(value) : LazyInitReference.ofEmpty();
    }

    public T get() {
        if (!initialized) {
            value = supplier.get();
            initialized = true;
        }
        return value;
    }

    public void set(T value) {
        initialized = true;
        this.value = value;
    }

    @Override
    public LazyInitValue clone() {
        try {
            LazyInitValue clone = (LazyInitValue) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
