package me.matl114.matlib.utils.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import me.matl114.matlib.utils.ConfigUtils;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.bukkit.BlockLocation;
import me.matl114.matlib.utils.persistentDataContainer.SerializeUtils;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public interface BukkitCodecs {
    Codec<NamespacedKey> NAMESPACED_KEY = Codec.STRING.xmap(NamespacedKey::fromString, NamespacedKey::toString);
    Codec<World> WORLD = Codec.STRING.xmap(Bukkit::getWorld, World::getName);

    private static DataResult<Location> locationFromString(String string) {
        try {
            if ("null".equals(string)) {
                return DataHelper.A.I.success(null);
            }
            String[] list = string.split(",");
            if (list.length != 4) return DataHelper.A.I.error(() -> "Not a valid location: " + string);
            String world = list[0];
            double x = Double.parseDouble(list[1]);
            double y = Double.parseDouble(list[2]);
            double z = Double.parseDouble(list[3]);
            return DataHelper.A.I.success(new Location(Bukkit.getWorld(world), x, y, z));
        } catch (Throwable e) {
            return DataHelper.A.I.error(() -> "Not a valid location: " + string);
        }
    }

    Codec<Location> LOCATION = Codec.STRING.comapFlatMap(BukkitCodecs::locationFromString, TextUtils::locationToString);

    private static DataResult<BlockLocation> blockLocationFromString(String string) {
        try {
            if (string == null || "null".equals(string)) {
                return DataHelper.A.I.success(null);
            }
            String[] list = string.split(",");
            if (list.length != 4) return DataHelper.A.I.error(() -> "Not a valid location: " + string);
            String world = list[0];
            int x = Integer.parseInt(list[1]);
            int y = Integer.parseInt(list[2]);
            int z = Integer.parseInt(list[3]);
            return DataHelper.A.I.success(new BlockLocation(Bukkit.getWorld(world), x, y, z));
        } catch (Throwable e) {
            return DataHelper.A.I.error(() -> "Not a valid location: " + string);
        }
    }

    Codec<BlockLocation> BLOCK_LOCATION =
            Codec.STRING.comapFlatMap(BukkitCodecs::blockLocationFromString, BlockLocation::toString);

    private static DataResult<ItemStack> deserializeItemStack(String yaml) {
        try {
            if (yaml == null || "null".equals(yaml)) {
                return DataHelper.A.I.success(new ItemStack(Material.AIR));
            }
            return DataHelper.A.I.success(SerializeUtils.deserializeItemStackInternal(yaml));
        } catch (Throwable e) {
            return DataHelper.A.I.error(() -> "Unable to deserialize item: " + e.getMessage());
        }
    }

    Codec<ItemStack> ITEMSTACK_STRING =
            Codec.STRING.comapFlatMap(BukkitCodecs::deserializeItemStack, SerializeUtils::serializeItemStack);
    Codec<ConfigurationSection> MEMORY_SECTION = Codec.PASSTHROUGH.comapFlatMap(
            dynamic -> {
                Object obj = dynamic.convert(ConfigOps.I).getValue();
                return obj instanceof ConfigurationSection sec
                        ? DataHelper.A.I.success(sec)
                        : DataHelper.A.I.error(() -> "Not a config: " + obj.toString());
            },
            sec -> new Dynamic<Object>(ConfigOps.I, ConfigUtils.copySection(sec)));

    private static DataResult<ItemStack> deserializeItemStack(Map<String, Object> map) {
        if (map.isEmpty()) return DataHelper.A.I.success(new ItemStack(Material.AIR));
        if (map.containsKey("==")) {
            ConfigurationSerializable serializable = SerializeUtils.deepDeserialize(map);
            return serializable instanceof ItemStack stack
                    ? DataHelper.A.I.success(stack)
                    : DataHelper.A.I.error(() -> "Not a item: " + map.toString());
        } else {
            return DataHelper.A.I.error(() -> "Not a configurationSerializable: " + map);
        }
    }

    private static Map<String, Object> serializeItemStack(ItemStack item) {
        if (item == null) return Map.of();
        return SerializeUtils.deepSerialize(item);
    }

    Codec<ItemStack> ITEMSTACK_MAP =
            TypeOps.MAP.comapFlatMap(BukkitCodecs::deserializeItemStack, BukkitCodecs::serializeItemStack);

    Codec<ItemStack> ITEMSTACK = Codec.withAlternative(ITEMSTACK_MAP, ITEMSTACK_STRING);

    //    private static DataResult<ItemStack> deserializeItemStack(ByteBuffer  buffer){
    //
    //    }
    //    Codec<ItemStack> ITEMSTACK_BYTE = Codec.BYTE_BUFFER.flatXmap(
    //        BukkitCodecs::deserializeItemStack,
    //    )

    //  Codec<YamlConfiguration> YAML = Codec.PASSTHROUGH;

}
