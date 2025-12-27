package me.matl114.matlib.algorithms.dataStructures.struct;

@SuppressWarnings("all")
public class LazyInitReference<T extends Object> implements Cloneable {
    public T value;
    public boolean init;
    private static LazyInitReference INSTANCE = new LazyInitReference();

    private LazyInitReference() {}

    public static <W extends Object> LazyInitReference<W> ofEmpty() {
        return INSTANCE.clone();
    }

    public static <W extends Object> LazyInitReference<W> ofValue(W value) {
        LazyInitReference<W> result = INSTANCE.clone();
        result.value = value;
        result.init = true;
        return result;
    }

    @Override
    public LazyInitReference clone() {
        try {
            LazyInitReference clone = (LazyInitReference) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
