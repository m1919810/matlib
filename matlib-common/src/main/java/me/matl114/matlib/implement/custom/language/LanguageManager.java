package me.matl114.matlib.implement.custom.language;

import me.matl114.matlib.core.Manager;
import org.bukkit.plugin.Plugin;

public class LanguageManager implements Manager {

    @Override
    public boolean isAutoDisable() {
        return true;
    }

    @Override
    public LanguageManager init(Plugin pl, String... path) {
        return this;
    }

    @Override
    public LanguageManager reload() {
        return null;
    }

    @Override
    public void deconstruct() {}
}
