package me.matl114.matlib.common.functions.reflect;

public interface FieldAccessor<T extends Object> {
    public void set(Object obj, T value);

    public T get(Object obj);

    public static <T> FieldAccessor<T> fromGetterMethod(MethodInvoker<T> getter) {
        return new FieldAccessor<T>() {
            @Override
            public void set(Object obj, T value) {
                throw new RuntimeException("Setting field is not allowed in this FieldAccessor");
            }

            @Override
            public T get(Object obj) {
                return getter.invokeNoArg(obj);
            }
        };
    }

    default <W extends Object> FieldAccessor<W> safeCast() {
        return (FieldAccessor<W>) this;
    }
}
