package me.matl114.matlib.core.bukkit.entity.groups.implement;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroup;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroupManager;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;

public class SimpleGroupManager<W extends EntityGroup<? extends Entity>> implements EntityGroupManager<W> {
    public SimpleGroupManager(Map<String, W> groups) {
        this.groups = groups;
    }

    public SimpleGroupManager() {
        this.groups = new HashMap<String, W>();
    }

    final Map<String, W> groups;

    @Override
    public void addGroup(String key, W group) {
        groups.put(key, group);
    }

    @Override
    public W getGroup(String key) {
        return groups.get(key);
    }

    @Override
    public W removeGroup(String key) {
        return groups.remove(key);
    }

    @Override
    public Iterator<W> getGroups() {
        return groups.values().iterator();
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
