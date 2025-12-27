package me.matl114.matlib.utils.config;

public interface DataView<T> extends View {
    public T get();
    //    public boolean set(T value);
    NodeReference<T> getDelegate();

    public Type getType();

    boolean setString(String val);

    static enum Type {
        INT_RANGE,
        LONG_RANGE,
        FLOAT_RANGE,
        BOOLEAN,
        STRING,
        STRING_LIST,
        SUB_CONFIG_VIEW,
        NULL,
        CUSTOM_VIEW,
        UNDEFINED;
    }

    default String getAsString() {
        return String.valueOf(get());
    }

    default DataView<T> validate(Class<?> clazz) {
        if (clazz == this.getClass()) {
            return this;
        } else {
            throw new IllegalArgumentException("Type view mismatch in config value!");
        }
    }
}
