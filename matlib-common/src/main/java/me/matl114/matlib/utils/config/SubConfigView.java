package me.matl114.matlib.utils.config;

import java.util.Map;
import me.matl114.matlib.common.lang.annotations.Note;

@Note("you can implement from this class to make custom codec of sub config tree")
public class SubConfigView implements DataView<Config> {
    private final NodeReference<Map> parent;
    // private final Config map
    public SubConfigView(NodeReference<Map> innerNode) {
        this.parent = innerNode;
        // todo subConfig
    }

    @Override
    public Config get() {
        return null;
    }

    @Override
    public NodeReference getDelegate() {
        return parent;
    }

    @Override
    public Type getType() {
        return Type.SUB_CONFIG_VIEW;
    }

    @Override
    public boolean setString(String val) {
        return false;
    }
}
