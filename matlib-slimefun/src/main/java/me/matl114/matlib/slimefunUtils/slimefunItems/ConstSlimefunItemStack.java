package me.matl114.matlib.slimefunUtils.slimefunItems;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import java.lang.reflect.Field;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ConstSlimefunItemStack extends SlimefunItemStack {
    public SlimefunItemStack data;
    public ItemMeta thismeta;
    Field lockedField;

    public ConstSlimefunItemStack(SlimefunItemStack stack) {
        super(stack.getItemId(), stack);
        this.data = stack;
        this.thismeta = stack.getItemMeta();
        this.lockedField =
                ReflectUtils.getFieldsRecursively(this.getClass(), "locked").getA();
        try {
            Object locked = this.lockedField.get(this);
            this.lockedField.set(this, Boolean.FALSE);
            this.setItemMeta(data.getItemMeta());
            this.lockedField.set(this, locked);
        } catch (Throwable e) {

        }
    }

    public ItemStack clone() {
        SlimefunItemStack stack = (SlimefunItemStack) super.clone();
        try {
            Object locked = this.lockedField.get(stack);
            this.lockedField.set(stack, Boolean.FALSE);
            stack.setItemMeta(thismeta);
            this.lockedField.set(stack, locked);
        } catch (Throwable e) {

        }
        return stack;
    }
}
