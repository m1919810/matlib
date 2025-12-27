package me.matl114.matlib.nmsMirror.craftbukkit.utils;

import java.util.Map;
import me.matl114.matlib.nmsMirror.Import;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import org.bukkit.Material;

@MultiDescriptive(targetDefault = "org.bukkit.craftbukkit.util.CraftMagicNumbers")
public interface MagicNumberAPI extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    @RedirectName("getMaterial")
    Material getMaterialFromBlock(@RedirectType(Import.Block) Object block);

    @FieldTarget(isStatic = true)
    Map<Object, Material> ITEM_MATERIALGetter();

    @FieldTarget(isStatic = true)
    Map<Material, Object> MATERIAL_ITEMGetter();

    @FieldTarget(isStatic = true)
    Map<Object, Material> BLOCK_MATERIALGetter();

    @FieldTarget(isStatic = true)
    Map<Material, Object> MATERIAL_BLOCKGetter();

    @MethodTarget(isStatic = true)
    @RedirectName("getMaterial")
    Material getMaterialFromItem(@RedirectType(Import.Item) Object item);

    @MethodTarget(isStatic = true)
    Object getItem(Material mat);

    @MethodTarget(isStatic = true)
    Object getBlock(Material mat);

    //    @MethodTarget(isStatic = true)
    //    @RedirectName("key")
    //    Object toResourceLocation(Material mat);

}
