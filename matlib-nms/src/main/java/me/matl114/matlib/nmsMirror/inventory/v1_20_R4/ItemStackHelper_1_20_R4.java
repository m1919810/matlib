package me.matl114.matlib.nmsMirror.inventory.v1_20_R4;

import static me.matl114.matlib.nmsMirror.Import.*;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.*;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import me.matl114.matlib.algorithms.dataStructures.frames.cowCollection.COWImmutableListView;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.COWView;
import me.matl114.matlib.algorithms.dataStructures.struct.State;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.NeedTest;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.core.v1_20_R4.DataComponentHolderHelper;
import me.matl114.matlib.nmsMirror.impl.CodecEnum;
import me.matl114.matlib.nmsMirror.impl.EmptyEnum;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.versioned.Env1_20_R4;
import me.matl114.matlib.nmsMirror.inventory.ItemStackHelper;
import me.matl114.matlib.nmsMirror.nbt.TagEnum;

import me.matl114.matlib.nmsUtils.DynamicOpUtils;
import me.matl114.matlib.nmsUtils.v1_20_R4.DataComponentUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.serialization.CodecUtils;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionAtLeast;

@VersionAtLeast(Version.v1_20_R4)
@Descriptive(target = "net.minecraft.world.item.ItemStack")
@Note("After 1.20.5, ItemStack use DataComponents, only customTag remain nbt, so")
public interface ItemStackHelper_1_20_R4 extends TargetDescriptor, ItemStackHelper, DataComponentHolderHelper {

    default boolean hasExtraData(Object stack) {
        return !isEmpty(stack)
                && !Env1_20_R4.ICOMPONENT.patchGetter(getComponents(stack)).isEmpty();
    }

    @MethodTarget(isStatic = true)
    @RedirectName("isSameItemSameComponents")
    public boolean isSameItemSameTags(
            @RedirectType(ItemStack) @Nonnull Object stack, @RedirectType(ItemStack) @Nonnull Object otherStack);

    @MethodTarget
    @RedirectName("set")
    Object setDataComponentValue(Object item, @RedirectType(DataComponentType) Object type, @Nonnull Object value);

    @Note("it will set Optional.empty() if component exists in item prototype")
    @MethodTarget
    @RedirectName("remove")
    Object removeOrSetEmpty(Object stack, @RedirectType(DataComponentType) Object type);

    default void removeFromPatch(Object stack, @RedirectType(DataComponentType) Object type) {
        if (!isEmpty(stack)) {
            Env1_20_R4.ICOMPONENT.removeFromPatch(getComponents(stack), type);
        }
    }

    @Internal
    default Optional<?> getFromPatchOrEmpty(Object componentMap, Object type) {
        return componentMap == DataComponentEnum.COMPONENT_MAP_EMPTY
                ? Optional.empty()
                : Env1_20_R4.ICOMPONENT.patchGetter(componentMap).getOrDefault(type, Optional.empty());
    }

    @Internal
    default Optional<?> getFromPatchOptional(Object itemStack, Object type) {
        return getFromPatchOrEmpty(getComponents(itemStack), type);
    }

    @Internal
    default Object getFromPatch(Object itemStack, Object type) {
        if (isEmpty(itemStack)) {
            return null;
        } else {
            Optional<?> val =
                    Env1_20_R4.ICOMPONENT.patchGetter(getComponents(itemStack)).get(type);
            return val == null ? null : val.orElse(null);
        }
    }

    default boolean hasInPatch(Object itemStack, @RedirectType(DataComponentType) Object dataComponentType) {
        //        return !isEmpty(stack) &&
        // Env1_20_R4.ICOMPONENT.patchGetter(getComponents(stack)).getOrDefault(dataComponentType,
        // Optional.empty()).isPresent();
        if (isEmpty(itemStack)) {
            return false;
        } else {
            Optional<?> val =
                    Env1_20_R4.ICOMPONENT.patchGetter(getComponents(itemStack)).get(dataComponentType);
            return val != null && val.isPresent();
        }
    }

