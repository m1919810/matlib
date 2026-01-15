package me.matl114.matlib.utils;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemoryConfiguration;
import org.bukkit.configuration.MemorySection;

public class ConfigUtils {

    public static MemorySection copySection(ConfigurationSection sec) {
        MemorySection map2 = new MemoryConfiguration();
        copySection(map2, sec);
        return map2;
    }

    public static void copySection(ConfigurationSection map2, ConfigurationSection sec) {
        for (var key : sec.getKeys(false)) {
            map2.set(key, sec.get(key));
        }
    }
}
