package me.matl114.matlib.implement.bukkit.entity.display;

import static me.matl114.matlib.algorithms.algorithm.TransformationUtils.shrink;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import me.matl114.matlib.algorithms.algorithm.TransformationUtils;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Protected;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroup;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;

/**
 * this interface represents the building from a blueprint to real-world display gathered in a group
 */
public interface BuildableBluePrinted extends BluePrinted {
    /**
     * a display group should holds a core Location, which determines its abs-location
     * @return
     */
    public Location getCoreLocation();

    /**
     * should tp to move location
     * @param location
     */
    public void setCoreLocation(Location location);

    @Protected
    @ForceOnMainThread
    default void updateLocation() {
        Location core = getCoreLocation();
        this.getDisplayGroup().applyToAllMemberRecords(entityRecord -> {
            entityRecord.setLocation(core);
        });
    }

    /**
     * used for building Display context for Model
     * @param location
     * @param createIfNoExist
     */
    @Protected
    default void buildAt(Location location, BiConsumer<String, Supplier<Display>> createIfNoExist) {
        for (var part : getDisplayParts().entrySet()) {
            createIfNoExist.accept(
                    part.getKey(), () -> part.getValue().getContext().newEntity(location));
        }
    }

    default void updateStatus(boolean force) {
        Map<String, Transformation> transMap = new HashMap<>();
        TransformationUtils.LCTransformation ltran = getCurrentTransformation();
        Vector3f reshape = getCurrentReshape();
        for (var part : getDisplayParts().entrySet()) {
            transMap.put(
                    part.getKey(), ltran.transformOrigin(shrink(part.getValue().getTransformation(), reshape)));
        }

        // todo effect to origin transformation
        // todo add conjugate operation
        // 什么玩意 忽略
        if (force) {
            getDisplayGroup().applyToAllMembers((str, display) -> {
                var trans = transMap.get(str);
                display.setTransformation(trans);
            });
        } else {
            getDisplayGroup().applyToAllMembersUnsafe((str, display) -> {
                display.setTransformation(transMap.get(str));
            });
        }
    }

    public EntityGroup<Display> getDisplayGroup();
}
