package me.matl114.matlib.utils.stackCache;

import com.google.common.base.Preconditions;
import io.papermc.paper.datacomponent.DataComponentBuilder;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.inventory.ItemRarity;
import io.papermc.paper.inventory.tooltip.TooltipContext;
import io.papermc.paper.registry.set.RegistryKeySet;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.MaterialData;
import org.jetbrains.annotations.*;

public class ItemStackHolder extends ItemStack {

    public ItemStackHolder(ItemStack delegate) {
        super();
        this.handle = delegate;
    }

    public ItemStack handle;

    public @NotNull Material getType() {
        return this.handle.getType();
    }

    /** @deprecated */
    @Deprecated
    public void setType(@NotNull Material type) {
        Preconditions.checkArgument(type != null, "Material cannot be null");
        this.handle.setType(type);
    }

    @Contract(value = "_ -> new", pure = true)
    public @NotNull ItemStack withType(@NotNull Material type) {
        return this.handle.withType(type);
    }

    public int getAmount() {
        return this.handle.getAmount();
    }

    public void setAmount(int amount) {
        this.handle.setAmount(amount);
    }

    /** @deprecated */
    @Deprecated(forRemoval = true, since = "1.13")
    public @Nullable MaterialData getData() {
        return this.handle.getData();
    }

    /** @deprecated */
    @Deprecated(forRemoval = true, since = "1.13")
    public void setData(@Nullable MaterialData data) {
        this.handle.setData(data);
    }

    /** @deprecated */
    @Deprecated(since = "1.13")
    public void setDurability(short durability) {
        this.handle.setDurability(durability);
    }

    /** @deprecated */
    @Deprecated(since = "1.13")
    public short getDurability() {
        return this.handle.getDurability();
    }

    public int getMaxStackSize() {
        return this.handle.getMaxStackSize();
    }

    public String toString() {
        return this.handle.toString();
    }

    public boolean equals(Object obj) {
        return this.handle.equals(obj);
    }

    public boolean isSimilar(@Nullable ItemStack stack) {
        return this.handle.isSimilar(stack);
    }

    public @NotNull ItemStack clone() {
        return this.handle.clone();
    }

    public int hashCode() {
        return this.handle.hashCode();
    }

    public boolean containsEnchantment(@NotNull Enchantment ench) {
        return this.handle.containsEnchantment(ench);
    }

