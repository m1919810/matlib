package me.matl114.matlib.utils;

import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.FuncUtils;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.reflect.wrapper.*;
import me.matl114.matlib.utils.stackCache.ItemStackMetaCache;
import me.matl114.matlib.utils.version.VersionedMeta;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

public class CraftUtils {
    public static final ItemStack DEFAULT_ITEMSTACK = new ItemStack(Material.STONE);

    static {
        Debug.logger("Initializing CraftUtils...");
    }

    public static final ItemMeta NULL_META = (DEFAULT_ITEMSTACK.getItemMeta());
    public static final Class craftMetaItemClass = NULL_META.getClass();
    private static final ThreadLocal<Inventory> threadLocalInventory =
            ThreadLocal.withInitial(() -> Bukkit.createInventory(
                    new InventoryHolder() {
                        Inventory inv;

                        @Override
                        public Inventory getInventory() {
                            return inv;
                        }
                    },
                    InventoryType.CHEST));
    private static final ItemStack craftItemStack = Holder.of(threadLocalInventory)
            .thenApply(ThreadLocal::get)
            .thenPeek(inv -> inv.setItem(0, DEFAULT_ITEMSTACK))
            .thenApply(Inventory::getItem, 0)
            .get();

    @Getter
    private static final Class craftItemStackClass = Holder.of(craftItemStack)
            .thenApply(FuncUtils.ifPresent(Object::getClass))
            .get();

    @Getter
    private static final FieldAccess loreAccess = Holder.of(craftMetaItemClass)
            .thenApplyCaught(Class::getDeclaredField, "lore")
            .failHard()
            .thenPeek(Field::setAccessible, true)
            .thenApply(FieldAccess::of)
            .peekFail((e) -> Debug.logger(e, "Meta reflection failed,please check the error:"))
            .ifFail((e) -> FieldAccess.ofFailure())
            .get();

    @Getter
    private static final FieldAccess displayNameAccess = Holder.of(craftMetaItemClass)
            .thenApplyCaught(Class::getDeclaredField, "displayName")
            .failHard()
            .thenPeek(Field::setAccessible, true)
            .thenApply(FieldAccess::of)
            .peekFail((e) -> Debug.logger(e, "ItemMeta reflection failed,please check the error"))
            .ifFail((e) -> FieldAccess.ofFailure())
            .get();

    @Getter
    private static final FieldAccess enchantmentAccess = Holder.of(craftMetaItemClass)
            .thenApplyCaught(Class::getDeclaredField, "enchantments")
            .failHard()
            .thenPeek(Field::setAccessible, true)
            .thenApply(FieldAccess::of)
            .peekFail((e) -> Debug.logger(e, "ItemMeta reflection failed,please check the error"))
            .ifFail((e) -> FieldAccess.ofFailure())
            .get();

    @Getter
    private static final FieldAccess handledAccess = Holder.of(craftItemStackClass)
            .thenApplyCaught(Class::getDeclaredField, "handle")
            .failHard()
            .thenPeek(Field::setAccessible, true)
            .thenApply(FieldAccess::of)
            .peekFail((e) -> Debug.logger(e, "ItemStack reflection failed,please check the error:"))
            .ifFail((e) -> FieldAccess.ofFailure())
            .get();

    private static final Class NMSItemStackClass = Holder.of(handledAccess)
            .thenApplyCaught(FieldAccess::getValue, craftItemStack)
            .whenComplete((o, e) -> {
                if (e == null) {
                    return o.getClass();
                } else {
                    return null;
                }
            })
            .get();

    @Getter
    private static final VarHandle loreHandle = Holder.of(loreAccess)
            .thenApply(FieldAccess::getVarHandleOrDefault, FuncUtils.nullSupplier())
            .thenPeek((e) -> Debug.logger("Successfully initialize CraftMetaItem.lore VarHandle"))
            .get();

    @Getter
    private static final VarHandle displayNameHandle = Holder.of(displayNameAccess)
            .thenApply(FieldAccess::getVarHandleOrDefault, FuncUtils.nullSupplier())
            .thenPeek((e) -> Debug.logger("Successfully initialize CraftMetaItem.displayName VarHandle"))
            .get();

    @Getter
    private static final VarHandle enchantmentsHandle = Holder.of(enchantmentAccess)
            .thenApply(FieldAccess::getVarHandleOrDefault, FuncUtils.nullSupplier())
            .thenPeek((e) -> Debug.logger("Successfully initialize CraftMetaItem.enchantments VarHandle"))
            .get();

