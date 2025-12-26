package me.matl114.matlib.nmsMirror.inventory;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.List;
import java.util.function.Predicate;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.Location;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.InventoryHolder;

@MultiDescriptive(targetDefault = ContainerClass)
public interface ContainerHelper extends TargetDescriptor {
    @MethodTarget
    int getContainerSize(Object container);

    @MethodTarget
    boolean isEmpty(Object container);

    @MethodTarget
    Object getItem(Object container, int index);

    @MethodTarget
    Object removeItem(Object container, int index, int amount);

    @MethodTarget
    Object removeItemNoUpdate(Object container, int index);

    @MethodTarget
    void setItem(Object container, int slot, @RedirectType(ItemStack) Object item);

    @MethodTarget
    int getMaxStackSize(Object container);

    @MethodTarget
    void setChanged(Object container);

    @MethodTarget
    boolean stillValid(Object container, @RedirectType(Player) Object player);

    @MethodTarget
    boolean canPlaceItem(Object container, int slot, @RedirectType(ItemStack) Object item);

    @MethodTarget
    boolean canTakeItem(
            Object container, @RedirectType(Container) Object hopper, int slot, @RedirectType(ItemStack) Object item);

    @MethodTarget
    boolean hasAnyMatching(Object container, Predicate<?> predicate);

    @MethodTarget(isStatic = true)
    boolean stillValidBlockEntity(@RedirectType(BlockEntity) Object blockEntity, @RedirectType(Player) Object player);

    @MethodTarget
    List<?> getContents(Object container);

    @MethodTarget
    List<HumanEntity> getViewers(Object container);

    @MethodTarget
    InventoryHolder getOwner(Object container);

    @MethodTarget
    void setMaxStackSize(Object container, int size);

    @MethodTarget
    Location getLocation(Object container);

    @ConstructorTarget
    @RedirectClass("org.bukkit.craftbukkit.inventory.CraftInventoryCustom$MinecraftInventory")
    Object newCustomContainer(InventoryHolder holder, int size, String title);
}
