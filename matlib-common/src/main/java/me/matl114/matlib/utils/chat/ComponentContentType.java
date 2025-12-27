package me.matl114.matlib.utils.chat;

import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;

public enum ComponentContentType {
    LITERAL,
    SCORE,
    NBT,
    SELECTOR,
    TRANSLATABLE,
    KEYBIND,
    EMPTY;

    public static ComponentBuilder initBuilder(ComponentContentType type) {
        return switch (type) {
            case TRANSLATABLE -> Component.translatable();
            case LITERAL -> Component.text();
            case EMPTY -> null;
            case NBT -> throw new NotImplementedYet();
            case SCORE -> Component.score();
            case KEYBIND -> Component.keybind();
            case SELECTOR -> Component.selector();
        };
    }
}
