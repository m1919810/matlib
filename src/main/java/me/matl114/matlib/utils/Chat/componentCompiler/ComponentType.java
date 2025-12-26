package me.matl114.matlib.utils.chat.componentCompiler;

import lombok.Getter;

public enum ComponentType {
    TEXT,
    TRANSLATABLE,
    SCORE,
    ENTITY,
    KEYBIND,
    NBT,
    CUSTOM,
    FORMAT(false, true),
    HOVER(true),
    CLICK(true),
    INSERT(true);

    @Getter
    final boolean attach;

    @Getter
    final boolean prefix;

    ComponentType() {
        this.attach = false;
        this.prefix = false;
    }

    ComponentType(boolean attach) {
        this.attach = attach;
        this.prefix = false;
    }

    ComponentType(boolean attach, boolean prefix) {
        this.attach = attach;
        this.prefix = prefix;
    }
}