    public int getEnchantmentLevel(@NotNull Enchantment ench) {
        return this.handle.getEnchantmentLevel(ench);
    }

    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        return this.handle.getEnchantments();
    }

    public void addEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.handle.addEnchantments(enchantments);
    }

    public void addEnchantment(@NotNull Enchantment ench, int level) {
        this.handle.addEnchantment(ench, level);
    }

    public void addUnsafeEnchantments(@NotNull Map<Enchantment, Integer> enchantments) {
        this.handle.addUnsafeEnchantments(enchantments);
    }

    public void addUnsafeEnchantment(@NotNull Enchantment ench, int level) {
        this.handle.addUnsafeEnchantment(ench, level);
    }

    public int removeEnchantment(@NotNull Enchantment ench) {
        return this.handle.removeEnchantment(ench);
    }

    public void removeEnchantments() {
        this.handle.removeEnchantments();
    }

    public @NotNull Map<String, Object> serialize() {
        return this.handle.serialize();
    }

    public static @NotNull ItemStack deserialize(@NotNull Map<String, Object> args) {
        int version = args.containsKey("v") ? ((Number) args.get("v")).intValue() : -1;
        short damage = 0;
        int amount = 1;
        if (args.containsKey("damage")) {
            damage = ((Number) args.get("damage")).shortValue();
        }

        Material type;
        if (version < 0) {
            type = Material.getMaterial("LEGACY_" + (String) args.get("type"));
            byte dataVal = type != null && type.getMaxDurability() == 0 ? (byte) damage : 0;
            type = Bukkit.getUnsafe().fromLegacy(new MaterialData(type, dataVal), true);
            if (dataVal != 0) {
                damage = 0;
            }
        } else {
            type = Bukkit.getUnsafe().getMaterial((String) args.get("type"), version);
        }

        if (args.containsKey("amount")) {
            amount = ((Number) args.get("amount")).intValue();
        }

        ItemStack result = new ItemStack(type, amount, damage);
        Object raw;
        if (args.containsKey("enchantments")) {
            raw = args.get("enchantments");
            if (raw instanceof Map) {
                Map<?, ?> map = (Map) raw;
                Iterator var8 = map.entrySet().iterator();

                while (var8.hasNext()) {
                    Map.Entry<?, ?> entry = (Map.Entry) var8.next();
                    String stringKey = entry.getKey().toString();
                    stringKey = Bukkit.getUnsafe().get(Enchantment.class, stringKey);
                    NamespacedKey key = NamespacedKey.fromString(stringKey.toLowerCase(Locale.ROOT));
                    Enchantment enchantment = (Enchantment) Bukkit.getUnsafe().get(Registry.ENCHANTMENT, key);
                    if (enchantment != null && entry.getValue() instanceof Integer) {
                        result.addUnsafeEnchantment(enchantment, (Integer) entry.getValue());
                    }
                }
            }
        } else if (args.containsKey("meta")) {
            raw = args.get("meta");
            if (raw instanceof ItemMeta) {
                ((ItemMeta) raw).setVersion(version);
                if (version < 3837 && ((ItemMeta) raw).hasItemFlag(ItemFlag.HIDE_ADDITIONAL_TOOLTIP)) {
                    ((ItemMeta) raw).addItemFlags(new ItemFlag[] {ItemFlag.HIDE_STORED_ENCHANTS});
                }

                result.setItemMeta((ItemMeta) raw);
            }
        }

        if (version < 0 && args.containsKey("damage")) {
            result.setDurability(damage);
        }

        return result.ensureServerConversions();
    }

    public boolean editMeta(@NotNull Consumer<? super ItemMeta> consumer) {
        return this.editMeta(ItemMeta.class, consumer);
    }

    public <M extends ItemMeta> boolean editMeta(@NotNull Class<M> metaClass, @NotNull Consumer<? super M> consumer) {
        return this.handle.editMeta(metaClass, consumer);
    }

    @UndefinedNullability
    public ItemMeta getItemMeta() {
        return this.handle.getItemMeta();
    }

    public boolean hasItemMeta() {
        return this.handle.hasItemMeta();
    }

    public boolean setItemMeta(@Nullable ItemMeta itemMeta) {
        return this.handle.setItemMeta(itemMeta);
    }

    /** @deprecated */
    @Deprecated(forRemoval = true)
    public @NotNull String getTranslationKey() {
        return Bukkit.getUnsafe().getTranslationKey(this.handle);
    }

    public @NotNull ItemStack enchantWithLevels(
            @Range(from = 1L, to = 30L) int levels, boolean allowTreasure, @NotNull Random random) {
        return Bukkit.getServer().getItemFactory().enchantWithLevels(this.handle, levels, allowTreasure, random);
    }

    public @NotNull ItemStack enchantWithLevels(
            int levels, @NotNull RegistryKeySet<@NotNull Enchantment> keySet, @NotNull Random random) {
        return Bukkit.getItemFactory().enchantWithLevels(this.handle, levels, keySet, random);
    }

    public @NotNull HoverEvent<HoverEvent.ShowItem> asHoverEvent(@NotNull UnaryOperator<HoverEvent.ShowItem> op) {
        return Bukkit.getServer().getItemFactory().asHoverEvent(this.handle, op);
    }

    public @NotNull Component displayName() {
        return Bukkit.getServer().getItemFactory().displayName(this.handle);
    }

    public @NotNull ItemStack ensureServerConversions() {
        return Bukkit.getServer().getItemFactory().ensureServerConversions(this.handle);
    }

    public static @NotNull ItemStack deserializeBytes(@NotNull byte[] bytes) {
        return Bukkit.getUnsafe().deserializeItem(bytes);
    }

    public @NotNull byte[] serializeAsBytes() {
        return Bukkit.getUnsafe().serializeItem(this.handle);
    }

    public static byte @NotNull [] serializeItemsAsBytes(@Nullable ItemStack @NotNull [] items) {
        return serializeItemsAsBytes((Collection) Arrays.asList(items));
    }

    /** @deprecated */
    @Deprecated
    public @Nullable String getI18NDisplayName() {
        return Bukkit.getServer().getItemFactory().getI18NDisplayName(this.handle);
    }

    /** @deprecated */
    @Deprecated(forRemoval = true)
    public int getMaxItemUseDuration() {
        return this.getMaxItemUseDuration((LivingEntity) null);
    }

    public int getMaxItemUseDuration(@NotNull LivingEntity entity) {
        return this.handle.getMaxItemUseDuration(entity);
    }

    public @NotNull ItemStack asOne() {
        return this.asQuantity(1);
    }

    public @NotNull ItemStack asQuantity(int qty) {
        ItemStack clone = this.clone();
        clone.setAmount(qty);
        return clone;
    }

    public @NotNull ItemStack add() {
        return this.add(1);
    }

    public @NotNull ItemStack add(int qty) {
        this.setAmount(Math.min(this.getMaxStackSize(), this.getAmount() + qty));
        return this;
    }

    public @NotNull ItemStack subtract() {
        return this.subtract(1);
    }

    public @NotNull ItemStack subtract(int qty) {
        this.setAmount(Math.max(0, this.getAmount() - qty));
        return this;
    }

    /** @deprecated */
    @Deprecated
    public @Nullable List<String> getLore() {
        return this.handle.getLore();
    }

    public @Nullable List<Component> lore() {
        return this.handle.lore();
    }

    /** @deprecated */
    @Deprecated
    public void setLore(@Nullable List<String> lore) {
        this.handle.setLore(lore);
    }

    public void lore(@Nullable List<? extends Component> lore) {
        this.handle.lore(lore);
    }

    public void addItemFlags(ItemFlag... itemFlags) {
        this.handle.addItemFlags(itemFlags);
    }

    public void removeItemFlags(ItemFlag... itemFlags) {
        this.handle.removeItemFlags(itemFlags);
    }

    public @NotNull Set<ItemFlag> getItemFlags() {
        return this.handle.getItemFlags();
    }

    public boolean hasItemFlag(@NotNull ItemFlag flag) {
        return this.handle.hasItemFlag(flag);
    }

    public @NotNull String translationKey() {
        return Bukkit.getUnsafe().getTranslationKey(this.handle);
    }

    /** @deprecated */
    @Deprecated(forRemoval = true, since = "1.20.5")
    public @NotNull ItemRarity getRarity() {
        return ItemRarity.valueOf(this.getItemMeta().getRarity().name());
    }

    public boolean isRepairableBy(@NotNull ItemStack repairMaterial) {
        return Bukkit.getUnsafe().isValidRepairItemStack(this.handle, repairMaterial);
    }

    public boolean canRepair(@NotNull ItemStack toBeRepaired) {
        return Bukkit.getUnsafe().isValidRepairItemStack(toBeRepaired, this.handle);
    }

    public @NotNull ItemStack damage(int amount, @NotNull LivingEntity livingEntity) {
        return livingEntity.damageItemStack(this.handle, amount);
    }

    public boolean isEmpty() {
        return this.handle.isEmpty();
    }

    public @NotNull @Unmodifiable List<Component> computeTooltipLines(
            @NotNull TooltipContext tooltipContext, @Nullable Player player) {
        return Bukkit.getUnsafe().computeTooltipLines(this.handle, tooltipContext, player);
    }

    @Contract(pure = true)
    @ApiStatus.Experimental
    public <T> @Nullable T getData(DataComponentType.@NotNull Valued<T> type) {
        return this.handle.getData(type);
    }

    @Contract(value = "_, !null -> !null", pure = true)
    @ApiStatus.Experimental
    public <T> @Nullable T getDataOrDefault(DataComponentType.@NotNull Valued<? extends T> type, @Nullable T fallback) {
        T object = this.getData(type);
        return object != null ? object : fallback;
    }

    @Contract(pure = true)
    @ApiStatus.Experimental
    public boolean hasData(@NotNull DataComponentType type) {
        return this.handle.hasData(type);
    }

    @Contract("-> new")
    @ApiStatus.Experimental
    public @Unmodifiable Set<@NotNull DataComponentType> getDataTypes() {
        return this.handle.getDataTypes();
    }

    @ApiStatus.Experimental
    public <T> void setData(DataComponentType.@NotNull Valued<T> type, @NotNull DataComponentBuilder<T> valueBuilder) {
        this.setData(type, valueBuilder.build());
    }

    @ApiStatus.Experimental
    public <T> void setData(DataComponentType.@NotNull Valued<T> type, @NotNull T value) {
        this.handle.setData(type, value);
    }

    @ApiStatus.Experimental
    public void setData(DataComponentType.@NotNull NonValued type) {
        this.handle.setData(type);
    }

    @ApiStatus.Experimental
    public void unsetData(@NotNull DataComponentType type) {
        this.handle.unsetData(type);
    }

    @ApiStatus.Experimental
    public void resetData(@NotNull DataComponentType type) {
        this.handle.resetData(type);
    }

    @ApiStatus.Experimental
    public boolean isDataOverridden(@NotNull DataComponentType type) {
        return this.handle.isDataOverridden(type);
    }
}
