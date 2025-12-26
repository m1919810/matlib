package me.matl114.matlib.utils.config;

import java.util.List;
import me.matl114.matlib.common.lang.exceptions.DoNotCallException;

public class ListView implements DataView<List> {
    List<String> cache;
    private final NodeReference<List> parent;
    boolean initialize = false;

    public ListView(NodeReference<List> configNode) {
        this.parent = configNode;
    }

    @Override
    public List get() {
        return parent.get();
    }

    @Override
    public NodeReference<List> getDelegate() {
        return parent;
    }

    @Override
    public Type getType() {
        return Type.STRING_LIST;
    }

    @Override
    public boolean setString(String val) {
        throw new DoNotCallException();
    }

    public List<String> getAsList() {
        if (!initialize) {
            cache = (List<String>) parent.get();
            initialize = false;
        }
        return cache;
    }

    @Override
    public boolean setList(List<String> val) {
        initialize = true;
        cache = val;
        return parent.set(val);
    }
}
