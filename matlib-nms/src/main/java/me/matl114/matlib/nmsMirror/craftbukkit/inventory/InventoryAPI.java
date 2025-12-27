package me.matl114.matlib.nmsMirror.craftbukkit.inventory;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

@MultiDescriptive(targetDefault = "org.bukkit.craftbukkit.inventory.CraftInventory")
public interface InventoryAPI extends TargetDescriptor {
    @MethodTarget
    Object getInventory(Inventory inventory);

    @ConstructorTarget
    Inventory createInventory(@RedirectType(Container) Object container);

    @CastCheck("org.bukkit.craftbukkit.inventory.CraftInventory")
    boolean isCraftInventory(Inventory inv);

    @ConstructorTarget
    @RedirectClass("org.bukkit.craftbukkit.inventory.CraftInventoryCustom")
    Inventory createCustomInventory(InventoryHolder holder, int size, String title);
}
