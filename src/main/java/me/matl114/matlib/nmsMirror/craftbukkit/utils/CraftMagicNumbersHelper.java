package me.matl114.matlib.nmsMirror.craftbukkit.utils;

import me.matl114.matlib.nmsMirror.Import;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.Material;

@Descriptive(target = "org.bukkit.craftbukkit.util.CraftMagicNumbers")
public interface CraftMagicNumbersHelper extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    @RedirectName("getMaterial")
    Material getMaterialFromBlock(@RedirectType(Import.Block)Object block);

    @MethodTarget(isStatic = true)
    @RedirectName("getMaterial")
    Material getMaterialFromItem(@RedirectType(Import.Item)Object item);

    @MethodTarget(isStatic = true)
    Object getItem(Material mat);

    @MethodTarget(isStatic = true)
    Object getBlock(Material mat);

    @MethodTarget(isStatic = true)
    @RedirectName("key")
    Object toResourceLocation(Material mat);

}