    @Note("net.minecraft.ItemStack handle;")
    @Nonnull
    private static final VarHandle handleHandle = Holder.of(handledAccess)
            .thenApply(FieldAccess::getVarHandleOrDefault, FuncUtils.nullSupplier())
            .thenPeek((e) -> Debug.logger("Successfully initialize CraftItemStack.handle VarHandle"))
            .thenApply(Objects::requireNonNull)
            .get();

    //    @Note("net.minecraft.ItemStack handle may be public")
    //    private static final FieldAccessor<?> handleAccessor = new FieldAccessor() {
    //        @Override
    //        public void set(Object obj, Object value) {
    //            CraftUtils.handleHandle.set(obj, value);
    //        }
    //
    //        @Override
    //        public Object get(Object obj) {
    //            return CraftUtils.handleHandle.get(obj);
    //        }
    //    };

    @Getter
    private static final MethodAccess<ItemStack> asCraftCopyAccess =
            MethodAccess.ofName(craftItemStackClass, "asCraftCopy", ItemStack.class);

    @Getter
    @Note("public static CraftItemStack asCraftCopy")
    private static final Function<ItemStack, ItemStack> asCraftCopyLambda = Holder.of(
                    asCraftCopyAccess.getMethodOrDefault(() -> null))
            .thenApplyUnsafe((m) -> {
                return (Function<ItemStack, ItemStack>) LambdaUtils.createLambdaForStaticMethod(Function.class, m);
            })
            .get();

    private static final MethodAccess<?> asNMSCopyAccess =
            MethodAccess.ofName(craftItemStackClass, "asNMSCopy", ItemStack.class);

    @Getter
    @Note("public static net.minecraft.world.item.ItemStack asNMSCopy")
    private static final Function<ItemStack, ?> asNMSCopyLambda = Holder.of(
                    asNMSCopyAccess.getMethodOrDefault(() -> null))
            .thenApplyUnsafe((m) -> {
                return (Function<ItemStack, ?>) LambdaUtils.createLambdaForStaticMethod(Function.class, m);
            })
            .get();

    ;

    static {
        Debug.logger("Successfully initialize CraftItemStack static methods...");
        Debug.logger("Successfully initialize CraftUtils...");
    }
    /**
     * Retrieves the NMS (Net Minecraft Server) handle from a CraftBukkit ItemStack.
     * This method uses reflection to access the underlying NMS ItemStack object.
     *
     * @param stack The CraftBukkit ItemStack to get the NMS handle from
     * @return The NMS ItemStack object (net.minecraft.world.item.ItemStack)
     * @throws RuntimeException if the provided stack is not a CraftItemStack instance
     */
    public static Object getHandled(@Nonnull ItemStack stack) {
        return handleHandle.get(stack);
        //        if (craftItemStackClass.isInstance(stack)) {
        //
        //        } else {
        //            throw new RuntimeException(
        //                    "Invalid argument passed! " + stack.getClass() + " does not extend from CraftItemStack");
        //        }
    }

    /**
     * Creates a CraftBukkit copy of the given ItemStack using the asCraftCopy method.
     * This method ensures the returned ItemStack is a CraftItemStack instance.
     *
     * @param item The ItemStack to create a CraftBukkit copy from
     * @return A CraftItemStack copy of the provided ItemStack
     */
    public static ItemStack getCraftCopy(@Nullable ItemStack item) {
        return item == null ? null : asCraftCopyLambda.apply(item);
    }

    /**
     * Creates a CraftBukkit copy of the given ItemStack using the asCraftCopy method.
     * The throughInventorySafe parameter is currently ignored but may be used for future safety checks.
     *
     * @param item The ItemStack to create a CraftBukkit copy from
     * @param throughInventorySafe Whether to perform additional safety checks (currently ignored)
     * @return A CraftItemStack copy of the provided ItemStack
     */
    @Deprecated(forRemoval = true)
    public static ItemStack getCraftCopy(@Nullable ItemStack item, boolean throughInventorySafe) {
        return item == null ? null : asCraftCopyLambda.apply(item);
    }

    /**
     * Creates an NMS (Net Minecraft Server) copy of the given ItemStack using the asNMSCopy method.
     * This method converts a CraftBukkit ItemStack to its NMS equivalent.
     *
     * @param item The ItemStack to create an NMS copy from
     * @return The NMS ItemStack object (net.minecraft.world.item.ItemStack)
     */
    public static Object getNMSCopy(@Nullable ItemStack item) {
        return asNMSCopyLambda.apply(item);
    }

