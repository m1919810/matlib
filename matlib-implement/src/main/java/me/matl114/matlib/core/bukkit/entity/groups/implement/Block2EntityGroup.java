package me.matl114.matlib.core.bukkit.entity.groups.implement;

import lombok.Setter;
import me.matl114.matlib.algorithms.dataStructures.struct.Union;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.entity.entityRecords.FixedEntityRecord;
import me.matl114.matlib.utils.persistentDataContainer.PdcUtils;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class Block2EntityGroup<W extends Entity> extends AbstractBlock2EntityGroup<W> {
    final Union<String, Plugin> namespace;
    NamespacedKey sourceKey;
    NamespacedKey sourceLocationKey;

    @Setter
    String source;

    private void setNS() {
        this.sourceKey = namespace.isA()
                ? new NamespacedKey(namespace.getA(), "source")
                : new NamespacedKey(namespace.getB(), "source");
        this.sourceLocationKey = namespace.isA()
                ? new NamespacedKey(namespace.getA(), "source")
                : new NamespacedKey(namespace.getB(), "source-loc");
    }

    public Block2EntityGroup(String namespace, Location loc) {
        super(FixedEntityRecord::ofFixedEntity, loc);
        this.namespace = Union.ofA(namespace);
    }

    @Override
    public void postAddSyncTask(String childName, Entity entityChild) {
        PersistentDataContainer pc = entityChild.getPersistentDataContainer();
        PdcUtils.setOrRemove(pc, sourceKey, PersistentDataType.STRING, source);
        PdcUtils.setOrRemove(
                pc, sourceLocationKey, PersistentDataType.STRING, TextUtils.blockLocationToString(blockPos));
    }

    @Override
    public void postRemoveSyncTask(String childName, Entity entityChild) {
        PersistentDataContainer pc = entityChild.getPersistentDataContainer();
        pc.remove(sourceKey);
        pc.remove(sourceLocationKey);
    }
}
