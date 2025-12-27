package me.matl114.matlib.utils.entity.entityRecords;

import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.entity.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

@Note(
        "This kind of EntityRecord represents a fixed Entity, which do not move its Location,so when chunk unload,you can load it via recorded location")
@Getter
@AllArgsConstructor
public class FixedEntityRecord<T extends Entity> implements EntityRecord<T> {
    Entity entity;
    UUID uuid;
    Location location;

    boolean present;

    @Override
    public void setLocation(Location location) {
        getOrLoadEntity().ifPresent(e -> e.teleport(location));
        this.location = entity.getLocation();
    }

    @Override
    public boolean loadEntity() {
        if (isPresent() && !entity.isValid()) {

            if (EntityUtils.executeOnSameEntity(entity, (i) -> this.entity = i)) {
                return true;
            } else {
                setNotPresent();
                return false;
            }
        } else return present;
    }

    @Override
    public void setNotPresent() {
        this.present = false;
    }

    @Nonnull
    public static <W extends Entity> EntityRecord<W> ofFixedEntity(W entity) {
        return new FixedEntityRecord(entity, entity.getUniqueId(), entity.getLocation(), true);
    }

    @Nullable public static EntityRecord<?> ofFixedEntity(UUID uuid) {
        Entity entity1 = Bukkit.getEntity(uuid);
        if (entity1 != null && entity1.isValid()) {
            return ofFixedEntity(entity1);
        } else return null;
    }
}
