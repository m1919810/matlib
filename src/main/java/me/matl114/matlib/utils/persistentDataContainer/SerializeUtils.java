package me.matl114.matlib.utils.persistentDataContainer;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class SerializeUtils {
    public static ItemStack deserializeItemStack(String string) {
        try {
            YamlConfiguration itemConfig = new YamlConfiguration();
            itemConfig.loadFromString(string);
            return itemConfig.getItemStack("item");
        } catch (Throwable e) {
            throw new RuntimeException("Unable to deserialize item: " + e.getMessage());
        }
    }

    public static String serializeItemStack(ItemStack item) {
        YamlConfiguration itemConfig = new YamlConfiguration();
        itemConfig.set("item", item);
        return itemConfig.saveToString();
    }
}
