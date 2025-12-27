package me.matl114.matlib.common.functions.reflect;

public interface FieldGetter<T extends Object, W extends Object> {
    public T getField(W o);

    public static interface StaticFieldGetter<T extends Object> {
        T get();

        default FieldGetter<T, Void> toCommon() {
            return (o) -> get();
        }
    }
}
