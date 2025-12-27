package me.matl114.matlib.utils.chat.lan.i18n;

import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

public interface RegistryLocalizationHelper {
    String getItemName(ItemStack itemStack);

    String getMaterialName(Material material);

    String getAttributeName(Attribute attribute);

    String getBiomeName(Biome biome);

    String getEnchantmentName(Enchantment ench);

    String getEntityTypeName(EntityType entity);

    String getPotionEffectTypeName(PotionEffectType type);

    String getDyeColorName(DyeColor color);
}
