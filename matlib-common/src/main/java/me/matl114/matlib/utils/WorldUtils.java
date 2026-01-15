package me.matl114.matlib.utils;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.common.lang.annotations.ForceOnMainThread;
import me.matl114.matlib.common.lang.annotations.UnsafeOperation;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;

public class WorldUtils {

    private static final BlockState sampleBlockState = Holder.of(Material.STONE)
            .thenApply((mat) -> {
                try {
                    return mat.createBlockData().createBlockState();
                } catch (Throwable versionTooEarlyError) {
                    World sampleWorld = Bukkit.getWorlds().get(0);
                    return sampleWorld.getBlockAt(0, 0, 0).getState();
                }
            })
            .get();

    private static final Class craftBlockStateClass = Holder.of(sampleBlockState.getClass())
            .thenApply(ReflectUtils::getFieldsRecursively, "data")
            .thenApply(Pair::getB)
            .thenApply(Objects::requireNonNull)
            .get();

    //    private static final FieldAccess iBlockDataFieldAccess =
    //        new InitializeSafeProvider<>(
    //                    () -> {
    //                        var result = ReflectUtils.getFieldsRecursively(craftBlockStateClass, "data");
    //                        var IBlockDataField = result.getA();
    //                        IBlockDataField.setAccessible(true);
    //                        return FieldAccess.of(IBlockDataField);
    //                    },
    //                    FieldAccess.ofFailure())
    //            .v();
    private static final Field blockPositionFieldAccess = Holder.of(craftBlockStateClass)
            .thenApply(ReflectUtils::getFieldsRecursively, "position")
            .thenApply(Pair::getA)
            .thenApply(Objects::requireNonNull)
            .thenPeek(Field::setAccessible, true)
            .get();

    private static final Field worldFieldAccess = Holder.of(craftBlockStateClass)
            .thenApply(ReflectUtils::getFieldsRecursively, "world")
            .thenApply(Pair::getA)
            .thenApply(Objects::requireNonNull)
            .thenPeek(Field::setAccessible, true)
            .get();

    private static final Field weakWorldFieldAccess = Holder.of(craftBlockStateClass)
            .thenApply(ReflectUtils::getFieldsRecursively, "weakWorld")
            .thenApply(Pair::getA)
            .thenApply(Objects::requireNonNull)
            .thenPeek(Field::setAccessible, true)
            .get();

    private static final VarHandle positionHandle = ReflectUtils.getVarHandle(blockPositionFieldAccess);

    private static final VarHandle worldHandle = ReflectUtils.getVarHandle(worldFieldAccess);
    private static final VarHandle weakWorldHandle = ReflectUtils.getVarHandle(weakWorldFieldAccess);

    private static final VarHandle worldWorldHandle = Holder.of(
                    Bukkit.getWorlds().get(0).getClass())
            .thenApplyUnsafe(Class::getDeclaredField, "world")
            .thenApply(ReflectUtils::getVarHandle)
            .get();

    /**
     * Retrieves the NMS (Net Minecraft Server) handle from a CraftBukkit World.
     * This method uses reflection to access the underlying NMS World object.
     *
     * @param world The CraftBukkit World to get the NMS handle from
     * @return The NMS World object (net.minecraft.world.level.World)
     */
    public static Object getHandledWorld(World world) {
        return worldWorldHandle.get(world);
    }

    /**
     * Copies the block state data from one block to another block state.
     * This method uses reflection to copy position, world, and weak world references
     * from the target block's state to the provided state object.
     *
     * @param state The BlockState to copy data into
     * @param block2 The Block whose state data will be copied
     * @return The modified BlockState with copied data, or null if the operation fails
     */
    @ForceOnMainThread
    public static BlockState copyBlockState(BlockState state, Block block2) {

        BlockState state2 = WorldUtils.getBlockStateNoSnapShot(block2);
        if (craftBlockStateClass.isInstance(state2) && craftBlockStateClass.isInstance(state)) {

            try {
                positionHandle.set(state, positionHandle.get(state2));
                worldHandle.set(state, worldHandle.get(state2));
                weakWorldHandle.set(state, weakWorldHandle.get(state2));
                state.update(true, false);
                return state;
            } catch (Throwable unexpected) {
            }

            try {
                blockPositionFieldAccess.set(state, blockPositionFieldAccess.get(state2));
                worldFieldAccess.set(state, worldFieldAccess.get(state2));
                weakWorldFieldAccess.set(state, weakWorldFieldAccess.get(state2));
                state.update(true, false);
                return state;
            } catch (Throwable e) {
                return null;
            }
        } else return null;
    }

    /**
     * Gets a BlockState from a Block without creating a snapshot.
     * This method is more efficient than the default getState() method
     * as it doesn't create a snapshot of the block data.
     *
     * @param block The Block to get the state from
     * @return The BlockState of the block without snapshot
     */
    @ForceOnMainThread
    public static BlockState getBlockStateNoSnapShot(Block block) {
        return block.getState(false);
    }

    @Getter
    private static final Class<?> craftBlockEntityStateClass = Holder.of(new ItemStack(Material.CHEST))
            .thenApplyUnsafe((spawner) -> {
                ItemMeta meta = spawner.getItemMeta();
                BlockStateMeta blockMeta = (BlockStateMeta) meta;
                BlockState state = blockMeta.getBlockState();
                Class<?> type = state.getClass();
                Class<?> oldType;
                do {
                    oldType = type;
                    type = type.getSuperclass();
                } while (TileState.class.isAssignableFrom(type));
                return oldType;
            })
            .get();

