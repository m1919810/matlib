package me.matl114.matlib.utils.entity.groups;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.PairList;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.utils.entity.entityRecords.EntityRecord;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;

public interface EntityGroup<T extends Entity> {
    /**
     * basic information
     */
    public String getGroupIdentifier();

    public PairList<String, EntityRecord<T>> getMembers();

    default List<EntityRecord<T>> getMemberRecords() {
        return getMembers().valueList();
    }

    default int size() {
        return getMembers().size();
    }

    default List<String> getMemberNames() {
        return getMembers().keyList();
    }

    public Set<UUID> getMembersUUID();
    /**
     * load entity requires on mainThread
     * @param childName
     * @param entityChild
     */
    @ForceOnMainThread
    public void addMemberSync(String childName, T entityChild);

    /**
     * add member if absent, creating entity always requires on main thread, so supplier should be on main
     * @param childName
     * @param supplier
     */
    default void addMemberIfAbsent(String childName, Supplier<T> supplier) {
        if (getMembers().stream().noneMatch(i -> childName.equals(i.getA()))) {
            addMemberSync(childName, supplier.get());
        }
    }
    /**
     * run Sync part with Scheduled Task
     * @param childName
     * @param entityChild
     */
    public void addMemberAsync(String childName, T entityChild);

    /**
     * load entity requires on mainThread
     * @param childName
     * @return
     */
    @ForceOnMainThread
    public Optional<T> removeMemberSync(String childName);
    /**
     * run Sync part with Scheduled Task
     * @param childName
     */
    public boolean removeMemberAsync(String childName);

    /**
     * register to GroupManager
     * @param manager
     * @return
     */
    default EntityGroup<T> register(EntityGroupManager manager) {
        manager.addGroup(getGroupIdentifier(), this);
        return this;
    }

    default void unregister() {}

    /**
     * tells GroupManager whether to automatically kill group members when shutting down
     * if return true, will invoke killGroup when manager shutdown
     * @return
     */
    public boolean autoRemovalOnShutdown();

    /**
     * kill while load, killEntity requires on mainThread
     */
    @ForceOnMainThread
    default void killGroup() {
        applyToAllMemberRecords(EntityRecord::killEntity);
    }

    /**
     * let group members do action together
     * @param action
     */
    @ForceOnMainThread
    default void applyToAllMembers(Consumer<T> action) {
        getMembers().forEach(it -> it.getB().getOrLoadEntity().ifPresent(action));
    }

    @ForceOnMainThread
    default void applyToAllMembers(BiConsumer<String, T> action) {
        getMembers().forEach(it -> it.getB().getOrLoadEntity().ifPresent(i -> action.accept(it.getA(), i)));
    }

    default void applyToAllMemberRecords(Consumer<EntityRecord<T>> action) {
        getMembers().forEach(it -> {
            action.accept(it.getB());
        });
    }

    default void applyToAllMemberRecords(BiConsumer<String, EntityRecord<T>> action) {
        getMembers().forEach(it -> {
            action.accept(it.getA(), it.getB());
        });
    }

    default void applyToAllMembersUnsafe(Consumer<T> action) {
        boolean isPrimThread = Bukkit.isPrimaryThread();
        getMembers().forEach(it -> {
            if (isPrimThread && !it.getB().stillValid()) {
                it.getB().loadEntity();
            }
            action.accept(it.getB().getEntityView());
        });
    }

    default void applyToAllMembersUnsafe(BiConsumer<String, T> action) {
        boolean isPrimThread = Bukkit.isPrimaryThread();
        getMembers().forEach(it -> {
            if (isPrimThread && !it.getB().stillValid()) {
                it.getB().loadEntity();
            }
            action.accept(it.getA(), it.getB().getEntityView());
        });
    }

    public static interface EntityGroupBuilder<T extends Entity> {
        /**
         * must be called after startLoad and before build
         * @param childName
         * @param childSupplier
         * @return
         */
        public EntityGroupBuilder<T> addChild(String childName, Supplier<T> childSupplier);

        /**
         * call finally
         * @param manager
         * @return
         */
        public EntityGroup<T> build(@Nullable EntityGroupManager<EntityGroup<T>> manager);

        /**
         * called before anything
         * @param manager
         * @return
         */
        public EntityGroupBuilder<T> startLoad(EntityGroupManager<EntityGroup<T>> manager);
    }

    public static <W extends Entity> EntityGroupBuilder<W> builder(Supplier<EntityGroup<W>> getter) {
        return new EntityGroupBuilder<W>() {
            EntityGroup<W> handle;

            @Override
            public EntityGroupBuilder<W> addChild(String childName, Supplier<W> childSupplier) {
                handle.addMemberSync(childName, childSupplier.get());
                return this;
            }

            @Override
            public EntityGroup<W> build(@Nullable EntityGroupManager<EntityGroup<W>> manager) {
                return handle;
            }

            @Override
            public EntityGroupBuilder<W> startLoad(EntityGroupManager<EntityGroup<W>> manager) {
                this.handle = getter.get();
                return this;
            }
        };
    }

    public static interface EntityGroupLoader<T extends Entity> extends EntityGroupBuilder<T> {
        /**
         * if data loss ,create new Child
         * @param childName
         * @return
         */
        default EntityGroupBuilder<T> addChild(String childName, Supplier<T> childSupplier) {
            addChildIfAbsent(childName, childSupplier);
            return this;
        }

        public EntityGroupBuilder<T> addChildIfAbsent(String childName, Supplier<T> provider);

        public EntityGroupBuilder<T> startLoad(EntityGroupManager<EntityGroup<T>> manager);
    }
}
