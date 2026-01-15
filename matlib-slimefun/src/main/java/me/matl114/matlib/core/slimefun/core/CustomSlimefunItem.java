package me.matl114.matlib.core.slimefun.core;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.*;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;

public class CustomSlimefunItem extends SlimefunItem {
    public List<ItemStack> displayedMemory;
    public List<ItemStack> originalMemory;

    public CustomSlimefunItem(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        this(itemGroup, item, recipeType, recipe, new ArrayList<>());
    }

    public CustomSlimefunItem(
            ItemGroup itemGroup,
            SlimefunItemStack item,
            RecipeType recipeType,
            ItemStack[] recipe,
            List<ItemStack> displayInfo) {
        super(itemGroup, item, recipeType, recipe);
        if (displayInfo != null) {
            this.originalMemory = displayInfo;
        } else {
            this.originalMemory = new ArrayList<>();
        }
    }

    public boolean canStack(@Nonnull ItemMeta var1, @Nonnull ItemMeta var2) {
        PersistentDataContainer container1 = var1.getPersistentDataContainer();
        PersistentDataContainer container2 = var2.getPersistentDataContainer();
        return (container1.equals(container2));
    }

    public List<MachineRecipe> provideDisplayRecipe() {
        return new ArrayList<>();
    }

    public CustomSlimefunItem addHandler(ItemHandler handler) {
        this.addItemHandler(handler);
        return this;
    }

    public final void addHandlerForce(ItemHandler... handlers) {
        ItemState state = getState();
        ReflectUtils.setFieldRecursively(this, "state", ItemState.UNREGISTERED);
        try {
            addItemHandler(handlers);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        ReflectUtils.setFieldRecursively(this, "state", state);
    }

    public CustomSlimefunItem setDisplayRecipes(List<ItemStack> displayRecipes) {
        this.originalMemory = displayRecipes;
        return this;
    }

    public CustomSlimefunItem addDisplayRecipe(ItemStack stack) {
        if (originalMemory == null || originalMemory.isEmpty()) {
            originalMemory = new ArrayList<>();
        }
        this.originalMemory.add(stack);
        return this;
    }

    public CustomSlimefunItem setOutput(ItemStack obj) {
        if (obj != null) this.recipeOutput = new ItemStack(obj);
        return this;
    }

    public void register(@Nonnull SlimefunAddon addon) {
        super.register(addon);
        CustomRegistries.getManager().registerInternal(getId(), this);
    }

    public CustomSlimefunItem registerThis(SlimefunAddon plugin) {
        if (plugin != null) {
            register(plugin);
        } else {
            Debug.logger("找不到附属实例!  注册信息: " + this.toString());
        }
        return this;
    }

    public void preRegister() {
        super.preRegister();
    }

    public void postRegister() {
        super.postRegister();
    }
}
