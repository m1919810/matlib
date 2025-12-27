package me.matl114.matlib.utils.entity.entityRecords;

import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Internal;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

public interface EntityRecord<T extends Entity> {
    public UUID getUuid();

    @Internal
    public Entity getEntity();

    /**
     * get Recorded Entity View,may not load, should check valid before getEntity
     */
    default T getEntityView() {
        return (T) getEntity();
    }

    public Location getLocation();

    @ForceOnMainThread
    public void setLocation(Location location);

    /**
     * check the record of presenting
     */
    public boolean isPresent();

    /**
     * check if entity is killed or unloaded
     */
    default boolean stillValid() {
        return isPresent() && getEntity().isValid();
    }

    /**
     * try load Entity if entity is invalid
     * return true when entity is loaded and can be accessed through getEntity
     */
    @ForceOnMainThread
    public boolean loadEntity();

    /**
     * load ,or throw Exception here
     * if you are not sure whether it is valid or not, please check
     * @return
     */
    @ForceOnMainThread
    @Nonnull
    default Optional<T> getOrLoadEntity() {
        if (loadEntity()) {
            return Optional.of(getEntityView());
        }
        return Optional.empty();
    }
    /**
     * load ,or throw Exception here
     * if you are not sure whether it is valid or not, please check
     * @return
     */
    @ForceOnMainThread
    @Nonnull
    default T forceGetOrLoadEntity() {
        Optional<T> entity = getOrLoadEntity();
        if (entity.isPresent()) {
            return entity.get();
        } else throw new RuntimeException("Unable to find or load entity " + getUuid());
    }

    /**
     * try force kill this Entity, if entity is not valid anyMore,try load it and do kill
     * return true if successfully killed
     */
    @ForceOnMainThread
    default boolean killEntity() {
        if (stillValid() || loadEntity()) {
            getEntity().remove();
            setNotPresent();
            return true;
        } else {
            return false;
        }
    }

    @Internal
    public void setNotPresent();
}
