package me.matl114.matlib.utils.stackCache;

import java.util.Objects;
import java.util.function.Function;
import javax.annotation.Nullable;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class AbstractItemStackCache<T extends AbstractItemStackCache<T>>
        implements Cloneable, ItemStackCache<T> {
    @Getter
    @Nullable protected ItemStack item;

    @Getter
    protected Material type;

    @MustBeInvokedByOverriders
    public void init(ItemStack item) {
        this.item = item;
        this.type = item == null ? Material.AIR : item.getType();
    }

    @MustBeInvokedByOverriders
    public void setFromSource(T cache) {
        this.item = cache.item;
        this.type = cache.type;
    }

    @Override
    public AbstractItemStackCache<T> clone() {
        try {
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return (AbstractItemStackCache) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public T deepcopy(Function<ItemStack, ItemStack> copyFunction) {
        var copy = copy();
        copy.item = copyFunction.apply(copy.item);
        return copy;
    }

    @Override
    public abstract boolean matchItem(T cache, boolean compareLore);

    @Override
    public boolean equalsCache(ItemStackCache<?> unknown) {
        return Objects.equals(this.item, unknown.getItem());
    }

    @Override
    public T copy() {
        return (T) clone();
    }

    @Override
    public abstract T empty();

    @Override
    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof ItemStackCache<?> cache) {
            return equalsCache(cache);
        } else return false;
    }
}
