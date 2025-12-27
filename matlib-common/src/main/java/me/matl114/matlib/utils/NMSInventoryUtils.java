package me.matl114.matlib.utils;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.FuncUtils;
import me.matl114.matlib.algorithms.dataStructures.frames.initBuidler.InitializeSafeProvider;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.common.functions.reflect.MethodInvoker;
import me.matl114.matlib.common.lang.annotations.NotRecommended;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.common.lang.annotations.UnsafeOperation;
import me.matl114.matlib.utils.inventory.inventoryRecords.InventoryRecord;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.reflect.wrapper.MethodAccess;
import org.bukkit.Bukkit;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class NMSInventoryUtils {
    static {
        Debug.logger("Initializing NMSInventoryUtils...");
    }

    @Getter
    @Note("class CraftInventory")
    private static final Class<?> craftInventoryClass = new InitializeSafeProvider<>(() -> {
                Inventory inv = Bukkit.createInventory(
                        new InventoryHolder() {
                            Inventory thisInv;

                            @Override
                            public Inventory getInventory() {
                                return thisInv;
                            }
                        },
                        54);
                Class<?> craftCustomInv = inv.getClass();
                Class<?> superClass = craftCustomInv.getSuperclass();
                while (superClass != Object.class && superClass != null) {
                    craftCustomInv = superClass;
                    superClass = craftCustomInv.getSuperclass();
                }
                ;
                Debug.debug(craftCustomInv);
                return craftCustomInv;
            })
            .v();

    @Getter
    private static final MethodAccess<?> getIInventoryAccess = MethodAccess.ofName(
                    craftInventoryClass, "getInventory", new Class[0])
            .initWithNull();

    @Getter
    @Note("public IInventory getInventory()")
    private static final MethodInvoker<?> getIInventoryInvoker = Holder.of(getIInventoryAccess)
            .thenApply(MethodAccess::getMethodOrDefault, FuncUtils.nullTyped(Method.class))
            .thenApplyCaught((m) -> (Function<Inventory, ?>) LambdaUtils.createLambdaForMethod(Function.class, m))
            .thenApply(MethodInvoker::ofNoArgs)
            .thenPeek((e) -> Debug.logger("Successfully initialize CraftInventory.getInventory Method Invoker"))
            .get();

    @Getter
    @Note("interface IInventory")
    private static final Class<?> iInventoryClass = Holder.of(getIInventoryAccess)
            .thenApply(MethodAccess::getMethodOrDefault, FuncUtils.nullTyped(Method.class))
            .thenApply(Method::getReturnType)
            .get();

    @Getter
    private static final MethodAccess<List<?>> getIIContentsAccess = MethodAccess.ofName(
                    iInventoryClass, "getContents", new Class[0])
            .initWithNull()
            .cast();

    @Getter
    @Note("public List<net.minecraft.ItemStack> getContents()")
    private static final MethodInvoker<List<?>> getIIContentsInvoker = Holder.of(getIIContentsAccess)
            .thenApply(MethodAccess::getMethodOrDefault, FuncUtils.nullTyped(Method.class))
            .thenApplyCaught((m) -> (Function<?, List<?>>) LambdaUtils.createLambdaForMethod(Function.class, m))
            .thenApply(MethodInvoker::ofNoArgs)
            .get();

    @UnsafeOperation
    @Note("record must be a vanilla Inv")
    /**
     * Sets an item in a tile entity inventory without triggering update events.
     * This method uses NMS reflection to directly modify the inventory contents
     * without calling Bukkit's setItem method, which would trigger inventory events.
     * Supports both single block inventories and double chest inventories.
     *
     * @param record The InventoryRecord containing the inventory to modify
     * @param index The slot index where the item should be placed
     * @param item The ItemStack to place in the slot, or null to clear the slot
     */
    public static void setTileInvItemNoUpdate(InventoryRecord record, int index, ItemStack item) {
        Inventory bukkitInventory = record.inventory();
        if (record.isMultiBlockInv()) {
            DoubleChestInventory first = (DoubleChestInventory) bukkitInventory;
            Inventory left = first.getLeftSide();
            int size = left.getSize();
            if (index < size) {
                setInvInternal(left, index, item);
            } else {
                setInvInternal(first.getRightSide(), index - size, item);
            }
        } else {
            setInvInternal(bukkitInventory, index, item);
        }
    }

    /**
     * Sets an item in a tile entity inventory without triggering update events.
     * This method uses NMS reflection to directly modify the inventory contents.
     * The inventory must be a block inventory (holder instanceof TileState or DoubleChest).
     *
     * @param inventory The inventory to modify
     * @param index The slot index where the item should be placed
     * @param item The ItemStack to place in the slot, or null to clear the slot
     */
    @UnsafeOperation
    @NotRecommended
    @Note("inventory must be a block inventory, s.t. holder instanceof TileState or DoubleChest")
    public static void setTileInvItemNoUpdate(Inventory inventory, int index, ItemStack item) {
        if (inventory instanceof DoubleChestInventory first) {
            Inventory left = first.getLeftSide();
            int size = left.getSize();
            if (index < size) {
                setInvInternal(left, index, item);
            } else {
                setInvInternal(first.getRightSide(), index - size, item);
            }
        } else {
            setInvInternal(inventory, index, item);
        }
    }

    /**
     * Sets an item in a custom inventory without triggering update events.
     * This method uses NMS reflection to directly modify the inventory contents.
     * The inventory must be a CraftInventoryCustom created through Bukkit.createInventory().
     *
     * @param inventory The custom inventory to modify
     * @param index The slot index where the item should be placed
     * @param item The ItemStack to place in the slot, or null to clear the slot
     */
    @UnsafeOperation
    @NotRecommended
    @Note("inventory must be a CraftInventoryCustom, s.t. it is created through Bukkit.createInventory()")
    public static void setInvItem(Inventory inventory, int index, ItemStack item) {
        setInvInternal(inventory, index, item);
    }

    /**
     * Sets an item in a custom inventory without copying the ItemStack.
     * This method uses NMS reflection to directly modify the inventory contents
     * and avoids creating a copy of the ItemStack for better performance.
     * The inventory must be a CraftInventoryCustom created through Bukkit.createInventory().
     *
     * @param inventory The custom inventory to modify
     * @param index The slot index where the item should be placed
     * @param item The ItemStack to place in the slot (must be a CraftItemStack)
     */
    @UnsafeOperation
    @NotRecommended
    @Note("inventory must be a CraftInventoryCustom, s.t. it is created through Bukkit.createInventory()")
    public static void setInvItemNoCopy(Inventory inventory, int index, ItemStack item) {
        if (CraftUtils.isCraftItemStack(item)) {
            setInvNoCopy(inventory, index, item);
        } else {
            setInvInternal(inventory, index, null);
        }
    }

    private static void setInvInternal(Inventory inventory, int index, @Nullable ItemStack item) {
        try {
            Object iInventory = getIInventoryInvoker.invokeNoArg(inventory);
            List itemContents = getIIContentsInvoker.invokeNoArg(iInventory);
            itemContents.set(index, CraftUtils.getNMSCopy(item));
        } catch (ArrayIndexOutOfBoundsException SHIT) {
            throw SHIT;
        } catch (Throwable e) {
            Debug.logger(e, "Error while doing nms Inventory modification");
        }
    }

    private static void setInvNoCopy(Inventory inventory, int index, @Nonnull ItemStack item) {
        try {
            Object iInventory = getIInventoryInvoker.invokeNoArg(inventory);
            List itemContents = getIIContentsInvoker.invokeNoArg(iInventory);
            itemContents.set(index, CraftUtils.getHandled(item));
        } catch (ArrayIndexOutOfBoundsException SHIT) {
            throw SHIT;
        } catch (Throwable e) {
            if (CraftUtils.isCraftItemStack(item)) {
                throw new RuntimeException(
                        "Invalid Argument passed, can not access nms handle from " + (item.getClass()));
            }
            Debug.logger(e, "Error while doing nms Inventory modification");
        }
    }

    /**
     * Sets multiple items in a tile entity inventory without triggering update events.
     * This method uses NMS reflection to directly modify the inventory contents
     * without calling Bukkit's setItem method, which would trigger inventory events.
     * Supports both single block inventories and double chest inventories.
     * The contents array length must not exceed the inventory size.
     *
     * @param inventory The InventoryRecord containing the inventory to modify
     * @param contents The ItemStack array containing items to place in the inventory
     * @throws IllegalArgumentException if the contents array length exceeds the inventory size
     */
    @UnsafeOperation
    public static void setTileInvContentsNoUpdate(InventoryRecord inventory, ItemStack... contents) {
        Inventory bukkitInventory = inventory.inventory();
        int size = bukkitInventory.getSize();
        Preconditions.checkArgument(
                contents.length <= size, "Invalid inventory size (%s); expected %s or less", contents.length, size);
        if (inventory.isMultiBlockInv()) {
            DoubleChestInventory first = (DoubleChestInventory) bukkitInventory;
            Inventory left = first.getLeftSide();
            int sizeA = left.getSize();
            if (contents.length < sizeA) {
                setInvContentInternal(left, contents, 0, contents.length);
            } else {
                setInvContentInternal(left, contents, 0, sizeA);
                setInvContentInternal(first.getRightSide(), contents, sizeA, contents.length);
            }
        } else {
            setInvContentInternal(bukkitInventory, contents, 0, contents.length);
        }
    }

    private static void setInvContentInternal(Inventory inventory, ItemStack[] item, int start, int end) {
        try {
            Object iInventory = getIInventoryInvoker.invokeNoArg(inventory);
            List itemContents = getIIContentsInvoker.invokeNoArg(iInventory);
            for (int i = start; i < end; i++) {
                itemContents.set(i - start, CraftUtils.getNMSCopy(item[i]));
            }
        } catch (ArrayIndexOutOfBoundsException SHIT) {
            throw SHIT;
        } catch (Throwable e) {
            Debug.logger(e, "Error while doing no-update blockInventory modification:");
        }
    }

    //
    //    @Note("InventoryLargeChest.class")
    //    private static Class<?> doubleChestInvClass;
    //
    //
    //    @Note("public final IInventory container1")
    //    private static FieldAccess doubleChestInv1;
    //
    //
    //    @Note("public final IInventory container2")
    //    private static FieldAccess doubleChestInv2;
    //
    //    public void initDoubleChest(DoubleChestInventory inv){
    //
    //    }

}
