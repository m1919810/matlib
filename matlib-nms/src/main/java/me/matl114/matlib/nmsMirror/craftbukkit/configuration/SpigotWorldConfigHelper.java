package me.matl114.matlib.nmsMirror.craftbukkit.configuration;

import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "org.spigotmc.SpigotWorldConfig")
public interface SpigotWorldConfigHelper extends TargetDescriptor {
    @FieldTarget
    int simulationDistanceGetter(Object config);

    @FieldTarget
    byte mobSpawnRangeGetter(Object config);

    @FieldTarget
    int itemDespawnRateGetter(Object config);

    @FieldTarget
    int hopperTransferGetter(Object config);

    @FieldTarget
    void hopperTransferSetter(Object config, int val);

    @FieldTarget
    int hopperAmountGetter(Object config);

    @FieldTarget
    void hopperAmountSetter(Object config, int val);
}
