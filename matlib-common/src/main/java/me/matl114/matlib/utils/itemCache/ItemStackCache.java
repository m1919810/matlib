package me.matl114.matlib.utils.itemCache;

import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.LazyInitReference;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemStackCache implements Cloneable {
    /**
     * get item,should be read-only! and will not represent real amount
     * @return
     */
    @Getter
    protected ItemStack item;

    protected LazyInitReference<ItemMeta> metaRef;
    private static ItemStackCache INSTANCE = new ItemStackCache();

    public ItemStackCache() {}

    public static ItemStackCache get(ItemStack item) {
        ItemStackCache cache = INSTANCE.clone();
        cache.init(item);
        return cache;
    }

    protected void init(ItemStack item) {
        this.item = item;
        this.metaRef = LazyInitReference.ofEmpty();
    }

    public Material getType() {
        return item.getType();
    }
    /**
     * get meta info ,if havn't get ,getItemMeta()
     * if !hasItemMeta(), return null;
     * @return
     */
    public ItemMeta getMeta() {
        if (!metaRef.init) {
            metaRef.value = item.hasItemMeta() ? item.getItemMeta() : null;
            metaRef.init = true;
        }
        return metaRef.value;
    }
    /**
     * set Meta info to avoid repeating computation,
     * make sure you know what you are doing
     * @return
     */
    public void setMeta(ItemMeta meta) {
        this.metaRef.value = meta;
        this.metaRef.init = true;
    }

    public void fromSource(ItemStackCache cache) {
        item = cache.item;
        metaRef = cache.metaRef;
    }

    @Override
    protected ItemStackCache clone() {
        try {
            ItemStackCache clone = (ItemStackCache) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
