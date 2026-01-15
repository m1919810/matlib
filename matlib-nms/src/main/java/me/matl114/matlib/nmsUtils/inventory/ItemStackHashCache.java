package me.matl114.matlib.nmsUtils.inventory;

import java.util.function.Function;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.utils.stackCache.AbstractItemStackCache;
import org.bukkit.inventory.ItemStack;

public class ItemStackHashCache extends AbstractItemStackCache<ItemStackHashCache>
        implements ItemHashCache, NMSItemHolder {
    private static final ItemStackHashCache INSTANCE = new ItemStackHashCache();

    static {
        INSTANCE.nms = EmptyEnum.EMPTY_ITEMSTACK;
    }

    private Integer hashCodeNoLore;
    private Integer hashCode;
    private Object nms;

    public static ItemStackHashCache get(ItemStack item) {
        ItemStackHashCache cache = (ItemStackHashCache) INSTANCE.clone();
        cache.init(item);
        return cache;
    }

    @Override
    public void init(ItemStack item) {
        super.init(item);
        hashCodeNoLore = null;
        hashCode = null;
        this.nms = ItemUtils.unwrapNullable(this.item);
    }

    public void setFromSource(ItemStackHashCache cache) {
        super.setFromSource(cache);
        this.hashCodeNoLore = cache.hashCodeNoLore;
        this.hashCode = cache.hashCode;
        this.nms = cache.nms;
    }

    public ItemStackHashCache deepcopy(Function<ItemStack, ItemStack> copyFunction) {
        ItemStackHashCache cache = super.deepcopy(copyFunction);
        this.nms = ItemUtils.unwrapNullable(this.item);
        return cache;
    }

    public ItemStackHashCache() {}

    @Override
    public int getAmount() {
        return super.getAmount();
    }

    @Override
    public int getMaxStackSize() {
        return super.getMaxStackSize();
    }

    @Override
    public boolean isAir() {
        return super.isAir();
    }

    @Override
    public boolean matchItem(ItemStackHashCache cache, boolean compareLore) {
        // compare cached hashCode
        if (hashCode != null && cache.hashCode != null && hashCode.intValue() != cache.hashCode.intValue()) {
            return false;
        }
        if (!compareLore) {
            if (hashCodeNoLore != null
                    && cache.hashCodeNoLore != null
                    && hashCodeNoLore.intValue() != cache.hashCodeNoLore.intValue()) {
                return false;
            }
        }
        return NMSItem.ITEMSTACK.matchItem(nms, cache.nms, compareLore, true);
    }

    @Override
    public ItemStackHashCache empty() {
        return INSTANCE;
    }

    @Override
    public int getHashCode() {
        if (hashCode == null) {
            hashCode = NMSItem.ITEMSTACK.customHashcode(nms);
        }
        return hashCode;
    }

    @Override
    public int getHashCodeNoLore() {
        if (hashCodeNoLore == null) {
            hashCodeNoLore = NMSItem.ITEMSTACK.customHashWithoutDisplay(nms);
        }
        return hashCodeNoLore;
    }

    @Override
    public ItemStack getCraftStack() {
        return ItemUtils.asCraftMirror(nms);
    }

    @Override
    public Object getNMS() {
        return nms;
    }

    @Override
    public boolean hasMeta() {
        return NMSItemHolder.super.hasMeta();
    }

    @Override
    public ItemStack toBukkit() {
        return NMSItemHolder.super.toBukkit();
    }

    public ItemStackHashCache copy() {
        return super.copy();
    }
}
