package me.matl114.matlib.nmsUtils;

import static me.matl114.matlib.nmsMirror.impl.CraftBukkit.*;
import static me.matl114.matlib.nmsMirror.impl.NMSLevel.*;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.matl114.matlib.nmsMirror.impl.NMSEntity;
import me.matl114.matlib.utils.WorldUtils;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class PlayerUtils {
    public static Collection<Entity> getViewableEntity(Player player) {
        Object nms = ENTITY.getHandle(player);
        Object conn = PLAYER.connectionGetter(nms);
        World world = player.getWorld();
        Object worldHandle = WorldUtils.getHandledWorld(world);
        Object chunkCache = LEVEL.getChunkSource(worldHandle);
        Object entityMap = CHUNK_CACHE_SYSTEM.chunkMapGetter(chunkCache);
        Int2ObjectMap<?> entityMap0 = CHUNK_MAP.entityMapGetter(entityMap);
        Set<Entity> entities = new HashSet<>();
        for (var entry : entityMap0.int2ObjectEntrySet()) {
            Object tracker = entry.getValue();
            ReferenceOpenHashSet<?> seen = CHUNK_MAP.seenByGetter(tracker);
            if (seen.contains(conn)) {
                // in view range
                Object entity = CHUNK_MAP.entityGetter(tracker);
                Entity bukkit = NMSEntity.ENTITY.getBukkitEntity(entity);
                if (player.canSee(bukkit)) {
                    entities.add(bukkit);
                }
            }
        }
        return entities;
    }
}
