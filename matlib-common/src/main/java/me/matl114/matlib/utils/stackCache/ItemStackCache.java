package me.matl114.matlib.utils.stackCache;

import java.util.function.Function;
import me.matl114.matlib.common.lang.annotations.ConstVal;
import me.matl114.matlib.common.lang.annotations.DoNotOverride;
import me.matl114.matlib.common.lang.annotations.Protected;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public interface ItemStackCache<T extends ItemStackCache<T>> {
    public Material getType();

    public ItemStack getItem();

    @DoNotOverride
    default int getAmount() {
        return getType() == Material.AIR ? 0 : getItem().getAmount();
    }

    @DoNotOverride
    default int getMaxStackSize() {
        return getType() == Material.AIR ? 0 : getItem().getMaxStackSize();
    }

    @DoNotOverride
    default boolean isAir() {
        return getType().isAir();
    }

    public boolean matchItem(T cache, boolean compareLore);

    /**
     * same as clone
     * @return
     */
    public T copy();
    /**
     * copy internal, left else unchanged
     * @param copyFunction
     */
    public T deepcopy(Function<ItemStack, ItemStack> copyFunction);

    @ConstVal
    public T empty();

    @MustBeInvokedByOverriders
    @Protected
    public void init(ItemStack cache);

    @MustBeInvokedByOverriders
    public void setFromSource(T source);

    public boolean equalsCache(ItemStackCache<?> unknown);
}