    private static final Field tileEntityAccess = Holder.of(craftBlockEntityStateClass)
            .thenApplyUnsafe((cls) -> {
                try {
                    return cls.getDeclaredField("tileEntity");
                } catch (NoSuchFieldException no) {
                    return cls.getDeclaredField("blockEntity");
                }
            })
            .thenApply(Objects::requireNonNull)
            .thenPeek(Field::setAccessible, true)
            .get();

    @Getter
    private static final Class<?> tileEntityClass = tileEntityAccess.getType();

    @Getter
    private static final Field tileEntityRemovalAccess =
            Objects.requireNonNull(ReflectUtils.getFirstFitField(tileEntityClass, boolean.class, false));

    @Getter
    private static final VarHandle tileEntityHandle = ReflectUtils.getVarHandle(tileEntityAccess);

    @Getter
    private static final VarHandle tileEntityRemovalHandle = ReflectUtils.getVarHandle(tileEntityRemovalAccess);

    @Getter
    private static final Method tileEntitySetChangeAccess = Holder.of(null)
            .thenApply(v -> {
                Method[] methods = tileEntityClass.getMethods();
                var met = Arrays.stream(methods)
                        .filter(m -> m.getParameterCount() == 0)
                        .filter(m -> m.getReturnType() == void.class)
                        .filter(m -> {
                            int mod = m.getModifiers();
                            return Modifier.isPublic(mod) && !Modifier.isStatic(mod) && !Modifier.isFinal(mod);
                        })
                        .filter(m -> {
                            String name = m.getName();
                            return Set.of("e", "setChanged").contains(name);
                        })
                        .findFirst();
                return met.orElse(null);
            })
            .thenApply(Objects::requireNonNull)
            .get();

    private static final Consumer tileEntitySetChangeMethodInvoker = Holder.of(tileEntitySetChangeAccess)
            .thenApplyCaught((m) -> (Consumer<?>) LambdaUtils.createLambdaForMethod(Consumer.class, m))
            .get();

    /**
     * Checks if a TileState's underlying tile entity is still valid and not marked for removal.
     * This method uses reflection to access the tile entity's removal flag and verify its validity.
     *
     * @param tile The TileState to check for validity
     * @return true if the tile entity is valid and not marked for removal, false otherwise
     */
    public static boolean isTileEntityStillValid(@Nonnull TileState tile) {
        if (craftBlockEntityStateClass.isInstance(tile)) {
            Object tileEntity = tileEntityHandle.get(tile);
            return tileEntity != null && !((boolean) tileEntityRemovalHandle.get(tileEntity));
        } else {
            // they may get a wrong state ,so we suppose that the origin state is removed
            return false;
        }
    }

    /**
     * Marks a TileState's underlying tile entity as changed.
     * This method invokes the tile entity's setChanged method to notify the server
     * that the tile entity has been modified and needs to be saved.
     *
     * @param tile The TileState whose tile entity should be marked as changed
     */
    @UnsafeOperation
    public static void tileEntitySetChange(@Nonnull TileState tile) {
        if (craftBlockEntityStateClass.isInstance(tile)) {
            Object tileEntity = tileEntityHandle.get(tile);
            if (tileEntitySetChangeMethodInvoker != null && !(boolean) tileEntityRemovalHandle.get(tileEntity)) {
                tileEntitySetChangeMethodInvoker.accept(tileEntity);
            }
        }
    }

    private static final EnumSet<Material> TILE_ENTITIES_MATERIAL = EnumSet.noneOf(Material.class);
    private static final EnumSet<Material> INVENTORYHOLDER_MATERIAL = EnumSet.noneOf(Material.class);

    static {
        for (Material material : Material.values()) {
            if (material.isBlock()) {
                try {
                    BlockState sampleBlockState = material.createBlockData().createBlockState();
                    if (sampleBlockState instanceof TileState) {
                        TILE_ENTITIES_MATERIAL.add(material);
                    }
                    if (sampleBlockState instanceof InventoryHolder) {
                        INVENTORYHOLDER_MATERIAL.add(material);
                    }
                } catch (Throwable e) {
                }
            }
        }
    }

    /**
     * Checks if a Material represents a block that can have a tile entity.
     * This method uses a pre-computed set of materials that are known to support tile entities.
     *
     * @param material The Material to check
     * @return true if the material supports tile entities, false otherwise
     */
    public static boolean isTileEntity(Material material) {
        return TILE_ENTITIES_MATERIAL.contains(material);
    }

    /**
     * Gets an iterator over all Material types that support tile entities.
     * This provides access to the complete set of materials that can have tile entities.
     *
     * @return An Iterator containing all Material types that support tile entities
     */
    public static Iterator<Material> getTileEntityTypes() {
        return TILE_ENTITIES_MATERIAL.iterator();
    }

    /**
     * Checks if a Material represents a block that implements InventoryHolder.
     * This method uses a pre-computed set of materials that are known to be inventory holders.
     *
     * @param material The Material to check
     * @return true if the material is an inventory holder, false otherwise
     */
    public static boolean isInventoryHolder(Material material) {
        return INVENTORYHOLDER_MATERIAL.contains(material);
    }

    /**
     * Gets an iterator over all Material types that implement InventoryHolder.
     * This provides access to the complete set of materials that can hold inventories.
     *
     * @return An Iterator containing all Material types that implement InventoryHolder
     */
    public static Iterator<Material> getInventoryHolderTypes() {
        return INVENTORYHOLDER_MATERIAL.iterator();
    }
}
