package me.matl114.matlib.nmsUtils.inventory;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.function.Consumer;
import lombok.NoArgsConstructor;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.COWView;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.nbt.ItemMetaView;
import me.matl114.matlib.utils.version.VersionedRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

@NoArgsConstructor
public class ItemBuilder {
    public ItemBuilder(ItemStack stack) {
        stack(stack);
    }

    public static ItemBuilder builder() {
        return new ItemBuilder();
    }

    public static ItemBuilder builder(Material mat) {
        return new ItemBuilder(ItemUtils.newStack(mat, 1));
    }

    public static ItemBuilder builder(Material mat, int size) {
        return new ItemBuilder(ItemUtils.newStack(mat, size));
    }

    public ItemBuilder stack(ItemStack stack0) {
        this.itemStack = ItemUtils.copyStack(stack0);
        this.metaView = ItemMetaView.ofCraft(this.itemStack);
        return this;
    }

    private void ensureStack() {
        Preconditions.checkNotNull(itemStack, "ItemStack should be initialized before any other action!");
    }

    public ItemBuilder name(String legacy) {
        ensureStack();
        metaView.setDisplayName(legacy);
        return this;
    }

    public ItemBuilder name(Component component) {
        ensureStack();
        metaView.displayName(component);
        return this;
    }

    public ItemBuilder lore(List<String> strings) {
        ensureStack();
        metaView.setLore(strings);
        return this;
    }

    public ItemBuilder adventureLore(List<Component> adventure) {
        ensureStack();
        metaView.lore(adventure);
        return this;
    }

    public ItemBuilder loreLine(String string) {
        ensureStack();
        metaView.addLoreAt(-1, string);
        return this;
    }

    public ItemBuilder loreLine(Component comp) {
        ensureStack();
        metaView.addLoreAdventureAt(-1, comp);
        return this;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        ensureStack();
        metaView.addEnchant(enchantment, level, true);
        return this;
    }

    public ItemBuilder hideFlags(ItemFlag... flags) {
        ensureStack();
        metaView.addItemFlags(flags);
        return this;
    }

    public ItemBuilder allItemFlags() {
        return hideFlags(ItemFlag.values());
    }

    public ItemBuilder unbreakable() {
        ensureStack();
        metaView.setUnbreakable(true);
        return this;
    }

    public ItemStack build() {

        return ItemUtils.copyStack(this.itemStack);
    }

    public ItemBuilder glow() {
        ensureStack();
        if (ItemMetaView.versionAtLeast1_20_R4) {
            metaView.setEnchantmentGlintOverride(true);
        } else {
            metaView.addEnchant(VersionedRegistry.enchantment("luck_of_the_sea"), 1, true);
        }
        return this;
    }

    public ItemBuilder metaView(Consumer<ItemMetaView> metaView) {
        ensureStack();
        metaView.accept(this.metaView);
        return this;
    }

    public ItemBuilder meta(Consumer<ItemMeta> meta) {
        ensureStack();
        ItemMeta meta0 = itemStack.getItemMeta();
        meta.accept(meta0);
        itemStack.setItemMeta(meta0);
        metaView = ItemMetaView.ofCraft(itemStack);
        return this;
    }

    public ItemBuilder pdc(String key, String val) {
        ensureStack();
        COWView<Object> value =
                NMSItem.ITEMSTACK.getPersistentDataCompoundView(ItemUtils.unwrapHandle(this.itemStack), false);
        Object writableNBT = value.getWritable();
        NMSCore.COMPOUND_TAG.putString(writableNBT, key, val);
        value.writeBack(writableNBT);
        return this;
    }

    public ItemBuilder pdc(NamespacedKey key, String val) {
        ensureStack();
        return pdc(key.toString(), val);
    }

    ItemStack itemStack;
    ItemMetaView metaView;
}