    /**
     * Checks if the given ItemStack is a CraftItemStack instance.
     *
     * @param item The ItemStack to check
     * @return true if the ItemStack is a CraftItemStack, false otherwise
     */
    public static boolean isCraftItemStack(@Nullable ItemStack item) {
        return craftItemStackClass.isInstance(item);
    }

    /**
     * Checks if the given object is an NMS ItemStack instance.
     *
     * @param nms The object to check
     * @return true if the object is an NMS ItemStack, false otherwise
     */
    public static boolean isNMSItemStack(@Nonnull Object nms) {
        return NMSItemStackClass.isInstance(nms);
    }

    /**
     * Compares two CraftItemStack instances to check if they have the same NMS handle.
     * If both items are CraftItemStack instances, this method checks if their underlying
     * NMS ItemStack objects are the same reference.
     *
     * @param a The first ItemStack to compare
     * @param b The second ItemStack to compare
     * @return true if both items are CraftItemStack instances with the same NMS handle, false otherwise
     */
    public static boolean sameCraftItem(ItemStack a, ItemStack b) {
        if (craftItemStackClass.isInstance(a) && craftItemStackClass.isInstance(b)) {
            try {
                return handleHandle.get(a) == handleHandle.get(b);
            } catch (Throwable e) {
                return false;
            }
        } else return false;
    }

    /**
     * Creates an ItemStackCache for the given ItemStack for efficient comparison and manipulation.
     * The cache stores the ItemStack and its metadata for optimized operations.
     *
     * @param a The ItemStack to create a cache for
     * @return An ItemStackCache containing the ItemStack and its metadata, or null if the input is null
     */
    public static ItemStackMetaCache getStackCache(ItemStack a) {
        if (a == null) return null;
        // 用于比较和
        return ItemStackMetaCache.get(a);
    }

    /**
     * Compares the amounts of two ItemStacks to check if the first has a larger or equal amount.
     * If either ItemStack is null, they are considered equal only if both are null.
     *
     * @param thisItem The first ItemStack to compare
     * @param thatItem The second ItemStack to compare
     * @return true if thisItem has greater or equal amount compared to thatItem, false otherwise
     */
    public static boolean amountLargerThan(ItemStack thisItem, ItemStack thatItem) {
        if (thisItem == null || thatItem == null) {
            return thisItem == thatItem;
        } else {
            return thisItem.getAmount() >= thatItem.getAmount();
        }
    }

    /**
     * Consumes the amount from the first ItemStack from the second ItemStack.
     * This method reduces the amount of thatItem by the amount of thisItem.
     *
     * @param thisItem The ItemStack whose amount will be consumed (source)
     * @param thatItem The ItemStack whose amount will be reduced (target)
     */
    public static void consumeThat(ItemStack thisItem, ItemStack thatItem) {
        if (thisItem != null && thatItem != null) {
            thatItem.setAmount(thatItem.getAmount() - thisItem.getAmount());
        }
    }

    /**
     * Consumes a specific amount from the given ItemStack.
     * This method reduces the amount of the ItemStack by the specified amount.
     *
     * @param amount The amount to consume from the ItemStack
     * @param thatItem The ItemStack whose amount will be reduced
     */
    public static void consumeThat(int amount, ItemStack thatItem) {
        if (thatItem != null) {
            thatItem.setAmount(thatItem.getAmount() - amount);
        }
    }

