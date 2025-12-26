package me.matl114.matlib.algorithms.dataStructures.frames.mmap;

public abstract class ValueAccess<T> {
    boolean validBit;
    T cache;

    public abstract T get0();

    public T get() {
        if (validBit) {
            return cache;
        } else {
            cache = get0();
            return cache;
        }
    }

    public abstract void set0(T val);

    public void set(T value) {
        validBit = true;
        cache = value;
        set0(value);
    }

    public void flush() {
        validBit = false;
    }
}
