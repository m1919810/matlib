package me.matl114.matlib.utils.entity.groups.implement;

import java.util.*;
import java.util.function.Function;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.PairList;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.utils.ThreadUtils;
import me.matl114.matlib.utils.entity.entityRecords.EntityRecord;
import me.matl114.matlib.utils.entity.groups.EntityGroup;
import org.bukkit.entity.Entity;

public abstract class ListedEntityGroup<T extends Entity> implements EntityGroup<T> {
    public ListedEntityGroup(Function<T, EntityRecord<T>> recorder) {
        this.recorder = recorder;
        this.members = new PairList<>();
        this.membersUUIDMap = new HashMap<>();
    }

    final Function<T, EntityRecord<T>> recorder;
    final PairList<String, EntityRecord<T>> members;

    @Override
    public Set<UUID> getMembersUUID() {
        return membersUUIDMap.keySet();
    }

    final Map<UUID, String> membersUUIDMap;

    @Override
    public PairList<String, EntityRecord<T>> getMembers() {
        return members;
    }

    @Override
    @ForceOnMainThread
    public void addMemberSync(String childName, T entityChild) {
        putChildInternal(childName, recorder.apply(entityChild));
        postAddSyncTask(childName, entityChild);
    }

    public void addMemberAsync(String childName, T entityChild) {
        putChildInternal(childName, recorder.apply(entityChild));
        ThreadUtils.executeSync(() -> postAddSyncTask(childName, entityChild));
    }

    protected void putChildInternal(String childName, EntityRecord<T> entityChild) {
        members.put(childName, entityChild);
        membersUUIDMap.put(entityChild.getUuid(), childName);
    }

    public abstract void postAddSyncTask(String childName, T entityChild);

    @Override
    @ForceOnMainThread
    public Optional<T> removeMemberSync(String childName) {
        EntityRecord<T> child = removeChildInternal(childName);
        if (child != null) {
            Optional<T> removed = child.getOrLoadEntity();
            removed.ifPresent(childEntity -> postRemoveSyncTask(childName, childEntity));
            return removed;
        }
        return Optional.empty();
    }

    @Override
    public boolean removeMemberAsync(String childName) {
        EntityRecord<T> child = removeChildInternal(childName);
        if (child != null) {
            ThreadUtils.executeSync(() -> {
                Optional<T> removed = child.getOrLoadEntity();
                removed.ifPresent(childEntity -> postRemoveSyncTask(childName, childEntity));
            });
            return true;
        }
        return false;
    }

    protected EntityRecord<T> removeChildInternal(String childName) {
        var rc = members.removeEntry(childName);
        if (rc != null) {
            membersUUIDMap.remove(rc.getUuid());
        }
        return rc;
    }

    public abstract void postRemoveSyncTask(String childName, T entityChild);
}