    /**
     * Compares two ItemMeta objects to determine if they match.
     * This method performs comprehensive metadata comparison including display name,
     * custom model data, special metadata types, persistent data container,
     * custom item IDs, lore, enchantments, and attribute modifiers.
     *
     * @param meta1 The first ItemMeta to compare
     * @param meta2 The second ItemMeta to compare
     * @param strictCheck Whether to perform strict checking (includes enchantments, attributes, etc.)
     * @return true if the ItemMeta objects match, false otherwise
     */
    public static boolean matchItemMeta(@Nonnull ItemMeta meta1, @Nonnull ItemMeta meta2, boolean strictCheck) {

        // match display name
        if (!matchDisplayNameField(meta1, meta2)) {
            return false;
        }
        // custommodeldata
        final boolean hasCustomOne = meta1.hasCustomModelData();
        final boolean hasCustomTwo = meta2.hasCustomModelData();
        if (hasCustomOne) {
            if (!hasCustomTwo || meta1.getCustomModelData() != meta2.getCustomModelData()) {
                return false;
            }
        } else if (hasCustomTwo) {
            return false;
        }

        // check special metas
        if (canQuickEscapeMetaVariant(meta1, meta2)) {
            return false;
        }

        if (VersionedMeta.getInstance().differentSpecialMeta(meta1, meta2)) {
            return false;
        }
        // check pdc
        if (!meta1.getPersistentDataContainer().equals(meta2.getPersistentDataContainer())) {
            return false;
        }

        if (!matchLoreField(meta1, meta2)) {
            return false;
            // 对于普通物品 检查完lore就结束是正常的
        } else if (!strictCheck) {
            return true;
        }
        // Strict check: Make sure enchantments match
        if (!matchEnchantmentsFields(meta1, meta2)) {
            return false;
        }

        final boolean hasAttributeOne = meta1.hasAttributeModifiers();
        final boolean hasAttributeTwo = meta2.hasAttributeModifiers();
        if (hasAttributeOne) {
            if (!hasAttributeTwo || !Objects.equals(meta1.getAttributeModifiers(), meta2.getAttributeModifiers())) {
                return false;
            }
        } else if (hasAttributeTwo) {
            return false;
        }
        return true;
    }

    /**
     * Compares the lore fields of two ItemMeta objects.
     * This method uses optimized field access via VarHandle when available,
     * falling back to reflection-based field comparison.
     *
     * @param meta1 The first ItemMeta to compare lore from
     * @param meta2 The second ItemMeta to compare lore from
     * @return true if the lore fields match, false otherwise
     */
    public static boolean matchLoreField(@Nonnull ItemMeta meta1, @Nonnull ItemMeta meta2) {
        if (loreHandle != null) {
            return Objects.equals(loreHandle.get(meta1), loreHandle.get(meta2));
        } else {
            return loreAccess.compareFieldOrDefault(
                    meta1, meta2, () -> Objects.equals(meta1.getLore(), meta2.getLore()));
        }
    }

    /**
     * Compares the display name fields of two ItemMeta objects.
     * This method uses optimized field access via VarHandle when available,
     * falling back to reflection-based field comparison.
     *
     * @param meta1 The first ItemMeta to compare display name from
     * @param meta2 The second ItemMeta to compare display name from
     * @return true if the display name fields match, false otherwise
     */
    public static boolean matchDisplayNameField(ItemMeta meta1, ItemMeta meta2) {
        if (displayNameHandle != null) {
            return Objects.equals(displayNameHandle.get(meta1), displayNameHandle.get(meta2));
        } else {
            return displayNameAccess.compareFieldOrDefault(
                    meta1, meta2, () -> Objects.equals(meta1.getDisplayName(), meta2.getDisplayName()));
        }

        //        try{
        //            Object name1=(CRAFTDISPLAYNAME.get(meta1));
        //            Object name2=(CRAFTDISPLAYNAME.get(meta2));
        //            return name1.equals(name2);
        //        }catch (Throwable e){
        //            return meta1.getDisplayName().equals(meta2.getDisplayName());
        //        }
    }

    /**
     * Compares the enchantments fields of two ItemMeta objects.
     * This method checks if both ItemMeta objects have enchantments and compares them.
     * Uses optimized field access via VarHandle when available, falling back to reflection.
     *
     * @param meta1 The first ItemMeta to compare enchantments from
     * @param meta2 The second ItemMeta to compare enchantments from
     * @return true if the enchantments fields match, false otherwise
     */
    public static boolean matchEnchantmentsFields(ItemMeta meta1, ItemMeta meta2) {
        if (enchantmentsHandle != null) {
            return meta1.hasEnchants()
                    ? meta2.hasEnchants()
                            && Objects.equals(enchantmentsHandle.get(meta1), enchantmentsHandle.get(meta2))
                    : !meta2.hasEnchants();
        } else {
            return meta1.hasEnchants()
                    ? meta2.hasEnchants()
                            && displayNameAccess.compareFieldOrDefault(
                                    meta1, meta2, () -> Objects.equals(meta1.getEnchants(), meta2.getEnchants()))
                    : !meta2.hasEnchants();
        }
    }
    //    private static Class CraftMetaBlockState;
    //    private static Field blockEntityTag;
    //    private static boolean hasFailed;

