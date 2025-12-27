package me.matl114.matlib.utils.config;

public class ObjectView<T> implements DataView<T> {
    private final NodeReference<T> parent;

    public ObjectView(NodeReference<T> parent) {
        this.parent = parent;
    }

    @Override
    public T get() {
        return this.parent.get();
    }

    public boolean set(T value) {
        return this.parent.set(value);
    }

    @Override
    public NodeReference<T> getDelegate() {
        return parent;
    }

    @Override
    public Type getType() {
        return parent.get() == null ? Type.NULL : Type.UNDEFINED;
    }

    @Override
    public boolean setString(String val) {
        return false;
    }
}
