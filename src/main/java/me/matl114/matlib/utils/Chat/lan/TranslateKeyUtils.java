package me.matl114.matlib.utils.chat.lan;

import com.google.common.base.Preconditions;
import java.util.concurrent.ConcurrentHashMap;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TranslatableComponent;
import org.bukkit.DyeColor;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

public class TranslateKeyUtils {
    private static final ThreadLocal<ItemMeta> TEMPLATE_STACK =
            ThreadLocal.withInitial(() -> new ItemStack(Material.STONE).getItemMeta());
    private static final ConcurrentHashMap<String, String> CACHED_TRANSLATION = new ConcurrentHashMap<>();

    private static String translateKeyToDefault(String resourceKey) {
        return CACHED_TRANSLATION.computeIfAbsent(resourceKey, key -> {
            TranslatableComponent component = Component.translatable(key, key);
            ItemMeta meta = TEMPLATE_STACK.get();
            meta.displayName(component);
            return meta.getDisplayName();
        });
    }

    public static String getMaterialTranslateDefault(Material mat) {
        Preconditions.checkArgument(mat != null, "材料不能为空");
        String type = mat.isBlock() ? "block" : "item";
        String key =
                type + "." + mat.getKey().getNamespace() + "." + mat.getKey().getKey();
        return translateKeyToDefault(key);
    }

    public static String getEntityTranslationDefault(EntityType entityType) {
        Preconditions.checkArgument(entityType != null, "实体类型不能为空");
        Preconditions.checkArgument(entityType != EntityType.UNKNOWN, "实体类型不能为无效类型");
        String val = "entity.minecraft." + entityType.getName().toLowerCase();
        return translateKeyToDefault(val);
    }

    public static String getPotionTranslationDefault(PotionEffectType type) {
        Preconditions.checkArgument(type != null, "药水效果不能为空");
        String key = type.getKey().getKey();
        String val = "effect.minecraft." + key;
        return translateKeyToDefault(val);
    }

    public static String getDyeColorTranslationDefault(DyeColor dyeColor) {
        Preconditions.checkArgument(dyeColor != null, "染料颜色不能为空");
        String val = "color.minecraft." + dyeColor.toString().toLowerCase();
        return translateKeyToDefault(val);
    }

    public static String getEnchantmentTranslationDefault(Enchantment enchantment) {
        return translateKeyToDefault(
                "enchantment.minecraft." + enchantment.getKey().getKey());
    }

    public static String getAttributeTranslationDefault(Attribute attribute) {
        return getKeyedTranslationDefault("attribute.name", attribute);
    }

    public static String getKeyedTranslationDefault(String prefix, Keyed keyed) {
        return translateKeyToDefault(prefix + "." + keyed.getKey().getKey());
    }
}
