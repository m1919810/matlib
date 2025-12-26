package me.matl114.matlib.utils.entity;

import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public class EntityUtils {
    /**
     * search in range to find Entity Target,used when Entity can't move by themselves
     * @param entity
     * @param execution
     * @return
     */
    public static boolean executeOnSameEntity(Entity entity, Consumer<Entity> execution) {
        if (entity == null) return false;
        return !entity.getLocation()
                        .getChunk()
                        .getWorld()
                        .getNearbyEntities(entity.getLocation(), 0.25, 0.25, 0.25, (entity1 -> {
                            if (entity1 != null
                                    && entity1.isValid()
                                    && entity.getUniqueId().equals(entity1.getUniqueId())) {
                                execution.accept(entity1);
                                return true;
                            }
                            return false;
                        }))
                        .isEmpty()
                || executeOnSameEntity(entity.getUniqueId(), execution);
    }

    /**
     * using UID to find Entity target
     * @param entity
     * @param execution
     * @return
     */
    public static boolean executeOnSameEntity(UUID entity, Consumer<Entity> execution) {
        Entity entity1 = Bukkit.getEntity(entity);
        if (entity1 == null || !entity1.isValid()) return false;
        execution.accept(entity1);
        return true;
    }
}
