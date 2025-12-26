package me.matl114.matlib.common.functions.reflect;

public interface FieldSetter<T, W> {
    void setField(W base, T fieldValue);

    static interface StaticFieldSetter<T> {
        void set(T val);

        default FieldSetter<T, Void> toCommon() {
            return (b, f) -> set(f);
        }
    }
}
