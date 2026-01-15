package me.matl114.matlib.utils.stackCache;

import javax.annotation.Nullable;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.NMSInventoryUtils;
import me.matl114.matlib.utils.WorldUtils;
import org.bukkit.block.TileState;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 * used to cal num when push item, consume-only, it will not push item when unionSum
 */
public class ItemWithSlot<T extends ItemStackCache<T>> extends ItemCounter<T> {
    // negative means not slot
    // slotFlag = wasNull ? ~slot : slot
    private int slotFlag = 0;
    protected InventoryHolder menu;

    public ItemWithSlot() {
        super();
    }

    public ItemWithSlot<T> setMenu(InventoryHolder menu) {
        this.menu = menu;
        return this;
    }

    public int getSlot() {
        return slotFlag < 0 ? ~slotFlag : slotFlag;
    }

    public ItemWithSlot<T> setSlot(int slot) {
        this.slotFlag = cache.isAir() ? ~slot : slot;
        return this;
    }

    public ItemWithSlot(T item) {
        // can use as storage unit
        super(item, item.getAmount());
        setSlot(0);
    }

    protected void init(ItemStack item) {
        if (item != null) {
            super.init(item);
        } else {
            super.init();
        }
    }

    protected void init() {
        super.init();
    }

    public ItemWithSlot(T item, int maxcnt) {
        // can use as storage unit
        this(item);
        this.maxCnt = maxcnt;
    }

    public boolean wasNull() {
        return slotFlag < 0;
    }

    public boolean safeAddAmount(int amount) {
        long result = amount + cnt;
        if (result > maxCnt) {
            return false;
        } else {
            setAmount(result);
            return true;
        }
    }
    /**
     * this arguments has no meaning ,just a formated argument
     * @param menu : avoid load BlockInventoryHolder in async thread
     */
    protected void updateMenu(@Nullable InventoryHolder menu) {
        if (dirty && !getCache().isAir()) {
            if (wasNull()) { // 空
                // 从数据源clone一个 正式转变为有实体的ItemStack 因为consumer那边可能是sfItem MultiItem
                if (getAmount() > 0) { // 非0
                    // wasNull  = false;
                    setSlot(getSlot());
                    //                    slotFlag = ~slotFlag;
                    // copy instance
                    cache = cache.deepcopy(CraftUtils::getCraftCopy);
                    updateItemStack();
                    if (menu != null) {
                        var cacheItem = cache.getItem();
                        var instance = NMSInventoryUtils.syncInventorySlot(menu, getSlot(), cacheItem);
                        // compare address,
                        if (instance != cacheItem) {
                            cache = instance != null ? cache.deepcopy((it) -> instance) : cache.empty();
                        }
                    }
                } // 若空且是0，寄.直接退出
            } else { // 不为空，同正常一样
                // 已经是之前有了的 可以直接修改
                updateItemStack();
                //try mark changed if vanilla
                // do not,  like hopper , too many update may be bad for the tps
                //todo: add stopUpdateSignal method
                if(false && menu instanceof TileState tileEntity) {
                    WorldUtils.tileEntitySetChange(tileEntity);
                }
            }
        }
    }

    public final void updateSource() {
        this.updateMenu(menu);
    }

    public void updateItemStack() {
        if (!getCache().isAir()) {
            super.updateItemStack();
        }
    }

    public void syncData() {
        if (!wasNull()) {
            super.syncData();
        } else {
            toNull();
        }
    }

    //    public void updateMenu(@Nonnull InventoryHolder menu) {
    //        if (dirty) {
    //            updateItemStack();
    //        }
    //    }

    public void grab(ItemCounter counter) {
        if (this.cache.isAir()) {
            if (counter != null && !counter.getCache().isAir() && counter.getAmount() > 0) setFrom(counter);
            else return;
        }
        long left = maxCnt - cnt;
        if (left > counter.getAmount()) {
            addAmount(counter.getAmount());
            counter.setAmount(0);
        } else {
            setAmount(maxCnt);
            counter.addAmount(-left);
        }
    }

    public void push(ItemCounter counter) {
        counter.grab(this);
    }

    //    public void setFrom(ItemCounter source) {
    //        // only when null AND source not null, can we setFrom
    //        // we don't support cache change
    //        if (wasNull() && (source != null && !source.getCache().isAir())) {
    //            fromSource(source, true);
    //        }
    //    }
    /**
     * should sync before
     * @param source
     */
    public boolean setFrom(ItemStackCache<T> source) {
        if ((wasNull() || getCache().isAir()) && (source != null && !source.isAir())) {
            fromSource(source, true);
            return true;
        }
        return false;
    }
    /**
     * transport item From target till limit count,return limit left
     * @param limit
     * @return
     */
    public long transportFrom(ItemCounter counter, int limit) {
        if (this.getCache().isAir()) {
            if (counter != null && !counter.getCache().isAir()) {
                setFrom(counter);
            } else return limit;
        }
        long left = Math.min(maxCnt - cnt, limit);
        // 如果这个数量比提供的少
        if (left > counter.getAmount()) {
            // 设置真正被传输的数量是... 提供的数量 小于预期left
            left = counter.getAmount();
            // counter清空
            counter.setAmount(0);
            // 加上
            addAmount(left);

        } else {
            // 否则这个数量提供的比那个多
            // 设置数量+=left
            setAmount(cnt + left);
            // left <= counter.getAmount()
            counter.addAmount(-left);
        }
        return limit - left;
    }

    protected ItemWithSlot clone() {
        return (ItemWithSlot) super.clone();
    }
}
