package me.matl114.matlib.utils.stackCache;

import javax.annotation.Nonnull;
import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import org.bukkit.inventory.ItemStack;

public class ItemCounter<T extends ItemStackCache<T>> implements StackBuffer {
    // todo fix the unfreshed item dupe bug
    protected long cnt;
    protected boolean dirty;
    protected long maxCnt;
    // when -1,means item is up to date
    private long cachedItemAmount = -1;

    @Nonnull
    @Getter
    protected T cache;

    public final T getCache() {
        return cache;
    }

    public ItemCounter(T item, int amount) {
        dirty = false;
        this.cnt = amount;
        this.cachedItemAmount = this.cnt;
        this.cache = item;
        // for maxSize override
        this.maxCnt = cache.getMaxStackSize();
        this.maxCnt = maxCnt <= 0 ? Long.MAX_VALUE - 1 : maxCnt;
    }

    protected void toNull() {
        cache.setFromSource(cache.empty());
        cnt = 0;
        dirty = false;
        cachedItemAmount = 0;
    }

    protected void fromSource(ItemStackCache<T> source, boolean overrideMaxSize) {
        this.cache.setFromSource(cache);
        if (overrideMaxSize) {
            maxCnt = cache.getMaxStackSize();
        }
        cnt = 0;
        // do need clone?
        cachedItemAmount = -1;
        //
    }

    protected void itemChange() {
        cachedItemAmount = cache.getAmount();
    }

    public ItemCounter() {
        dirty = false;
    }

    public ItemCounter<T> createWith(ItemStack item) {
        ItemCounter<T> counter = this.clone();
        counter.init(item);
        return counter;
    }

    protected void init(ItemStack item) {
        this.cache.init(item);
        this.dirty = false;
        this.cnt = item.getAmount();
        this.maxCnt = item.getMaxStackSize();
        this.maxCnt = maxCnt <= 0 ? Long.MAX_VALUE - 1 : maxCnt;
        this.cachedItemAmount = cnt;
    }

    protected void init() {
        this.cache.init(null);
        this.dirty = false;
        this.cnt = 0;

        this.maxCnt = Long.MAX_VALUE - 1;
        this.cachedItemAmount = 0;
    }

    public long getMaxStackCnt() {
        return maxCnt;
    }

    public boolean isNull() {
        return cache.isAir();
    }

    public final boolean isFull() {
        return cnt >= this.maxCnt;
    }

    public final boolean isEmpty() {
        return cnt <= 0;
    }

    /**
     * make sure you know what you are doing!
     * @param meta
     */

    /**
     * get dirty bits
     * @return
     */
    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean t) {
        this.dirty = t;
    }

    /**
     * modify recorded amount
     * @param amount
     */
    public void setAmount(long amount) {
        dirty = dirty || amount != cnt;
        cnt = amount;
    }
    /**
     * get recorded amount
     */
    public long getAmount() {
        return cnt;
    }

    /**
     * modify recorded amount
     * @param amount
     */
    public void addAmount(long amount) {
        cnt += amount;
        dirty = dirty || (amount != 0);
    }

    /**
     * will sync amount and other data ,override by subclasses
     */
    public void syncData() {
        if (dirty) {
            cnt = cache.getAmount();
            dirty = false;
        }
    }

    /**
     * will only sync amount,keep the rest of data unchanged
     */
    public void syncAmount() {
        if (dirty) {
            cnt = cache.getAmount();
            dirty = false;
        }
    }

    @Override
    public void updateSource() {
        this.updateItemStack();
    }

    @Override
    public boolean setFrom(StackBuffer source) {
        if (source instanceof ItemCounter<?> counter) {
            return setFrom((ItemStackCache<T>) counter.getCache());
        }
        return false;
    }

    public boolean setFrom(ItemStackCache<T> source) {
        if ((getCache().isAir()) && (source != null && !source.isAir())) {
            fromSource((ItemStackCache<T>) source, true);
            return true;
        }
        return false;
    }

    /**
     * update amount of real itemstack ,or amount of real storage.etc
     */
    public void updateItemStack() {
        if (dirty) {
            // check if cachedItemAmount is not refreshed
            if (cachedItemAmount < 0) {
                cache.getItem().setAmount(MathUtils.clampToInt(cnt));
            } else {
                int newCachedItemAmount = cache.getAmount();
                cnt += -cachedItemAmount + newCachedItemAmount;
                cache.getItem().setAmount(MathUtils.clampToInt(cnt));
            }
            cachedItemAmount = cnt;

            dirty = false;
        }
    }

    /**
     * consume other counter ,till one of them got zero
     * @param other
     */
    public void consume(ItemCounter other) {
        long diff = (other.getAmount() > cnt) ? cnt : other.getAmount();
        cnt -= diff;
        dirty = true;
        other.addAmount(-diff);
    }

    public long consume(long cnt2) {
        long diff = Math.min(cnt2, cnt);
        cnt -= diff;
        dirty = true;
        return cnt2 - diff;
    }

    /**
     * grab other counter till maxSize or sth
     * @param other
     */
    public void grab(ItemCounter other) {
        cnt += other.getAmount();
        dirty = true;
        other.setAmount(0);
    }

    public long grab(long grab) {
        long left = maxCnt - cnt;
        if (left > grab) {
            addAmount(grab);
            return 0;
        } else {
            setAmount(maxCnt);
            return grab - left;
        }
    }

    /**
     * push to other counter till maxsize or sth
     * @param other
     */
    public void push(ItemCounter other) {
        other.grab(this);
    }

    protected ItemCounter<T> clone() {
        try {
            ItemCounter<T> counter = (ItemCounter) super.clone();
            // copy te cache reference
            counter.cache = this.cache.copy();
            return counter;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public final ItemCounter<T> copy() {
        return clone();
    }
}
