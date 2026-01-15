package me.matl114.matlib.utils.persistentDataContainer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

public class SerializeUtils {
    public static ItemStack deserializeItemStackInternal(String string) throws InvalidConfigurationException {
        if (string == null || "null".equals(string)) {
            return null;
        }
        YamlConfiguration itemConfig = new YamlConfiguration();
        itemConfig.loadFromString(string);
        // compat old method
        return itemConfig.getItemStack("item");
    }

    public static ItemStack deserializeItemStack(String string) {
        try {
            return deserializeItemStackInternal(string);
        } catch (Throwable e) {
            throw new RuntimeException("Unable to deserialize item: " + e.getMessage());
        }
    }

    public static String serializeItemStack(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return "null";
        }
        YamlConfiguration itemConfig = new YamlConfiguration();
        itemConfig.set("item", item);
        return itemConfig.saveToString();
    }

    public static Map<String, Object> deepSerialize(ConfigurationSerializable section) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("==", ConfigurationSerialization.getAlias(section.getClass()));
        for (var entry : section.serialize().entrySet()) {
            if (entry.getValue() instanceof ConfigurationSerializable serializable) {
                map.put(entry.getKey(), deepSerialize(serializable));
            } else {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return map;
    }

    public static ConfigurationSerializable deepDeserialize(Map<String, Object> map) {
        Map<String, Object> map2 = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof Map map1) {
                if (map1.containsKey("==")) {
                    map2.put(entry.getKey(), deepDeserialize(map1));
                    continue;
                }
            }
            map2.put(entry.getKey(), entry.getValue());
        }
        return ConfigurationSerialization.deserializeObject(map2);
    }

    public static <W extends ConfigurationSerializable> W deserializeFromBytes(byte[] bytes) {
        try (final ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
                final BukkitObjectInputStream bukkitObjectInputStream = new BukkitObjectInputStream(inputStream)) {
            return (W) bukkitObjectInputStream.readObject();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] serializeToBytes(ConfigurationSerializable serializable) {
        try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                final BukkitObjectOutputStream bukkitObjectOutputStream = new BukkitObjectOutputStream(outputStream)) {
            bukkitObjectOutputStream.writeObject(serializable);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
