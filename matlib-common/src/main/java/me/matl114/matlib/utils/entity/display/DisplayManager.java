package me.matl114.matlib.utils.entity.display;

import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.utils.entity.groups.EntityGroup;
import me.matl114.matlib.utils.entity.groups.EntityGroupManager;
import org.bukkit.Location;
import org.bukkit.entity.*;

public interface DisplayManager extends EntityGroupManager<EntityGroup<Display>>, BuildableBluePrinted {
    @ForceOnMainThread
    public DisplayManager buildDisplay(Location location, EntityGroup.EntityGroupBuilder<Display> entityGroupCreator);
}
