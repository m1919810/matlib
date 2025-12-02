package me.matl114.matlib.nmsMirror.craftbukkit.inventory;

import me.matl114.matlib.nmsMirror.Import;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Map;

@Descriptive(target = "org.bukkit.craftbukkit.inventory.CraftItemStack")
public interface CraftItemStackHelper extends TargetDescriptor {
    @ConstructorTarget
    ItemStack createCraftItemStack(Material type, int amount, short durability, ItemMeta meta);
    default ItemStack createCraftItemStack(Material type, int amount, ItemMeta meta){
        return createCraftItemStack(type, amount, (short) 0, meta);
    }
    default ItemStack createCraftItemStack(Material type, int amount){
        return createCraftItemStack(type, amount, null);
    }
    @FieldTarget
    @RedirectType(Import.ItemStack)
    Object handleGetter(ItemStack cis);

    @MethodTarget(isStatic = true)
    @RedirectName("unwrap")
    @Note("copy only when bukkit is not a craftItemStack")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R2, below = true)
    default Object unwrapToNMS(@Nonnull ItemStack bukkit){
        if(isCraftItemStack(bukkit)){
            Object nms = handleGetter(bukkit);
            return nms == null? EmptyEnum.EMPTY_ITEMSTACK: nms;
        }else {
            return asNMSCopy(bukkit);
        }
    }

    @Note("success only after the item-api change")
    @MethodTarget(isStatic = true)
    @IgnoreFailure(thresholdInclude = Version.v1_20_R1)
    default ItemStack getCraftStack(@Nonnull ItemStack origin){
        if(isCraftItemStack(origin)){
            return origin;
        }else {
            return asCraftCopy(origin);
        }
    }
    @CastCheck("org.bukkit.craftbukkit.inventory.CraftItemStack")
    boolean isCraftItemStack(ItemStack stack);

    @MethodTarget(isStatic = true)
    @Note("create a nmsItem copy")
    Object asNMSCopy(ItemStack origin);

    @MethodTarget(isStatic = true)
    @Note("copy nmsItem with amount")
    Object copyNMSStack(@RedirectType(Import.ItemStack) Object original, int amount);

    @MethodTarget(isStatic = true)
    @Note("create a strictly-Bukkit stack(or same as craftMirror upper 1_20_R4)")
    ItemStack asBukkitCopy(@RedirectType(Import.ItemStack) Object original);

    @MethodTarget(isStatic = true)
    @Note("create a CraftItemStack mirroring nmsItemStack")
    ItemStack asCraftMirror(@RedirectType(Import.ItemStack) Object original);

    @MethodTarget(isStatic = true)
    @Note("create a CraftItemStack copying original")
    ItemStack asCraftCopy(ItemStack original);

    @MethodTarget(isStatic = true)
    @Note("new CraftItemStack using Item Registry")
    ItemStack asNewCraftStack(@RedirectType(Import.Item)Object item, int amount);

//    @MethodTarget(isStatic = true)
//    @Note("translate Enchantment nbt to bukkit, ImmutableMap")
//    Map<Enchantment, Integer> getEnchantments(@RedirectType(Import.ItemStack) Object item);

    @MethodTarget(isStatic = true)
    boolean hasItemMeta(@RedirectType(Import.ItemStack)Object nmsItem);

    @MethodTarget(isStatic = true)
    @Note("create empty CompoundTag if absent, useless when upper 1_20_R4")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    boolean makeTag(@RedirectType(Import.ItemStack) Object nms);

    @MethodTarget(isStatic = true)
    ItemMeta getItemMeta(@RedirectType(Import.ItemStack)Object nms);

}
