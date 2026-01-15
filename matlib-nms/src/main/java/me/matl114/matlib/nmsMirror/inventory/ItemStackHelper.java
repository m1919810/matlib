package me.matl114.matlib.nmsMirror.inventory;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.List;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.ValueAccess;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.interfaces.CustomNbtHolder;
import me.matl114.matlib.nmsMirror.interfaces.PdcCompoundHolder;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.CastCheck;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import org.bukkit.inventory.ItemStack;

public interface ItemStackHelper extends PdcCompoundHolder, CustomNbtHolder {

    @CastCheck("net.minecraft.world.item.ItemStack")
    boolean isItemStack(Object val);

    @ConstructorTarget
    public Object newItemStack(@RedirectType(ItemLike) Object itemLike, int count);

    Object ofNbt(@RedirectType(CompoundTag) Object nbt);

    @MethodTarget
    public Object copyAndClear(Object itemStack);

    @MethodTarget
    public Object getItem(Object itemStack);

    @MethodTarget
    public int getMaxStackSize(Object itemStack);

    @MethodTarget
    boolean isEmpty(Object itemStack);

    @MethodTarget
    @Note("this is known as \"Duriability\" not attack damage")
    int getDamageValue(Object stack);

    @MethodTarget
    void setDamageValue(Object stack, int value);

    @MethodTarget
    int getMaxDamage(Object stack);

    @MethodTarget
    public void setItem(Object stack, @RedirectType(Item) Object item);

    @MethodTarget
    public int getCount(Object stack);

    @MethodTarget
    public void setCount(Object stack, int count);

    @MethodTarget
    public Object split(Object stack, int amount);

    @CastCheck(NbtCompoundClass)
    public boolean isCompoundTag(Object unknown);

    @MethodTarget
    public org.bukkit.inventory.ItemStack getBukkitStack(Object stack);

    @MethodTarget
    public ItemStack asBukkitCopy(Object stack);

    @MethodTarget(isStatic = true)
    public boolean isSameItemSameTags(
            @RedirectType(ItemStack) @Nonnull Object stack, @RedirectType(ItemStack) @Nonnull Object otherStack);

    default Object copy(Object itemStack) {
        return copy(itemStack, false);
    }

    @MethodTarget
    public Object copy(Object itemSTack, boolean originItem);

    @MethodTarget
    public Object copyWithCount(Object itemSTack, int count);

    @MethodTarget
    Iterable<?> getHoverName(Object stack);

    public boolean hasExtraData(Object stack);

    public Object save(Object itemStack, @RedirectType(CompoundTag) Object nbt);

    default Object save(Object itemStack) {
        return save(itemStack, NMSCore.COMPOUND_TAG.newComp());
    }

    Object saveNbtAsTag(Object itemStack);

    Map<String, ?> saveNbtAsMap(Object itemStack);

    Object saveElementInPath(Object itemStack, String path);

    void applyNbtFromMap(Object itemStack, Map<String, ?> val);

    void replaceElementInPath(Object itemStack, String path, Object primitive);

    boolean matchItem(
            @Nullable Object item1,
            @Nullable Object item2,
            @Note(
                            "distinct assumed that they both have lore/name, and we don't care about them, BUT if one of then don't have, then it is regarded as not match")
                    boolean distinctLore,
            boolean distinctName);

    boolean matchNbt(Object item1, Object item2, boolean distinctLore, boolean distinctName);

    boolean hasLore(Object stack);

    ListMapView<?, Iterable<?>> getLoreView(Object stack, boolean overrideOnWrite);

    void replaceLore(Object item, List<Iterable<?>> lore);

    public boolean hasCustomHoverName(Object stack);

    Object setHoverName(Object stack, @RedirectType(ChatComponent) Iterable<?> name);

    @Note("when you want to modify anything, pls copy first")
    default ValueAccess<Iterable<?>> getDisplayNameView(final Object stack) {
        return new ValueAccess<Iterable<?>>() {
            @Override
            public Iterable<?> get0() {
                return getHoverName(stack);
            }

            @Override
            public void set0(Iterable<?> val) {
                setHoverName(stack, val);
            }
        };
    }

    default boolean equalsEmpty(Object stack) {
        return stack == EmptyEnum.EMPTY_ITEMSTACK;
    }

    int customHashcode(@Nonnull Object item);

    int customHashWithoutDisplay(Object item);

    @Note(value = "Set the passed value at the pdc compound, no copy")
    void setPersistentDataCompound(Object val, Object val2);

    //    Object getPersistentDataCompound(Object val, boolean create);
    //
    //    void setPersistentDataCompound(Object itemStack, Object compound);
}
