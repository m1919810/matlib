package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

@MultiDescriptive(targetDefault = "net.minecraft.world.level.block.entity.BaseContainerBlockEntity")
public interface BlockEntityAPI extends TargetDescriptor {
    @CastCheck(ContainerClass)
    boolean isContainer(Object blockEntity);

    @CastCheck(HopperBlockEntityClass)
    boolean isHopper(Object hopperEntity);

    @MethodTarget(isStatic = true)
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("tryTakeInItemFromSlot")
    @IgnoreFailure(below = true, thresholdInclude = Version.v1_20_R4)
    default boolean hopper$tryTakeInItemFromSlot(
            @RedirectType("Lnet/minecraft/world/level/block/entity/Hopper;") Object hopper,
            @RedirectType(Container) Object container,
            int index,
            @RedirectType(Direction) Object direction,
            @RedirectType(Level) Object world) {
        var itemStack = NMSItem.CONTAINER.getItem(container, index);
        if (!NMSItem.ITEMSTACK.isEmpty(itemStack)
                && hopper$canTakeItemFromContainer(hopper, container, itemStack, index, direction)) {
            return hopper$hopperPull(world, hopper, container, itemStack, index);
        }
        return false;
    }

    @MethodTarget(isStatic = true)
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("canTakeItemFromContainer")
    boolean hopper$canTakeItemFromContainer(
            @RedirectType(Container) Object hopper,
            @RedirectType(Container) Object from,
            @RedirectType(ItemStack) Object itemStack,
            int slot,
            @RedirectType(Direction) Object facing);

    @MethodTarget(isStatic = true)
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("hopperPull")
    boolean hopper$hopperPull(
            @RedirectType(Level) Object level,
            @RedirectType("Lnet/minecraft/world/level/block/entity/Hopper;") Object hopper,
            @RedirectType(Container) Object container,
            @RedirectType(ItemStack) Object itemStack,
            int index);

    @MethodTarget(isStatic = true)
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("tryMoveInItem")
    Object hopper$tryMoveInItem(
            @RedirectType(Container) Object from,
            @RedirectType(Container) Object to,
            @RedirectType(ItemStack) Object item,
            int slot,
            @RedirectType(Direction) Object direction);

    @FieldTarget(isStatic = true)
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("skipPullModeEventFireSetter")
    void hopper$setSkipPullModeEventFire(boolean s);

    @MethodTarget
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("setCooldown")
    void hopper$setCooldown(Object hopper, int transferCooldown);

    @FieldTarget
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("cooldownTimeGetter")
    int hopper$cooldown(Object hopper);

    @FieldTarget(isStatic = true)
    @RedirectClass(HopperBlockEntityClass)
    @RedirectName("skipHopperEventsGetter")
    boolean hopper$skipHopperEvents();
}
