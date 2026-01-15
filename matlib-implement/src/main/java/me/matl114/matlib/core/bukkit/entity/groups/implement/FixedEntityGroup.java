package me.matl114.matlib.core.bukkit.entity.groups.implement;

import com.google.common.base.Preconditions;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.PairList;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroup;
import me.matl114.matlib.core.bukkit.entity.groups.EntityGroupManager;
import me.matl114.matlib.utils.entity.entityRecords.EntityRecord;
import me.matl114.matlib.utils.entity.entityRecords.FixedEntityRecord;
import me.matl114.matlib.utils.persistentDataContainer.AbstractStringList;
import me.matl114.matlib.utils.persistentDataContainer.PdcUtils;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class FixedEntityGroup<T extends Entity, W extends Entity> extends ListedEntityGroup<T> {
    /**
     * this flag will be set to true if loaded from prev data or built by builder
     * when enable, it will set Entity uid in parent and sons, so that programs can reload it after server restart
     */
    @Getter
    private boolean dataRecordEnabled = false;

    @Nullable private String source;

    @Nonnull
    private NamespacedKey sonListKey;

    @Nonnull
    private NamespacedKey parentKey;

    @Nonnull
    private NamespacedKey sourceKey;

    @Nullable @Getter
    private EntityRecord<W> parent;

    private boolean temp = false;
    private static final AtomicInteger rawGroupIdCounter = new AtomicInteger(0);

    @Override
    public String getGroupIdentifier() {
        return "fixed_uid" + (parentUid == null ? String.valueOf(rawGroupIdCounter.getAndIncrement()) : parentUid);
    }

    @Nonnull
    protected AbstractStringList listPdcType;

    @Nullable @Getter
    private String parentUid;

    public static final String PLACEHOLDER = "†";

    private void setNS(String namespace) {
        this.sonListKey = new NamespacedKey(namespace, "child-list");
        this.sourceKey = new NamespacedKey(namespace, "source");
        this.parentKey = new NamespacedKey(namespace, "parent-uid");
        this.listPdcType = new AbstractStringList(namespace);
    }

    private void setNS(Plugin namespace) {
        this.sonListKey = new NamespacedKey(namespace, "child-list");
        this.sourceKey = new NamespacedKey(namespace, "source");
        this.parentKey = new NamespacedKey(namespace, "parent-uid");
        this.listPdcType = new AbstractStringList(namespace);
    }

    public FixedEntityGroup(String namespace, W entityParent) {
        super(FixedEntityRecord::ofFixedEntity);
        setParent(entityParent);
        // membersUUIDMap.put(parent.getUuid(),"parent");
        setNS(namespace);
    }

    public FixedEntityGroup(Plugin namespace, W entityParent) {
        super(FixedEntityRecord::ofFixedEntity);
        setParent(entityParent);
        setNS(namespace);
    }

    public FixedEntityGroup<T, W> setParent(W entityParent) {
        if (entityParent != null) {
            parent = FixedEntityRecord.ofFixedEntity(entityParent);
            parentUid = parent.getUuid().toString();
            if (isDataRecordEnabled()) {
                startRecordingData();
            }
        } else {
            parent = null;
            parentUid = null;
        }
        return this;
    }

    public FixedEntityGroup<T, W> loadFrom(W parent) {
        Preconditions.checkState(!dataRecordEnabled, "This group already starts recording data in current parent");
        setParent(parent);
        loadFrom();
        return this;
    }

    public FixedEntityGroup<T, W> loadFrom() {
        Preconditions.checkState(!dataRecordEnabled, "This group already starts recording data in current parent");
        Preconditions.checkNotNull(this.parent, "Could not load group data from null!");
        tryBuild();
        return this;
    }

    public static <R extends Entity, S extends Entity> FixedEntityGroupBuilder<R, S> builder(
            String namespace, S parent, boolean temp) {
        return new FixedEntityGroupBuilder<R, S>(namespace, parent).temp(true);
    }

    public static <R extends Entity, S extends Entity> FixedEntityGroupBuilder<R, S> builder(
            Plugin namespace, S parent, boolean temp) {
        return new FixedEntityGroupBuilder<R, S>(namespace, parent).temp(true);
    }

    protected void setChildData(T e) {
        PdcUtils.setOrRemove(e.getPersistentDataContainer(), sourceKey, PersistentDataType.STRING, source);
        PdcUtils.setOrRemove(e.getPersistentDataContainer(), parentKey, PersistentDataType.STRING, parentUid);
    }

    protected void removeChildData(T e) {
        PdcUtils.setOrRemove(e.getPersistentDataContainer(), sourceKey, PersistentDataType.STRING, null);
        PdcUtils.setOrRemove(e.getPersistentDataContainer(), parentKey, PersistentDataType.STRING, null);
    }

    @Override
    public void postAddSyncTask(String childName, T entityChild) {
        // add Data in addMember
        if (dataRecordEnabled) {
            updateSonList();
        }
    }

    @Override
    public void postRemoveSyncTask(String childName, T entityChild) {
        if (dataRecordEnabled) {
            removeChildData(entityChild);
            updateSonList();
        }
    }

    @ForceOnMainThread
    public void addMemberSync(String childName, T entityChild) {
        if (dataRecordEnabled) {
            setChildData(entityChild);
        }
        super.addMemberSync(childName, entityChild);
    }

    public void addMemberAsync(String childName, T entityChild) {
        if (dataRecordEnabled) {
            setChildData(entityChild);
        }
        super.addMemberAsync(childName, entityChild);
    }

    @ForceOnMainThread
    public Optional<T> removeMemberSync(String childName) {
        return super.removeMemberSync(childName);
    }

    public boolean removeMemberAsync(String childName) {
        return super.removeMemberAsync(childName);
    }

    @Override
    public boolean autoRemovalOnShutdown() {
        return temp;
    }

    public FixedEntityGroup<T, W> setAutoRemovalOnShutdown(boolean temp) {
        this.temp = temp;
        return this;
    }

    @ForceOnMainThread
    public void killGroup() {
        if (parent != null) {
            parent.killEntity();
        }
        applyToAllMemberRecords(EntityRecord::killEntity);
    }

    /**
     * start recording data in parent(if present) and sons, using recorded data, you can rebuild entitygroup from parent if your lost track of entity group
     */
    @ForceOnMainThread(condition = "when load entity from unloaded chunks")
    public void startRecordingData() {
        if (parent != null) {
            PersistentDataContainer container = parent.forceGetOrLoadEntity().getPersistentDataContainer();
            PdcUtils.setOrRemove(container, sourceKey, PersistentDataType.STRING, source);
            PdcUtils.setOrRemove(container, sonListKey, listPdcType, genChildList());
        }
        for (var it : members) {
            it.getB().getOrLoadEntity().ifPresent(this::setChildData);
        }

        dataRecordEnabled = true;
    }

    private List<String> genChildList() {
        List<String> list = new ArrayList<String>(members.size() + 1);
        for (var it : members) {
            list.add(it.getA() + PLACEHOLDER + it.getB().getUuid().toString());
        }
        return list;
    }

    private static PairList<String, UUID> genChildFromList(List<String> childs) {
        PairList<String, UUID> list = new PairList<>(childs.size() + 1);
        for (String it : childs) {
            try {
                String[] args = it.split(PLACEHOLDER);
                UUID uuid = UUID.fromString(args[1]);
                if (uuid != null) {
                    list.put(args[0], uuid);
                }
            } catch (Throwable e) {
            }
        }
        return list;
    }

    @Note("Invoke this method when parent is NOT NULL,because you are trying to load a GROUP from parent's NBT")
    private void tryBuild() {
        Entity entity = parent.forceGetOrLoadEntity();
        PersistentDataContainer container = entity.getPersistentDataContainer();
        if (container.has(sonListKey, listPdcType)) {
            var sons = genChildFromList(PdcUtils.getOrDefault(container, sonListKey, listPdcType, List.of()));
            for (var it : sons) {
                EntityRecord<?> record = FixedEntityRecord.ofFixedEntity(it.getB());
                if (record != null) {
                    putChildInternal(it.getA(), (EntityRecord<T>) record);
                }
            }
        }
        startRecordingData();
    }

    private void updateSonList() {
        if (parent != null) {
            parent.getOrLoadEntity().ifPresent(e -> {
                PdcUtils.setOrRemove(e.getPersistentDataContainer(), sonListKey, listPdcType, genChildList());
            });
        }
    }

    public static class FixedEntityGroupBuilder<T extends Entity, W extends Entity> implements EntityGroupBuilder<T> {
        private FixedEntityGroup<T, W> group;

        @ForceOnMainThread
        public FixedEntityGroupBuilder(Plugin namespace, W entityParent) {
            group = new FixedEntityGroup<>(namespace, entityParent);
        }

        @ForceOnMainThread
        public FixedEntityGroupBuilder(String namespace, W entityParent) {
            group = new FixedEntityGroup<>(namespace, entityParent);
        }

        public FixedEntityGroupBuilder<T, W> temp(boolean temp) {
            group.temp = temp;
            return this;
        }

        public FixedEntityGroupBuilder<T, W> setSource(String source) {
            this.group.source = source;
            return this;
        }

        public FixedEntityGroupBuilder<T, W> addChild(String childName, T child) {
            group.putChildInternal(childName, FixedEntityRecord.ofFixedEntity(child));
            return this;
        }

        public FixedEntityGroupBuilder<T, W> addChild(T child) {
            int size = group.members.size();
            String defaultName = "_Es" + size;
            return addChild(defaultName, child);
        }

        @ForceOnMainThread
        public FixedEntityGroup<T, W> build(@Nullable EntityGroupManager manager) {
            this.group.startRecordingData();
            if (manager != null) this.group.register(manager);
            return group;
        }

        @Override
        public EntityGroupBuilder<T> addChild(String childName, Supplier<T> childSupplier) {
            return addChild(childName, childSupplier.get());
        }

        @Override
        public EntityGroupBuilder<T> startLoad(EntityGroupManager<EntityGroup<T>> manager) {
            return this;
        }
    }
}
