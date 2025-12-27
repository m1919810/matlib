package me.matl114.matlib.core;

import java.util.HashSet;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bukkit.plugin.Plugin;

public interface Manager {
    static HashSet<Manager> managers = new HashSet<>();
    static AtomicBoolean enable = new AtomicBoolean(false);

    static void onEnable() {
        managers.clear();
        enable.set(true);
    }

    static void onDisable() {
        new HashSet<>(managers).forEach(Manager::deconstruct);
        managers.clear();
        enable.set(false);
    }

    boolean isAutoDisable();

    default Manager addToRegistry() {
        // must called in init
        managers.add(this);
        return this;
    }

    default Manager removeFromRegistry() {
        // must called in deconstruct
        managers.remove(this);
        return this;
    }

    public <T extends Manager> T init(Plugin pl, String... path);

    public <T extends Manager> T reload();

    public void deconstruct();

    default void safeDeconstruct() {
        if (!isAutoDisable() || enable.get()) {
            deconstruct();
        }
    }
}
