package me.matl114.matlib.utils.version;

import java.util.*;
import javax.annotation.Nonnull;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffectType;

public abstract class VersionedRegistry {

    private static VersionedRegistry Instance;

    public static VersionedRegistry getInstance() {
        if (Instance == null) {
            init0();
        }
        return Instance;
    }

    protected final Map<String, String> remappingEnchantId = new HashMap<>();
    protected final HashMap<String, String> remappingMaterialId = new HashMap<>();
    protected final HashMap<String, String> remappingEntityId = new HashMap<>();

    protected String convertLegacyEnchantment0(String from) {
        if (from == null) {
            return null;
        } else {
            switch (from.toLowerCase()) {
                case "damage_all":
                    return "sharpness";
                case "arrow_fire":
                    return "flame";
                case "protection_explosions":
                    return "blast_protection";
                case "sweeping_edge":
                    return "sweeping";
                case "oxygen":
                    return "respiration";
                case "protection_projectile":
                    return "projectile_protection";
                case "loot_bonus_blocks":
                    return "fortune";
                case "water_worker":
                    return "aqua_affinity";
                case "arrow_damage":
                    return "power";
                case "luck":
                    return "luck_of_the_sea";
                case "damage_arthropods":
                    return "bane_of_arthropods";
                case "damage_undead":
                    return "smite";
                case "durability":
                    return "unbreaking";
                case "arrow_knockback":
                    return "punch";
                case "arrow_infinite":
                    return "infinity";
                case "loot_bonus_mobs":
                    return "looting";
                case "protection_environmental":
                    return "protection";
                case "dig_speed":
                    return "efficiency";
                case "protection_fall":
                    return "feather_falling";
                case "protection_fire":
                    return "fire_protection";
            }

            return from;
        }
    }

    protected final HashMap<NamespacedKey, NamespacedKey> remappingPotionEffect = new HashMap<>();

    public abstract Enchantment getEnchantment(String name);

    public static Enchantment enchantment(String v) {
        return getInstance().getEnchantment(v);
    }

    public abstract Material getMaterial(String name);

    public static Material material(String name) {
        return getInstance().getMaterial(name);
    }

    public abstract EntityType getEntityType(String name);

    public static EntityType entityType(String na) {
        return getInstance().getEntityType(na);
    }

    public abstract PotionEffectType getPotionEffectType(String key);

    public static PotionEffectType potionEffectType(String key) {
        return getInstance().getPotionEffectType(key);
    }

    protected Map<String, Attribute> remappingAttribute = new LinkedHashMap<>();

    public abstract Attribute getAttribute(String key);

    public static Attribute attribute(String key) {
        return getInstance().getAttribute(key);
    }

    public abstract Collection<Attribute> getAttributes();

    static class Default extends VersionedRegistry {
        {
            Attribute[] values;
            try {
                values = (Attribute[]) Attribute.class.getMethod("values").invoke(null);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            for (Attribute attr : values) {
                Keyed keyed = (Keyed) attr;
                String key = keyed.getKey().getKey();
                String[] val = key.split("\\.");
                if (val.length == 2) {
                    remappingAttribute.put(val[1], attr);
                } else {
                    remappingAttribute.put(key, attr);
                }
            }
        }

        @Override
        public Enchantment getEnchantment(String name) {
            name = this.convertLegacyEnchantment0(name);
            return Enchantment.getByName(remappingEnchantId.getOrDefault(name, name));
        }

        @Override
        public Material getMaterial(String name) {
            return Material.getMaterial(remappingMaterialId.getOrDefault(name, name));
        }

        @Override
        public EntityType getEntityType(String name) {
            name = name.toLowerCase(Locale.ROOT);
            return EntityType.fromName(remappingEntityId.getOrDefault(name, name));
        }

        protected PotionEffectType getPotionEffectByNSK(NamespacedKey key) {
            return PotionEffectType.getByKey(remappingPotionEffect.getOrDefault(key, key));
        }

        @Override
        public PotionEffectType getPotionEffectType(String key) {
            String[] splited = key.split(":");
            if (splited.length == 2) {
                return getPotionEffectByNSK(new NamespacedKey(splited[0], splited[1]));
            } else {
                return getPotionEffectByNSK(NamespacedKey.minecraft(key));
            }
        }

        @Override
        public Attribute getAttribute(String key) {
            return remappingAttribute.get(key);
        }

        @Override
        public Collection<Attribute> getAttributes() {
            return remappingAttribute.values();
        }
    }

    static class v1_20_R1 extends Default {
        private static String convertToLegacy(String from) {
            if (from == null) {
                return null;
            } else {
                switch (from.toLowerCase()) {
                    case "sharpness":
                        return "damage_all";
                    case "flame":
                        return "arrow_fire";
                    case "blast_protection":
                        return "protection_explosions";
                    case "sweeping":
                        return "sweeping_edge";
                    case "respiration":
                        return "oxygen";
                    case "projectile_protection":
                        return "protection_projectile";
                    case "fortune":
                        return "loot_bonus_blocks";
                    case "aqua_affinity":
                        return "water_worker";
                    case "power":
                        return "arrow_damage";
                    case "luck_of_the_sea":
                        return "luck";
                    case "bane_of_arthropods":
                        return "damage_arthropods";
                    case "smite":
                        return "damage_undead";
                    case "unbreaking":
                        return "durability";
                    case "punch":
                        return "arrow_knockback";
                    case "infinity":
                        return "arrow_infinite";
                    case "looting":
                        return "loot_bonus_mobs";
                    case "protection":
                        return "protection_environmental";
                    case "efficiency":
                        return "dig_speed";
                    case "feather_falling":
                        return "protection_fall";
                    case "fire_protection":
                        return "protection_fire";
                }
                return from;
            }
        }

        private static String convertToBelow1_20_R1(@Nonnull String name) {
            return convertToLegacy(name).toUpperCase(Locale.ROOT);
        }

        @Override
        public Enchantment getEnchantment(String name) {
            name = convertToBelow1_20_R1(name);
            return Enchantment.getByName(remappingEnchantId.getOrDefault(name, name));
        }
    }

    static class v1_20_R3 extends v1_20_R1 {
        {
            remappingMaterialId.put("grass", "short_grass");
        }

        public Enchantment getEnchantment(String name) {
            name = convertLegacyEnchantment0(name);
            return Enchantment.getByName(remappingEnchantId.getOrDefault(name, name));
        }
    }

    static class v1_20_R4 extends v1_20_R3 {
        {
            remappingMaterialId.put("SCUTE", "TURTLE_SCUTE");
            remappingEntityId.put("mushroom_cow", "mooshroom");
            remappingEntityId.put("snowman", "snow_golem");
        }
    }

    public static void init0() {
        Instance = switch (Version.getVersionInstance()) {
            case v1_20_R1, v1_20_R2 -> new v1_20_R1();
            case v1_20_R3 -> new v1_20_R3();
            case v1_20_R4, v1_21_R1, v1_21_R2 -> new v1_20_R4();
            default -> new Default();};
    }
}
