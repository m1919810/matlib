package me.matl114.matlib.utils.chat.lan.i18n;

import static me.matl114.matlib.utils.chat.lan.TranslateKeyUtils.*;

import java.util.Objects;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public class DefaultLocalizationHelper implements RegistryLocalizationHelper {
    public String getItemName(ItemStack itemStack) {
        return getMaterialName(Objects.requireNonNull(itemStack).getType());
    }

    public String getMaterialName(Material material) {
        return getMaterialTranslateDefault(material);
    }

    public String getAttributeName(Attribute attribute) {
        return getAttributeTranslationDefault(attribute);
    }

    public String getBiomeName(Biome biome) {
        return getKeyedTranslationDefault("biome.minecraft", biome);
    }

    public String getEnchantmentName(Enchantment ench) {
        return getEnchantmentTranslationDefault(ench);
    }

    public String getEntityTypeName(EntityType entity) {
        return getEntityTranslationDefault(entity);
    }

    public String getPotionEffectTypeName(PotionEffectType type) {
        return getPotionTranslationDefault(type);
    }

    public String getDyeColorName(DyeColor color) {
        return getDyeColorTranslationDefault(color);
    }
}
