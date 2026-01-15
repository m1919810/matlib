package me.matl114.matlib.implement.bukkit.entity.display.Implementation;

import static me.matl114.matlib.algorithms.algorithm.TransformationUtils.*;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroup;
import me.matl114.matlib.core.bukkit.entity.groups.implement.SingleGroupManager;
import me.matl114.matlib.implement.bukkit.entity.display.DisplayManager;
import me.matl114.matlib.utils.ThreadUtils;
import org.bukkit.Location;
import org.bukkit.entity.Display;
import org.joml.Vector3f;

@Deprecated(forRemoval = true)
public abstract class SimpleDisplayManager extends SingleGroupManager<EntityGroup<Display>> implements DisplayManager {
    Location core;
    Map<String, DisplayPart> partMap;
    Deque<LCTransformation> transformationStack;
    Vector3f reshape = ID_SCALE;
    private static final int MAX_STACK_SIZE = 32;

    public SimpleDisplayManager() {
        this.partMap = new HashMap<>();
        this.transformationStack = new ArrayDeque<>();
        this.transformationStack.addLast(LCTransformation.ofIdentical());
    }

    private void addLastInternal(LCTransformation transformation) {
        if (this.transformationStack.size() > MAX_STACK_SIZE) {
            this.transformationStack.pollFirst();
        }
        this.transformationStack.addLast(transformation);
    }
    //    public void setDisplayGroup(EntityGroup<Display> group) {
    //        this.setHandle(group);
    //    }
    @Override
    public Location getCoreLocation() {
        return core;
    }

    @Override
    public void setCoreLocation(Location location) {
        core = location;
        updateEntityLocation();
    }

    public void updateEntityLocation() {
        ThreadUtils.executeSync(this::updateLocation);
    }

    public SimpleDisplayManager addDisplayPart(DisplayPart part) {
        partMap.put(part.partIdentifier, part);
        return this;
    }

    @ForceOnMainThread
    public DisplayManager buildDisplay(Location location, EntityGroup.EntityGroupBuilder<Display> builder) {
        this.core = location;
        builder.startLoad(this);
        buildAt(location, builder::addChild);
        setHandle(builder.build(this));
        updateStatus(true);
        return this;
    }

    @Override
    public Map<String, DisplayPart> getDisplayParts() {
        return partMap;
    }

    @Override
    public EntityGroup<Display> getDisplayGroup() {
        return getHandle();
    }

    @Override
    public void reshapeBase(Vector3f scale, boolean forceEntityUpdate) {
        reshape = cloneVec(scale);
        updateStatus(forceEntityUpdate);
    }

    public boolean isShrinkable() {
        return true;
    }

    @Override
    public void undo() {
        LCTransformation lastTran = transformationStack.pollLast();
        if (lastTran == null) {
            transformationStack.addLast(LCTransformation.ofIdentical());
        }
        updateStatus(true);
    }

    @Override
    public void undoAll() {
        transformationStack.clear();
        transformationStack.addLast(LCTransformation.ofIdentical());
        reshape = ID_SCALE;
        updateStatus(true);
    }

    @Override
    public LCTransformation getCurrentTransformation() {
        LCTransformation ltran = transformationStack.peekLast();
        if (ltran == null) {
            ltran = LCTransformation.ofIdentical();
        }
        return ltran;
    }

    @Override
    public void setCurrentTransformation(LCTransformation transformation) {
        addLastInternal(transformation);
    }

    public Vector3f getCurrentReshape() {
        return reshape;
    }

    public void setCurrentReshape(Vector3f reshape) {
        this.reshape = cloneVec(reshape);
    }
}
