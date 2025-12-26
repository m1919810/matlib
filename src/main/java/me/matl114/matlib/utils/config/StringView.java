package me.matl114.matlib.utils.config;

public class StringView implements DataView<String> {
    private final NodeReference<String> parent;

    public StringView(NodeReference<String> parent) {
        this.parent = parent;
    }

    @Override
    public String get() {
        return parent.get();
    }

    @Override
    public NodeReference<String> getDelegate() {
        return parent;
    }

    @Override
    public Type getType() {
        return Type.STRING;
    }

    @Override
    public String getAsString() {
        return parent.get();
    }

    @Override
    public boolean setString(String val) {
        return parent.set(val);
    }
}
