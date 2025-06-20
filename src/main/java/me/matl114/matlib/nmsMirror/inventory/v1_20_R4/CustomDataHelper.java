package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;

import static me.matl114.matlib.nmsMirror.Import.*;

@Descriptive(target = "net.minecraft.world.item.component.CustomData")
public interface CustomDataHelper extends TargetDescriptor {
    ;
    @MethodTarget
    Object getUnsafe(Object customData);

    @MethodTarget(isStatic = true)
    Object of(@RedirectType(CompoundTag) Object nbt);

    @ConstructorTarget
    Object ofNoCopy(@RedirectType(CompoundTag) Object nbt);

    default Object unsafeOrNull(Object input){
        return input== null ? null : getUnsafe(input);
    }

    default Object copyCustomData(Object val){
        return val == null? null: of(getUnsafe(val));
    }
}
