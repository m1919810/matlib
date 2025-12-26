package me.matl114.matlib.nmsUtils.nbt;

import static me.matl114.matlib.nmsMirror.impl.NMSItem.ITEMSTACK;

import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.SetMultimap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.util.*;
import me.matl114.matlib.algorithms.algorithm.CollectionUtils;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.ValueAccess;
import me.matl114.matlib.common.lang.annotations.Lazily;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.VersionedUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.tags.CustomItemTagContainer;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.tag.DamageTypeTags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractItemMetaView implements ItemMetaView {

    public AbstractItemMetaView(Object itemStack) {
        this.itemStack = itemStack;
        this.nmsNameView = ITEMSTACK.getDisplayNameView(itemStack);
        this.nmsLoreView = ITEMSTACK.getLoreView(itemStack, true);
    }

    @Override
    public Object getNMSItemStack() {
        return itemStack;
    }

    Object itemStack;
    ValueAccess<Iterable<?>> nmsNameView;

    public ValueAccess<Iterable<?>> getNMSNameView() {
        return nmsNameView;
    }

    ListMapView<?, Iterable<?>> nmsLoreView;

    public ListMapView<?, Iterable<?>> getNMSLoreView() {
        return nmsLoreView;
    }

    @Lazily(nonnull = true, constVal = true)
    Object2IntMap<Enchantment> enchMap;

    protected static final Comparator<Enchantment> ENCH_SORTOR =
            Comparator.comparing(o -> o.getKey().toString());

    protected final Object2IntMap<Enchantment> enchantmentMap() {
        if (enchMap == null) {
            enchMap = initMap();
        }
        return enchMap;
    }

    protected abstract Object2IntMap<Enchantment> initMap();

    @Override
    public boolean hasDisplayName() {
        return ITEMSTACK.hasCustomHoverName(itemStack);
    }

    @Override
    public @NotNull String getDisplayName() {
        return ChatUtils.serializeToLegacy(nmsNameView.get());
    }

    @Override
    public void setDisplayName(@Nullable String s) {
        nmsNameView.set(ChatUtils.deserializeLegacy(s));
    }

    @Override
    public @NotNull BaseComponent[] getDisplayNameComponent() {
        throw new UnsupportedOperationException("spigot");
    }

    @Override
    public void setDisplayNameComponent(@Nullable BaseComponent[] baseComponents) {
        throw new UnsupportedOperationException("spigot");
    }

    @Override
    public boolean hasLore() {
        return ITEMSTACK.hasLore(itemStack);
    }

    @Override
    public @Nullable List<String> getLore() {
        List<String> itemLore = new ArrayList<>();
        for (var entry : this.nmsLoreView) {
            itemLore.add(ChatUtils.serializeToLegacy(entry));
        }
        return itemLore;
    }

    @Override
    public @Nullable List<BaseComponent[]> getLoreComponents() {
        throw new UnsupportedOperationException("spigot");
    }

    @Override
    public void setLore(@Nullable List<String> list) {
        CollectionUtils.mapAndSet(list, ChatUtils::deserializeLegacy, this.nmsLoreView);
        this.nmsLoreView.batchWriteback();
    }

    @Override
    public void setLoreComponents(@Nullable List<BaseComponent[]> list) {
        throw new UnsupportedOperationException("spigot");
    }

    @Override
    public boolean hasEnchants() {
        return !enchantmentMap().isEmpty();
    }

    @Override
    public boolean hasEnchant(@NotNull Enchantment enchantment) {
        return enchantmentMap().containsKey(enchantment);
    }

    @Override
    public int getEnchantLevel(@NotNull Enchantment enchantment) {
        return enchantmentMap().getInt(enchantment);
    }

    @Override
    public @NotNull Map<Enchantment, Integer> getEnchants() {
        return enchantmentMap();
    }

    @Override
    public boolean hasConflictingEnchant(@NotNull Enchantment enchantment) {
        Object2IntMap<Enchantment> ench = enchantmentMap();
        if (ench.isEmpty()) return false;
        for (var en : ench.keySet()) {
            if (enchantment.conflictsWith(en)) return true;
        }
        return false;
    }

    @Override
    public boolean isFireResistant() {
        return this.hasDamageResistant() && DamageTypeTags.IS_FIRE.equals(this.getDamageResistant());
    }

    @Override
    public void setFireResistant(boolean b) {
        if (b) this.setDamageResistant(DamageTypeTags.IS_FIRE);
        else this.setDamageResistant(null);
    }

    @Lazily(constVal = false, nonnull = true)
    protected Multimap<Attribute, AttributeModifier> modifierMultimap;

    protected final Multimap<Attribute, AttributeModifier> modifiers() {
        if (modifierMultimap == null) {
            modifierMultimap = initModifiers();
        }
        return modifierMultimap;
    }

    protected abstract Multimap<Attribute, AttributeModifier> initModifiers();

    @Override
    public @Nullable Multimap<Attribute, AttributeModifier> getAttributeModifiers() {
        if (!hasAttributeModifiers()) {
            return null;
        }
        return modifiers();
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getAttributeModifiers(@NotNull EquipmentSlot slot) {
        SetMultimap<Attribute, AttributeModifier> result = LinkedHashMultimap.create();
        for (Map.Entry<Attribute, AttributeModifier> entry : modifiers().entries()) {
            if (entry.getValue().getSlot() == null || entry.getValue().getSlot() == slot) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    protected abstract void syncModifierChange();

    @Override
    public @Nullable Collection<AttributeModifier> getAttributeModifiers(@NotNull Attribute attribute) {
        if (!hasAttributeModifiers()) {
            return null;
        }
        return modifiers().containsKey(attribute) ? modifiers().get(attribute) : null;
    }

    @Override
    public boolean addAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier attributeModifier) {
        boolean re = modifiers().put(attribute, attributeModifier);
        if (re) {
            syncModifierChange();
        }
        return re;
    }

    @Override
    public void setAttributeModifiers(@Nullable Multimap<Attribute, AttributeModifier> multimap) {
        this.modifierMultimap = multimap == null ? LinkedHashMultimap.create() : LinkedHashMultimap.create(multimap);
        syncModifierChange();
    }

    @Override
    public boolean removeAttributeModifier(@NotNull Attribute attribute) {
        if (!hasAttributeModifiers()) {
            return false;
        }
        var re = modifiers().removeAll(attribute);
        if (re.isEmpty()) return false;
        syncModifierChange();
        return true;
    }

    @Override
    public boolean removeAttributeModifier(@NotNull EquipmentSlot equipmentSlot) {
        if (!hasAttributeModifiers()) {
            return false;
        }
        int removed = 0;
        Iterator<Map.Entry<Attribute, AttributeModifier>> iter =
                this.modifiers().entries().iterator();

        while (iter.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = iter.next();
            if (entry.getValue().getSlot() == equipmentSlot) { // Paper - correctly test slot against group
                iter.remove();
                ++removed;
            }
        }
        if (removed > 0) {
            syncModifierChange();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAttributeModifier(@NotNull Attribute attribute, @NotNull AttributeModifier modifier) {
        if (!hasAttributeModifiers()) {
            return false;
        }
        int removed = 0;
        Iterator<Map.Entry<Attribute, AttributeModifier>> iter =
                this.modifiers().entries().iterator();

        while (iter.hasNext()) {
            Map.Entry<Attribute, AttributeModifier> entry = iter.next();
            if (entry.getKey() == null || entry.getValue() == null) {
                iter.remove();
                ++removed;
                continue; // remove all null values while we are here
            }

            if (entry.getKey() == attribute && entry.getValue().getKey().equals(modifier.getKey())) {
                iter.remove();
                ++removed;
            }
        }
        if (removed > 0) {
            syncModifierChange();
            return true;
        }
        return false;
    }

    public Object getAsTag() {
        return ITEMSTACK.saveNbtAsTag(itemStack);
    }

    public abstract Object getAsComponentPatch();

    @Override
    public @NotNull CustomItemTagContainer getCustomTagContainer() {
        throw VersionedUtils.removal();
    }

    @Override
    public void setVersion(int i) {}

    @Override
    public ItemMeta clone() {
        // left as not completed
        return ItemMetaView.of(this.itemStack);
    }

    @Override
    public @NotNull Map<String, Object> serialize() {
        return CraftBukkit.ITEMSTACK.getItemMeta(this.itemStack).serialize();
    }

    @Override
    public @NotNull PersistentDataContainer getPersistentDataContainer() {
        return ItemUtils.getPersistentDataContainerView(this.itemStack, false);
    }

    public PersistentDataContainer getInternalNbt() {
        return new TagCompoundView(ITEMSTACK.getCustomedNbtView(this.itemStack, false));
    }
}
