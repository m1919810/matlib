package me.matl114.matlib.utils.stackCache;

import me.matl114.matlib.algorithms.dataStructures.struct.LazyInitReference;
import me.matl114.matlib.utils.CraftUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public class ItemStackMetaCache extends AbstractItemStackCache<ItemStackMetaCache> {
    /**
     * get item,should be read-only! and will not represent real amount
     * @return
     */

    // Meta Ref should change with itemStack, so metaRef doesn't need a clone with cloning the cache
    protected LazyInitReference<ItemMeta> metaRef;

    private static final ItemStackMetaCache INSTANCE = new ItemStackMetaCache();

    public ItemStackMetaCache() {}

    public static ItemStackMetaCache get(ItemStack item) {
        ItemStackMetaCache cache = INSTANCE.clone();
        cache.init(item);
        return cache;
    }

    @MustBeInvokedByOverriders
    public void init(ItemStack item) {
        super.init(item);
        this.item = item;
        this.metaRef = LazyInitReference.ofEmpty();
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

    @MustBeInvokedByOverriders
    public void setFromSource(ItemStackMetaCache cache) {
        super.setFromSource(cache);
        this.metaRef =
                (cache instanceof ItemStackMetaCache metaCache ? metaCache.metaRef : LazyInitReference.ofEmpty());
    }

    @Override
    public ItemStackMetaCache clone() {
        // TODO: copy mutable state here, so the clone can't change the internals of the original
        return (ItemStackMetaCache) super.clone();
    }

    @Override
    public boolean matchItem(ItemStackMetaCache cache, boolean compareLore) {
        if (this.getType() != cache.getType()) {
            return false;
        }
        ItemMeta meta = getMeta();
        ItemMeta meta2 = cache.getMeta();
        return meta == null ? meta2 == null : (meta2 != null && CraftUtils.matchItemMeta(meta, meta2, compareLore));
    }

    @Override
    public ItemStackMetaCache empty() {
        return INSTANCE;
    }
}
