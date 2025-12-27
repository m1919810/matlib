package me.matl114.matlib.utils.version;

import java.util.function.Consumer;
import me.matl114.matlib.utils.WorldUtils;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.CreatureSpawnEvent;

public abstract class VersionedWorld {
    private static VersionedWorld Instance;

    public static VersionedWorld getInstance() {
        if (Instance == null) {
            init0();
        }
        return Instance;
    }

    private static void init0() {
        Instance = switch (Version.getVersionInstance()) {
            case v1_20_R4, v1_21_R1, v1_21_R2 -> new v1_20_R4();
            case v1_20_R3 -> new v1_20_R3();
            default -> new Default();};
    }

    public BlockState copyBlockStateTo(BlockState state1, Block target) {
        return WorldUtils.copyBlockState(state1, target);
    }

    public <T extends Entity> T spawnEntity(
            Location location, Class<T> clazz, Consumer<T> consumer, CreatureSpawnEvent.SpawnReason reason) {
        T val = location.getChunk().getWorld().spawn(location, clazz, reason);
        consumer.accept(val);
        return val;
    }

    static class Default extends VersionedWorld {}

    static class v1_20_R4 extends v1_20_R3 {
        public BlockState copyBlockStateTo(BlockState state1, Block target) {
            try {
                BlockState newBlockStae = state1.copy(target.getLocation());
                newBlockStae.update(true, false);
                return newBlockStae;
            } catch (Throwable e) {
                return super.copyBlockStateTo(state1, target);
            }
        }
    }

    static class v1_20_R3 extends Default {
        public <T extends Entity> T spawnEntity(
                Location location, Class<T> clazz, Consumer<T> consumer, CreatureSpawnEvent.SpawnReason reason) {
            return location.getChunk().getWorld().spawn(location, clazz, consumer, reason);
        }
    }
}