    //    @MethodTarget
    //    Object getEnchantments(Object stack);

    @MethodTarget
    @RedirectName("save")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R3, below = false)
    @Internal
    default Object saveV1_20_R4(
            Object stack,
            @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registries,
            @RedirectType(Tag) Object tag) {
        if (isEmpty(stack)) {
            throw new IllegalArgumentException("Cannot encode empty ItemStack");
        } else {
            return CodecEnum.ITEMSTACK
                    .encode(stack, NMSCore.REGISTRIES.provideRegistryForDynamicOp(registries, Env.NBT_OP), tag)
                    .getOrThrow();
        }
    }

    @MethodTarget(isStatic = true)
    @RedirectName("parseOptional")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R3, below = false)
    default Object parseV1_20_R4(
            @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registries,
            @RedirectType(CompoundTag) Object nbt) {
        return NMSCore.COMPOUND_TAG.isEmpty(nbt)
                ? EmptyEnum.EMPTY_ITEMSTACK
                : CodecEnum.ITEMSTACK
                        .parse(NMSCore.REGISTRIES.provideRegistryForDynamicOp(registries, Env.NBT_OP), nbt)
                        .resultOrPartial((s) -> {
                            Debug.severe("Tried to load invalid item: '", s, "'");
                        })
                        .orElse(EmptyEnum.EMPTY_ITEMSTACK);
    }

    default Object saveNbtAsTag(Object itemStack) {
        if (isEmpty(itemStack)) {
            return NMSCore.COMPOUND_TAG.newComp();
        } else {
            Object patch = getComponentsPatch(itemStack);
            return CodecUtils.encode(DataComponentEnum.DATACOMPONENTPATCH_CODEC, DynamicOpUtils.nbtOp(), patch);
        }
    }

    default Map<String, ?> saveNbtAsHashMap(Object itemStack) {
        if (isEmpty(itemStack)) {
            return new HashMap<>();
        } else {
            Object patch = getComponentsPatch(itemStack);
            return (Map<String, ?>)
                    CodecUtils.encode(ComponentCodecEnum.DATACOMPONENTPATCH, DynamicOpUtils.primOp(), patch);
        }
    }

    default Object saveElementInPath(Object itemStack, String path) {
        return saveElementInPath0(itemStack, DataComponentUtils.getDataType(path));
    }

    default Object saveElementInPath0(Object itemStack, Object comp) {
        if (isEmpty(itemStack)) {
            return null;
        } else {
            Object val = getFromPatch(itemStack, comp);
            if (val == null) return null;
            else {
                Codec<Object> compCodec = DataComponentUtils.getTypeCodec(comp);
                return CodecUtils.encode(compCodec, DynamicOpUtils.primOp(), val);
            }
        }
    }

    @Note("primitive = null -> remove")
    default void replaceElementInPath(Object itemStack, String path, Object primitive) {
        replaceElementInPath0(itemStack, DataComponentUtils.getDataType(path), primitive);
    }

    default void replaceElementInPath0(Object itemStack, Object comp, Object primitive) {
        if (isEmpty(itemStack)) {
            throw new IllegalArgumentException("Can not modify a Empty ItemStack!");
        }
        if (primitive == null) {
            removeFromPatch(itemStack, comp);
        } else {
            Object val = CodecUtils.decode(DataComponentUtils.getTypeCodec(comp), DynamicOpUtils.primOp(), primitive);
            if (val != null) {
                setDataComponentValue(itemStack, comp, val);
            } else {
                removeFromPatch(itemStack, comp);
            }
        }
    }

    default void applyNbtFromMap(Object itemStack, Map<String, ?> val) {
        if (isEmpty(itemStack)) {
            throw new IllegalArgumentException("Can not modify a Empty ItemStack!");
        }
        Object patch = CodecUtils.decode(ComponentCodecEnum.DATACOMPONENTPATCH, DynamicOpUtils.primOp(), val);
        Env1_20_R4.ICOMPONENT.restorePatch(getComponents(itemStack), patch);
    }

    // should copy CustomData on write because in higher version there is no deepcopy anymore
    static final Supplier<Object> EMPTY_COMP = Suppliers.memoize(() -> {
        int a = 1145141919;
        return NMSCore.COMPOUND_TAG.newComp();
    });

    @Internal
    default Object createCustomDataUnsafe(Object val) {
        Object customData = Env1_20_R4.ICUSTOMDATA.of(EMPTY_COMP.get());
        setDataComponentValue(val, DataComponentEnum.CUSTOM_DATA, customData);
        return Env1_20_R4.ICUSTOMDATA.getUnsafe(customData);
    }

    @Internal
    @Note("return pdc")
    default Object copyAndWriteCustomDataWithPdcTag(Object val, Object customTag) {
        Object newCustomData = Env1_20_R4.ICUSTOMDATA.of(customTag);
        setDataComponentValue(val, DataComponentEnum.CUSTOM_DATA, newCustomData);
        Object copiedNbt = Env1_20_R4.ICUSTOMDATA.getUnsafe(newCustomData);
        Object pdc = NMSCore.COMPOUND_TAG.newComp();
        NMSCore.COMPOUND_TAG.put(copiedNbt, "PublicBukkitValues", pdc);
        return pdc;
    }

    @Internal
    @Note("return pdc")
    default Object createCustomDataWithPdcUnsafe(Object val) {
        return copyAndWriteCustomDataWithPdcTag(val, EMPTY_COMP.get());
        //        Object customData = Env1_20_R4.ICUSTOMDATA.of(EMPTY_COMP.get());
        //        setDataComponentValue(val, DataComponentEnum.CUSTOM_DATA, customData);
        //        Object newCopy = Env1_20_R4.ICUSTOMDATA.getUnsafe(customData);
        //        Object newPdc = NMSCore.COMPOUND_TAG.newComp();
        //        NMSCore.COMPOUND_TAG.put(newCopy, "PublicBukkitValues", newPdc);
        //        return newCopy;
    }

    default Object getPdcCompoundView(Object val, boolean forceCreate) {
        Object customData = getCustomDataUnsafe(val);
        Object pdc;
        if (customData == null) {
            if (forceCreate) {
                pdc = createCustomDataWithPdcUnsafe(val);
            } else {
                pdc = null;
            }
        } else {
            boolean hasPdc = NMSCore.COMPOUND_TAG.contains(customData, "PublicBukkitValues", TagEnum.TAG_COMPOUND);
            if (hasPdc) {
                pdc = NMSCore.COMPOUND_TAG.getCompound(customData, "PublicBukkitValues");
            } else {
                pdc = null;
                if (forceCreate) {
                    pdc = copyAndWriteCustomDataWithPdcTag(val, customData);
                }
            }
        }
        return pdc;
    }

    default Object getCustomTagView(Object val, boolean forceCreate) {
        Object customData = getCustomDataUnsafe(val);
        if (customData == null && forceCreate) {
            customData = createCustomDataUnsafe(val);
        }
        return customData;
    }

    default void setPersistentDataCompound(Object val, Object pdc) {
        // shallllllllllowcopy copy !!!!!!!!!!!!!
        if (pdc == null || NMSCore.COMPOUND_TAG.isEmpty(pdc)) {
            // ignore if no data
            Object customData = getFromPatch(val, DataComponentEnum.CUSTOM_DATA);
            Object customTag;
            if (customData == null) {
                // already empty
                return;
            } else {
                customTag = Env1_20_R4.ICUSTOMDATA.getUnsafe(customData);
            }
            Object shallowCopy = NMSCore.COMPOUND_TAG.shallowCopy(customTag);
            NMSCore.COMPOUND_TAG.remove(shallowCopy, "PublicBukkitValues");
            if (NMSCore.COMPOUND_TAG.isEmpty(shallowCopy)) {
                removeFromPatch(val, DataComponentEnum.CUSTOM_DATA);
            } else {
                Object data = Env1_20_R4.ICUSTOMDATA.ofNoCopy(shallowCopy);
                setDataComponentValue(val, DataComponentEnum.CUSTOM_DATA, data);
            }
        } else {
            Object pdcCopy = NMSCore.COMPOUND_TAG.shallowCopy(pdc);
            // directly replace the pdc after shallow copy, nothing is changed yeeeeeeeee motherfucker
            Object customData = getFromPatch(val, DataComponentEnum.CUSTOM_DATA);
            Object customTag;
            if (customData == null) {
                customTag = EMPTY_COMP.get();
            } else {
                customTag = Env1_20_R4.ICUSTOMDATA.getUnsafe(customData);
            }
            Object shallowCopy = NMSCore.COMPOUND_TAG.shallowCopy(customTag);
            NMSCore.COMPOUND_TAG.put(shallowCopy, "PublicBukkitValues", pdcCopy);
            Object data = Env1_20_R4.ICUSTOMDATA.ofNoCopy(shallowCopy);
            setDataComponentValue(val, DataComponentEnum.CUSTOM_DATA, data);
        }
    }

    default void setCustomTag(Object val, Object tag) {
        if (tag == null || NMSCore.COMPOUND_TAG.isEmpty(tag)) {
            removeFromPatch(val, DataComponentEnum.CUSTOM_DATA);
        } else {
            Object shallowCopy = NMSCore.COMPOUND_TAG.shallowCopy(tag);
            Object data = Env1_20_R4.ICUSTOMDATA.ofNoCopy(shallowCopy);
            setDataComponentValue(val, DataComponentEnum.CUSTOM_DATA, data);
        }
    }

    @Override
    default COWView<Object> getPersistentDataCompoundView(Object val, boolean forceCreate) {

        return new COWView<Object>() {
            @Override
            public Object getView0() {
                return getPdcCompoundView(val, forceCreate);
            }

            @Override
            public Object getWritable() {
                if (getView() == null) {
                    return NMSCore.COMPOUND_TAG.newComp();
                } else {
                    return NMSCore.COMPOUND_TAG.shallowCopy(getView());
                }
            }

            @Override
            public void write0(Object val222) {
                setPersistentDataCompound(val, val222);
            }
        };
    }

    default Object getPersistentDataCompoundCopy(Object val) {
        Object custom = getPdcCompoundView(val, false);
        return custom == null ? NMSCore.COMPOUND_TAG.newComp() : NMSCore.COMPOUND_TAG.shallowCopy(custom);
    }

    @Override
    default COWView<Object> getCustomedNbtView(Object val, boolean forceCreate) {
        return new COWView<Object>() {
            @Override
            public Object getView0() {
                return getCustomTagView(val, forceCreate);
            }

            @Override
            public Object getWritable() {
                if (getView() == null) {
                    return NMSCore.COMPOUND_TAG.newComp();
                } else {
                    return NMSCore.COMPOUND_TAG.shallowCopy(getView());
                }
            }

            @Override
            public void write0(Object val00) {
                setCustomTag(val, val00);
            }
        };
    }

    @MethodTarget
    @Internal
    Object getComponents(Object stack);

    @MethodTarget
    @Internal
    Object getComponentsPatch(Object stack);

    @Override
    default Object ofNbt(@RedirectType(CompoundTag) Object nbt) {
        return parseV1_20_R4(Env.REGISTRY_FROZEN, nbt);
    }

    @Override
    default Object save(Object stack, @RedirectType(CompoundTag) Object tag) {
        return saveV1_20_R4(stack, Env.REGISTRY_FROZEN, tag);
    }
    //    @Internal
    //    default boolean hasCustomTag(Object stack){
    //        return getCustomTag(stack) != null;
    //    }
    @Internal
    default Object getCustomDataUnsafe(Object stack) {
        return Env1_20_R4.ICUSTOMDATA.unsafeOrNull(getFromPatch(stack, DataComponentEnum.CUSTOM_DATA));
    }

    @Internal
    default void setCustomData(Object stack, @RedirectType(CompoundTag) @Nullable Object nbt) {
        if (nbt == null || NMSCore.COMPOUND_TAG.isEmpty(nbt)) {
            removeFromPatch(stack, DataComponentEnum.CUSTOM_DATA);
        } else {
            setDataComponentValue(stack, DataComponentEnum.CUSTOM_DATA, Env1_20_R4.ICUSTOMDATA.of(nbt));
        }
    }

    @MethodTarget
    void applyComponents(Object stack, @RedirectType(DataComponentPatch) Object dataPatch);

    @Override
    default boolean hasCustomHoverName(Object stack) {
        return hasInPatch(stack, DataComponentEnum.CUSTOM_NAME);
    }

    @Internal
    default Object setHoverName(Object stack, @RedirectType(ChatComponent) Iterable<?> name) {
        if (name != null) {
            setDataComponentValue(stack, DataComponentEnum.CUSTOM_NAME, name);
        } else {
            removeFromPatch(stack, DataComponentEnum.CUSTOM_NAME);
        }
        return stack;
    }

    default boolean hasLore(Object stack) {
        return hasInPatch(stack, DataComponentEnum.LORE);
    }

    @Nonnull
    @Override
    default ListMapView<?, Iterable<?>> getLoreView(Object stack, boolean overrideOnWrite) {
        class COWListViewWithMapping extends COWImmutableListView<Iterable<?>>
                implements ListMapView<Iterable<?>, Iterable<?>> {
            public COWListViewWithMapping() {
                super(List.of(), ArrayList::new);
            }

            @Override
            public void batchWriteback() {
                replaceLore(stack, this.delegate.value);
            }

            @Override
            public boolean isDelayWrite() {
                return true;
            }

            @Override
            public void flush() {
                this.delegate = flush0();
            }

            public State<List<Iterable<?>>> flush0() {
                final Object itemLore = getFromPatch(stack, DataComponentEnum.LORE);
                State<List<Iterable<?>>> state0 = State.newInstance();
                if (itemLore != null) {
                    // it willllllllllllllllllllllll modify origin itemStack because it does not copy when item copy
                    state0.value = Env1_20_R4.DATA_TYPES.itemLore$lines(itemLore);
                    // set cow flag to copy on write
                    state0.state = true;
                } else {
                    state0.value = new ArrayList<>();
                    // no need to set cow flag because this is already a new List, there is no need to copy again
                }
                return state0;
            }
        }
        ListMapView<?, Iterable<?>> mapView = (ListMapView<?, Iterable<?>>) new COWListViewWithMapping();
        mapView.flush();
        return mapView;
    }

    default void replaceLore(Object item, List<Iterable<?>> lore) {
        if (lore == null) {
            removeFromPatch(item, DataComponentEnum.LORE);
        } else {
            Object newItemLore = Env1_20_R4.DATA_TYPES.newItemLore(lore);
            setDataComponentValue(item, DataComponentEnum.LORE, newItemLore);
        }
    }

    @Override
    default boolean matchItem(
            Object item1,
            Object item2,
            @Note(
                            "distinct assumed that they both have lore/name, and we don't care about them, BUT if one of then don't have, then it is regarded as not match")
                    boolean distinctLore,
            boolean distinctName) {
        if (item1 == item2) {
            return true;
        }
        if (item1 == null || item2 == null) {
            return false;
        }
        if (distinctLore && distinctName) {
            return isSameItemSameTags(item1, item2);
        }
        if (getItem(item1) != getItem(item2)) {
            return false;
        }
        Object comp1 = getComponents(item1);
        Object comp2 = getComponents(item2);
        if (comp1 == comp2) {
            return true;
        }
        if (comp1 == null || comp2 == null) {
            return false;
        }
        // should match name , or match lore here
        return matchComp(comp1, comp2, distinctLore, distinctName);
    }

    @Override
    default boolean matchNbt(Object item1, Object item2, boolean distinctLore, boolean distinctName) {
        Object comp1 = getComponents(item1);
        Object comp2 = getComponents(item2);
        if (comp1 == comp2) {
            return true;
        }
        if (comp1 == null || comp2 == null) {
            return false;
        }
        if (distinctLore && distinctName) {
            return Objects.equals(comp1, comp2);
        }
        // should match name , or match lore here
        return matchComp(comp1, comp2, distinctLore, distinctName);
    }
    // need optimize
    @Internal
    default boolean matchComp(@Nonnull Object comp1, @Nonnull Object comp2, boolean matchLore, boolean matchName) {
        // prototype is from Item.component, it should be the reference to same object! if itemType is same
        //        if(Env1_20_R4.ICOMPONENT.prototypeGetter(comp1) != Env1_20_R4.ICOMPONENT.prototypeGetter(comp2)){
        //            return false;
        //        }
        if (comp1 == DataComponentEnum.COMPONENT_MAP_EMPTY || comp2 == DataComponentEnum.COMPONENT_MAP_EMPTY) {
            return comp1 == comp2;
        }

        Reference2ObjectMap<Object, Optional<?>> patch1 = Env1_20_R4.ICOMPONENT.patchGetter(comp1);
        Reference2ObjectMap<Object, Optional<?>> patch2 = Env1_20_R4.ICOMPONENT.patchGetter(comp2);

        if (patch1 == patch2) {
            return true;
        }
        if (patch1.size() != patch2.size()) return false;
        ObjectSet<Reference2ObjectMap.Entry<Object, Optional<?>>> entryset1 = patch1.reference2ObjectEntrySet();
        ObjectIterator<Reference2ObjectMap.Entry<Object, Optional<?>>> iter =
                entryset1 instanceof Reference2ObjectMap.FastEntrySet fast ? fast.fastIterator() : entryset1.iterator();

        while (iter.hasNext()) {
            var entry = iter.next();
            var type = entry.getKey();
            if (!matchName && type == DataComponentEnum.CUSTOM_NAME) {
            } else if (!matchLore && type == DataComponentEnum.LORE) {
            } else {
                if (!Objects.equals(entry.getValue(), patch2.get(type))) {
                    return false;
                }
            }
        }
        // ensure that no more type that need check in patch2
        return true;
    }

    @MethodTarget(isStatic = true)
    @RedirectName("hashItemAndComponents")
    @NeedTest
    public int customHashcode(@Nonnull @RedirectType(ItemStack) Object item);

    @Override
    default int customHashWithoutDisplay(Object item) {
        int a = 79 * getItem(item).hashCode();
        Object comp = getComponents(item);
        if (comp == null || comp == DataComponentEnum.COMPONENT_MAP_EMPTY) {
            return a;
        }
        Reference2ObjectMap<Object, Optional<?>> patch = Env1_20_R4.ICOMPONENT.patchGetter(comp);
        if (patch.containsKey(DataComponentEnum.LORE)) {
            Map<Object, Optional<?>> patchShallowCopy = new Reference2ObjectOpenHashMap<>(patch);
            patchShallowCopy.remove(DataComponentEnum.LORE);
            return a + patchShallowCopy.hashCode();
        }
        return a + patch.hashCode();
        //        ObjectSet<Reference2ObjectMap.Entry<Object, Optional<?>>> entryset = patch.reference2ObjectEntrySet();
        //        Object key,value;
        //        for (var entry: entryset){
        //            key = entry.getKey();
        //            if(key != DataComponentEnum.LORE && key != null && (value = entry.getValue()) != null){
        //                a += key.hashCode()^ value.hashCode();
        //            }
        //        }
        //        return a;
    }

    default boolean presentAt(Reference2ObjectMap<?, Optional<?>> map, Object type) {
        var re = map.get(type);
        if (re == null || re.isEmpty()) {
            return false;
        }
        return true;
    }
}
