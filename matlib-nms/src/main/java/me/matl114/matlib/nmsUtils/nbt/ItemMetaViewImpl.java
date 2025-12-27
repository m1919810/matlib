package me.matl114.matlib.nmsUtils.nbt;

import static me.matl114.matlib.nmsMirror.impl.CraftBukkit.ADVENTURE;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import it.unimi.dsi.fastutil.objects.*;
import java.util.*;
import me.matl114.matlib.nmsMirror.craftbukkit.inventory.ItemMetaAPI;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelperDefault;
import me.matl114.matlib.nmsMirror.nbt.TagEnum;
import me.matl114.matlib.nmsUtils.VersionedUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.KeyUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.damage.DamageType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemRarity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class ItemMetaViewImpl extends AbstractItemMetaView {
    public ItemMetaViewImpl(Object itemStack) {
        super(itemStack);
        if (ITEMSTACK == null) {
            ITEMSTACK = (ItemStackHelperDefault) NMSItem.ITEMSTACK;
        }
    }

    public static ItemStackHelperDefault ITEMSTACK;

    @Override
    protected Object2IntMap<Enchantment> initMap() {
        Object nbt1 = ITEMSTACK.getCustomTag(itemStack);
        Object2IntMap<Enchantment> map0 = new Object2IntAVLTreeMap<>(ENCH_SORTOR);
        if (nbt1 == null) {
            return map0;
        }
        AbstractList<?> nbt2 = NMSCore.COMPOUND_TAG.getList(nbt1, "Enchantments", 10);
        for (var i = 0; i < nbt2.size(); ++i) {
            Object ench1 = nbt2.get(i);
            String id = NMSCore.COMPOUND_TAG.getString(ench1, "id");
            int level = 255 & NMSCore.COMPOUND_TAG.getShort(ench1, "lvl");
            Enchantment bukkit = Enchantment.getByKey(KeyUtils.fromString(id));
            if (bukkit != null) {
                map0.put(bukkit, level);
            }
        }
        return map0;
    }

    @Override
    protected Multimap<Attribute, AttributeModifier> initModifiers() {
        Object nbt = ITEMSTACK.getCustomTag(itemStack);
        if (nbt != null) {
            return CraftBukkit.META.buildModifiersFromRaw(nbt);
        }
        return LinkedHashMultimap.create();
    }

    @Override
    protected void syncModifierChange() {
        Multimap<Attribute, AttributeModifier> mdMap = this.modifierMultimap;
        if (mdMap == null) return;
        if (mdMap.isEmpty()) {
            Object nbt = ITEMSTACK.getCustomTag(itemStack);
            if (nbt != null) {
                NMSCore.COMPOUND_TAG.remove(nbt, "AttributeModifiers");
            }
        } else {
            Object nbt1 = ITEMSTACK.getOrCreateCustomTag(itemStack);
            CraftBukkit.META.applyModifiers(mdMap, nbt1, ItemMetaAPI.ATTR_META_KEY);
        }
    }

    @Override
    public Object getAsComponentPatch() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public @Nullable Component displayName() {
        Object nbt = ITEMSTACK.getTagElement(itemStack, "display");
        if (nbt == null) {
            return null;
        } else {
            String nameString = NMSCore.COMPOUND_TAG.getString(nbt, "Name");
            return GsonComponentSerializer.gson().deserialize(nameString);
        }
    }

    @Override
    public void displayName(@Nullable Component component) {
        String rawJson =
                component == null ? null : GsonComponentSerializer.gson().serialize(component);
        NMSCore.COMPOUND_TAG.putString(ITEMSTACK.getOrCreateTagElement(itemStack, "display"), "Name", rawJson);
        this.nmsNameView.flush();
    }

    @Override
    public boolean hasItemName() {
        return false;
    }

    @Override
    public @NotNull Component itemName() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void itemName(@Nullable Component component) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public @NotNull String getItemName() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setItemName(@Nullable String s) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasLocalizedName() {
        Object display = ITEMSTACK.getTagElement(itemStack, "display");
        return display != null && NMSCore.COMPOUND_TAG.contains(display, "LocName");
    }

    @Override
    public @NotNull String getLocalizedName() {
        Object display = ITEMSTACK.getTagElement(itemStack, "display");
        return display == null ? "" : NMSCore.COMPOUND_TAG.getString(display, "LocName");
    }

    @Override
    public void setLocalizedName(@Nullable String s) {
        NMSCore.COMPOUND_TAG.putString(ITEMSTACK.getOrCreateTagElement(itemStack, "display"), "LocName", s);
    }

    @Override
    public @Nullable List<Component> lore() {
        List<String> lores = ITEMSTACK.getLoreRaw(itemStack);
        return lores == null ? null : CraftBukkit.ADVENTURE.asAdventureFromJson(lores);
    }

    @Override
    public void lore(@Nullable List<? extends Component> list) {
        Object nbt = ITEMSTACK.getTagElement(itemStack, "display");
        if (list == null || list.isEmpty()) {
            if (nbt != null) {
                NMSCore.COMPOUND_TAG.remove(nbt, "Lore");
            }
        } else {
            AbstractList listTag = NMSCore.TAGS.listTag();
            List<String> asJson = CraftBukkit.ADVENTURE.asJson(list);
            for (var str : asJson) {
                listTag.add(NMSCore.TAGS.stringTag(str));
            }
            NMSCore.COMPOUND_TAG.put(nbt, "Lore", listTag);
        }
        this.nmsLoreView.flush();
    }

    @Override
    public boolean hasCustomModelData() {
        return ITEMSTACK.hasCustomTagKey(itemStack, "CustomModelData");
    }

    @Override
    public int getCustomModelData() {
        return ITEMSTACK.getCustomTagInt(itemStack, "CustomModelData");
    }

    @Override
    public void setCustomModelData(@Nullable Integer integer) {

        if (integer != null) {
            Object nbt = ITEMSTACK.getOrCreateCustomTag(itemStack);
            NMSCore.COMPOUND_TAG.putInt(nbt, "CustomModelData", integer);
        } else {
            Object nbt = ITEMSTACK.getCustomTag(itemStack);
            if (nbt != null) NMSCore.COMPOUND_TAG.remove(nbt, "CustomModelData");
        }
    }

    @Override
    public boolean hasEnchantable() {
        return false;
    }

    @Override
    public int getEnchantable() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setEnchantable(@Nullable Integer integer) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean addEnchant(@NotNull Enchantment ench, int level, boolean b) {
        if (b || level >= ench.getStartLevel() && level <= ench.getMaxLevel()) {
            if (this.enchMap != null) {
                // update cache
                this.enchMap.put(ench, level);
            }
            String enchId = ench.getKey().toString();
            Object nbt1 = ITEMSTACK.getOrCreateCustomTag(itemStack);
            AbstractList<?> listTag = NMSCore.COMPOUND_TAG.getOrNewList(nbt1, "Enchantments", TagEnum.TAG_COMPOUND);
            boolean ret;
            Object newEnch = NMSCore.COMPOUND_TAG.newComp();
            NMSCore.COMPOUND_TAG.putString(newEnch, "id", enchId);
            NMSCore.COMPOUND_TAG.putShort(newEnch, "lvl", (short) level);
            int index = Collections.binarySearch(
                    listTag, newEnch, Comparator.comparing(o -> NMSCore.COMPOUND_TAG.getString(o, "id")));
            if (index < 0) {
                ((List) listTag).add(-index - 1, newEnch);
                ret = true;
            } else {
                Object value = listTag.get(index);
                int oldValue = 255 & NMSCore.COMPOUND_TAG.getShort(value, "lvl");
                if (oldValue != level) {
                    ret = true;
                    ((List) listTag).set(index, newEnch);

                } else {
                    ret = false;
                }
            }
            return ret;
        }
        return false;
    }

    @Override
    public boolean removeEnchant(@NotNull Enchantment ench) {
        if (this.enchMap != null) {
            this.enchMap.removeInt(ench);
        }
        String enchId = ench.getKey().toString();
        Object nbt1 = ITEMSTACK.getCustomTag(itemStack);
        if (nbt1 != null && NMSCore.COMPOUND_TAG.contains(nbt1, "Enchantments", TagEnum.TAG_LIST)) {
            AbstractList<?> listTag = NMSCore.COMPOUND_TAG.getList(nbt1, "Enchantments", TagEnum.TAG_COMPOUND);
            return listTag.removeIf(o -> Objects.equals(enchId, NMSCore.COMPOUND_TAG.getString(o, "id")));

        } else return false;
    }

    @Override
    public void removeEnchantments() {
        if (this.enchMap != null) {
            this.enchMap.clear();
        }
        Object nbt1 = ITEMSTACK.getCustomTag(itemStack);
        if (nbt1 != null) {
            NMSCore.COMPOUND_TAG.remove(nbt1, "Enchantments");
        }
    }

    public int hideFlag() {
        Object nbt1 = ITEMSTACK.getCustomTag(itemStack);
        return nbt1 != null ? NMSCore.COMPOUND_TAG.getInt(nbt1, "HideFlags") : 0;
    }

    public void hideFlag(int v) {
        if (v != 0) {
            Object nbt1 = ITEMSTACK.getOrCreateCustomTag(itemStack);
            NMSCore.COMPOUND_TAG.putInt(nbt1, "HideFlags", v);
        } else {
            Object nbt1 = ITEMSTACK.getCustomTag(itemStack);
            if (nbt1 != null) {
                NMSCore.COMPOUND_TAG.remove(nbt1, "HideFlags");
            }
        }
    }

    @Override
    public void addItemFlags(@NotNull ItemFlag... itemFlags) {
        int hideFlag = hideFlag();
        for (var flag : itemFlags) {
            hideFlag |= 1 << flag.ordinal();
        }
        hideFlag(hideFlag);
    }

    @Override
    public void removeItemFlags(@NotNull ItemFlag... itemFlags) {
        int hideFlag = hideFlag();
        for (var flag : itemFlags) {
            hideFlag &= ~(1 << flag.ordinal());
        }
        hideFlag(hideFlag);
    }

    @Override
    public @NotNull Set<ItemFlag> getItemFlags() {
        int hideFlag = hideFlag();
        Set<ItemFlag> flags = EnumSet.noneOf(ItemFlag.class);
        Debug.logger(hideFlag);
        for (var i : ItemFlag.values()) {
            int bit = 1 << (i.ordinal());
            if ((bit & hideFlag) != 0) {
                flags.add(i);
            }
        }
        return flags;
    }

    @Override
    public boolean hasItemFlag(@NotNull ItemFlag itemFlag) {
        int hideFlag = hideFlag();
        return ((1 << itemFlag.ordinal()) & hideFlag) != 0;
    }

    @Override
    public boolean isHideTooltip() {
        return false;
    }

    @Override
    public void setHideTooltip(boolean b) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasTooltipStyle() {
        return false;
    }

    @Override
    public @Nullable NamespacedKey getTooltipStyle() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setTooltipStyle(@Nullable NamespacedKey namespacedKey) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasItemModel() {
        return false;
    }

    @Override
    public @Nullable NamespacedKey getItemModel() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setItemModel(@Nullable NamespacedKey namespacedKey) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean isUnbreakable() {
        Object nbt1 = ITEMSTACK.getCustomTag(itemStack);
        return nbt1 != null && NMSCore.COMPOUND_TAG.getBoolean(nbt1, "Unbreakable");
    }

    @Override
    public void setUnbreakable(boolean b) {
        if (b) {
            Object nbt1 = ITEMSTACK.getOrCreateCustomTag(itemStack);
            NMSCore.COMPOUND_TAG.putBoolean(nbt1, "Unbreakable", true);
        } else {
            Object nbt1 = ITEMSTACK.getCustomTag(itemStack);
            if (nbt1 != null) {
                NMSCore.COMPOUND_TAG.remove(nbt1, "Unbreakable");
            }
        }
    }

    @Override
    public boolean hasEnchantmentGlintOverride() {
        return false;
    }

    @Override
    public @NotNull Boolean getEnchantmentGlintOverride() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setEnchantmentGlintOverride(@Nullable Boolean aBoolean) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean isGlider() {
        return false;
    }

    @Override
    public void setGlider(boolean b) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasDamageResistant() {
        return false;
    }

    @Override
    public @Nullable Tag<DamageType> getDamageResistant() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setDamageResistant(@Nullable Tag<DamageType> tag) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasMaxStackSize() {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setMaxStackSize(@Nullable Integer integer) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasRarity() {
        return false;
    }

    @Override
    public @NotNull ItemRarity getRarity() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setRarity(@Nullable ItemRarity itemRarity) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasUseRemainder() {
        return false;
    }

    @Override
    public @Nullable ItemStack getUseRemainder() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setUseRemainder(@Nullable ItemStack itemStack) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasUseCooldown() {
        return false;
    }

    @Override
    public @NotNull UseCooldownComponent getUseCooldown() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setUseCooldown(@Nullable UseCooldownComponent useCooldownComponent) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasFood() {
        return false;
    }

    @Override
    public @NotNull FoodComponent getFood() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setFood(@Nullable FoodComponent foodComponent) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasTool() {
        return false;
    }

    @Override
    public @NotNull ToolComponent getTool() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setTool(@Nullable ToolComponent toolComponent) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasEquippable() {
        return false;
    }

    @Override
    public @NotNull EquippableComponent getEquippable() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setEquippable(@Nullable EquippableComponent equippableComponent) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasJukeboxPlayable() {
        return false;
    }

    @Override
    public @NotNull JukeboxPlayableComponent getJukeboxPlayable() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public void setJukeboxPlayable(@Nullable JukeboxPlayableComponent jukeboxPlayableComponent) {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasAttributeModifiers() {
        Object nbt = ITEMSTACK.getCustomTag(itemStack);
        return nbt != null && NMSCore.COMPOUND_TAG.contains(nbt, "AttributeModifiers", 9);
    }

    @Override
    public @NotNull String getAsComponentString() {
        throw VersionedUtils.versionLow();
    }

    @Override
    public boolean hasPlaceableKeys() {
        return ITEMSTACK.hasCustomTagKey(itemStack, "CanPlaceOn");
    }

    @Override
    public boolean hasDestroyableKeys() {
        return ITEMSTACK.hasCustomTagKey(itemStack, "CanDestroy");
    }
}
