package me.matl114.matlib.utils.config;

public class BoolView implements DataView<Boolean> {
    private final NodeReference<Boolean> parent;

    public BoolView(NodeReference<Boolean> parent) {
        this.parent = parent;
    }

    public boolean getAsBoolean() {
        return parent.get();
    }

    @Override
    public boolean setString(String val) {
        return parent.set(Boolean.parseBoolean(val));
    }

    @Override
    public Boolean get() {
        return parent.get();
    }

    @Override
    public NodeReference<Boolean> getDelegate() {
        return parent;
    }

    @Override
    public Type getType() {
        return Type.BOOLEAN;
    }
}