    /**
     * Compares BlockStateMeta objects to determine if they match.
     * This method delegates to version-specific metadata comparison logic.
     *
     * @param meta1 The first BlockStateMeta to compare
     * @param meta2 The second BlockStateMeta to compare
     * @return true if the BlockStateMeta objects match, false otherwise
     */
    public static boolean matchBlockStateMetaField(BlockStateMeta meta1, BlockStateMeta meta2) {
        return VersionedMeta.getInstance().matchBlockStateMeta(meta1, meta2);
        //        if(!hasFailed){
        //            try{
        //                if(CraftMetaBlockState==null){
        //                    var field= ReflectUtils.getFieldsRecursively(meta1.getClass(),"blockEntityTag");
        //                    CraftMetaBlockState=field.getB();
        //                    blockEntityTag=field.getA();
        //                    blockEntityTag.setAccessible(true);
        //                    Debug.debug(CraftMetaBlockState);
        //                    Debug.debug(blockEntityTag);
        //                }
        //                return Objects.equals(blockEntityTag.get(meta1),blockEntityTag.get(meta2));
        //            }catch (Throwable e){
        //                hasFailed=true;
        //            }
        //        }
        //        return meta1.equals(meta2);
    }

    /**
     * Compares two ItemStack objects to determine if they match.
     * This method handles null checks and delegates to the core matching logic.
     *
     * @param stack1 The first ItemStack to compare
     * @param stack2 The second ItemStack to compare
     * @param strictCheck Whether to perform strict checking (includes enchantments, attributes, etc.)
     * @return true if the ItemStack objects match, false otherwise
     */
    public static boolean matchItemStack(ItemStack stack1, ItemStack stack2, boolean strictCheck) {
        if (stack1 == null || stack2 == null) {
            return stack1 == stack2;
        } else {
            Material material1 = stack1.getType();
            Material material2 = stack2.getType();
            if (material1 != material2) return false;
            boolean h1 = stack1.hasItemMeta();
            boolean h2 = stack2.hasItemMeta();
            if (h1 && h2) {
                return matchItemMeta(stack1.getItemMeta(), stack2.getItemMeta(), strictCheck);
            } else return h1 == h2;
            // return matchItemCore(getStackCache(stack1), getStackCache(stack2), strictCheck);
        }
    }

    /**
     * Compares an ItemStack with an ItemStackCache to determine if they match.
     * This method handles null checks and delegates to the core matching logic.
     *
     * @param counter1 The ItemStack to compare
     * @param counter2 The ItemStackCache to compare against
     * @param strictCheck Whether to perform strict checking (includes enchantments, attributes, etc.)
     * @return true if the ItemStack and ItemStackCache match, false otherwise
     */
    public static boolean matchItemStack(ItemStack counter1, ItemStackMetaCache counter2, boolean strictCheck) {
        if (counter1 == null) {
            return counter2.getItem() == null;
        } else {

            Material material1 = counter1.getType();
            Material material2 = counter2.getType();
            if (material1 != material2) return false;
            boolean h1 = counter1.hasItemMeta();
            boolean h2 = counter2.getMeta() != null;
            if (h1 && h2) {
                return matchItemMeta(counter1.getItemMeta(), counter2.getMeta(), strictCheck);
            } else return h1 == h2;
        }
    }

    /**
     * Performs quick escape checks for specific metadata variants that can be quickly determined
     * to not match. This method checks for differences in damage, repair cost, and block data
     * that would immediately indicate non-matching items.
     *
     * @param metaOne The first ItemMeta to compare
     * @param metaTwo The second ItemMeta to compare
     * @return true if the metadata variants can be quickly determined to not match, false otherwise
     */
    public static boolean canQuickEscapeMetaVariant(@Nonnull ItemMeta metaOne, @Nonnull ItemMeta metaTwo) {

        if (metaOne instanceof Damageable instanceOne && metaTwo instanceof Damageable instanceTwo) {
            if (instanceOne.hasDamage() != instanceTwo.hasDamage()) {
                return true;
            }

            if (instanceOne.getDamage() != instanceTwo.getDamage()) {
                return true;
            }
        }
        if (metaOne instanceof Repairable instanceOne && metaTwo instanceof Repairable instanceTwo) {
            if (instanceOne.hasRepairCost() != instanceTwo.hasRepairCost()) {
                return true;
            }

            if (instanceOne.getRepairCost() != instanceTwo.getRepairCost()) {
                return true;
            }
        }
        if (metaOne instanceof BlockDataMeta instanceOne) {
            if (metaTwo instanceof BlockDataMeta instanceTwo) {
                if (instanceOne.hasBlockData() != instanceTwo.hasBlockData()) {
                    return true;
                }
            } else return true;
        }
        return false;
    }
}
