package me.matl114.matlib.core.bukkit.entity.groups.implement;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroup;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroupManager;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class SingleGroupManager<W extends EntityGroup<? extends Entity>> implements EntityGroupManager<W> {
    public SingleGroupManager() {
        this.handle = null;
    }

    @Setter
    @Getter
    W handle;

    @Override
    public void addGroup(String key, W group) {
        handle = group;
    }

    @Override
    public W getGroup(String key) {
        return handle;
    }

    @Override
    public W removeGroup(String key) {
        W group = handle;
        handle = null;
        return group;
    }

    @Override
    public Iterator<W> getGroups() {
        Set<W> set = new HashSet<W>();
        set.add(handle);
        return set.iterator();
    }

    @Override
    public EntityGroupManager<W> init(Plugin pl, String... path) {
        return this;
    }

    @Override
    public EntityGroupManager<W> reload() {
        return this;
    }

    @Override
    public boolean isAutoDisable() {
        return false;
    }
}
