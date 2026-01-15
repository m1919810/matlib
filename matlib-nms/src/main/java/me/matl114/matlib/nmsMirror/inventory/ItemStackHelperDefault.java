package me.matl114.matlib.nmsMirror.inventory;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.COWView;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.MappingList;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.impl.NMSChat;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.nbt.TagEnum;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.serialization.TypeOps;
import me.matl114.matlib.utils.version.Version;

@Descriptive(target = "net.minecraft.world.item.ItemStack")
public interface ItemStackHelperDefault extends TargetDescriptor, ItemStackHelper {

    @MethodTarget(isStatic = true)
    @RedirectName("of")
    public Object ofNbt(@RedirectType(CompoundTag) Object nbt);

    @Note("Do not use it, its returnType varies with version, only used for comp")
    @MethodTarget
    @RedirectName("getEnchantmentTags")
    Object getEnchantments(Object stack);

    @MethodTarget
    public Object save(Object itemStack, @RedirectType(CompoundTag) Object nbt);

    default Object saveNbtAsTag(Object itemStack) {
        Object nbt = getCustomTag(itemStack);
        if (nbt == null) {
            return NMSCore.COMPOUND_TAG.newComp();
        } else {
            return NMSCore.COMPOUND_TAG.copy(nbt);
        }
    }

    default Map<String, ?> saveNbtAsMap(Object itemStack) {
        Object nbt = getCustomTag(itemStack);
        if (nbt == null) {
            return new LinkedHashMap<>();
        } else {
            return (Map<String, ?>) Env.NBT_OP.convertTo(TypeOps.I, nbt);
        }
    }

    default Object saveElementInPath(Object itemStack, String path) {
        Object nbt = getCustomTag(itemStack);
        if (nbt == null) {
            return null;
        }
        Object val = NMSCore.COMPOUND_TAG.get(nbt, path);
        return val == null ? null : Env.NBT_OP.convertTo(TypeOps.I, val);
    }

    default void applyNbtFromMap(Object itemStack, Map<String, ?> val) {
        if (isEmpty(itemStack)) {
            throw new IllegalArgumentException("Can not modify a Empty ItemStack!");
        }
        Object nbt = TypeOps.I.convertTo(Env.NBT_OP, val);
        if (nbt != null) {
            setTag(itemStack, nbt);
        }
    }

    @Note("primitive = null -> remove")
    default void replaceElementInPath(Object itemStack, String path, Object primitive) {
        if (isEmpty(itemStack)) {
            throw new IllegalArgumentException("Can not modify a Empty ItemStack!");
        }
        if (primitive == null) {
            Object nbt = getCustomTag(itemStack);
            if (nbt != null) {
                NMSCore.COMPOUND_TAG.remove(nbt, path);
            }
        } else {
            Object nbt0 = TypeOps.I.convertTo(Env.NBT_OP, primitive);
            if (nbt0 != null) {
                Object nbt = getOrCreateCustomTag(itemStack);
                NMSCore.COMPOUND_TAG.put(nbt, path, nbt0);
            } else {
                replaceElementInPath(itemStack, path, null);
            }
        }
    }

    @MethodTarget
    @RedirectName("hasTag")
    @Note("hasExtraTag value may vary when version up than 1_20_R4")
    public boolean hasExtraData(Object stack);

    @Nullable @MethodTarget
    @RedirectName("getTag")
    @Note("This returns the custom nbtTag of item, no copy")
    public Object getCustomTag(Object stack);

    @Nullable @MethodTarget
    @RedirectName("getOrCreateTag")
    @Note("This returns the custom nbtTag of item, no copy")
    public Object getOrCreateCustomTag(Object stack);

    @MethodTarget
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    @Internal
    public void addTagElement(Object stack, String key, @RedirectType(Tag) Object element);

    @MethodTarget
    @Internal
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    Object getOrCreateTagElement(Object stack, String key);

    @Internal
    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    @MethodTarget
    public Object getTagElement(Object stack, String key);

    @MethodTarget
    public void setTag(Object stack, @RedirectType(CompoundTag) @Nullable Object nbt);

    @MethodTarget
    public boolean hasCustomHoverName(Object stack);

    // todo make it run in 1.21
    @MethodTarget
    Iterable<?> getHoverName(Object stack);

    @MethodTarget
    Object setHoverName(Object stack, @RedirectType(ChatComponent) Iterable<?> name);

    @Internal
    default List<String> getLoreRaw(Object stack) {
        Object nbt = getTagElement(stack, DISPLAY);
        if (nbt != null && NMSCore.COMPOUND_TAG.getTagType(nbt, LORE) == TagEnum.TAG_LIST) {
            AbstractList<?> list = NMSCore.COMPOUND_TAG.getList(nbt, LORE, TagEnum.TAG_STRING);
            List<String> newList = new ArrayList<>(list.size());
            for (var entyr : list) {
                newList.add(NMSCore.TAGS.getAsString(entyr));
            }
            return newList;
        } else {
            return null;
        }
    }

    @Nonnull
    default ListMapView<?, Iterable<?>> getLoreView(Object stack, boolean overrideOnWrite) {

        Function<Iterable<?>, Object> writer = (comp) -> {
            String json = NMSChat.CHATCOMPONENT.toJson(comp);
            return NMSCore.TAGS.stringTag(json);
        };
        Consumer<MappingList<Object, Iterable<?>>> writeback0 = (mplist) -> {
            List<Object> originList = mplist.getOrigin();
            Object nbt0 = getOrCreateCustomTag(stack);
            Object nbt1 = NMSCore.COMPOUND_TAG.getOrNewCompound(nbt0, DISPLAY);
            //
            NMSCore.COMPOUND_TAG.put(nbt1, LORE, originList);
        };
        MappingList<Object, Iterable<?>> mappingList =
                new MappingList<Object, Iterable<?>>(
                        (tag) -> {
                            String json = NMSCore.TAGS.getAsString(tag);
                            return NMSChat.CHATCOMPONENT.fromJson(json);
                        },
                        writer,
                        List.of()) {
                    @Override
                    public void flush() {
                        super.flush();
                        Object nbt = getTagElement(stack, DISPLAY);
                        AbstractList<Object> loreList;
                        if (nbt != null && NMSCore.COMPOUND_TAG.getTagType(nbt, LORE) == TagEnum.TAG_LIST) {
                            loreList =
                                    (AbstractList<Object>) NMSCore.COMPOUND_TAG.getList(nbt, LORE, TagEnum.TAG_STRING);
                            this.withWriteBack(overrideOnWrite ? writeback0 : null);
                        } else {
                            loreList = (AbstractList<Object>) NMSCore.TAGS.listTag();
                            this.withWriteBack(writeback0);
                        }
                        this.origin = loreList;
                    }
                };
        mappingList.flush();
        return mappingList;
    }

    default void replaceLore(Object item, List<Iterable<?>> lore) {
        if (lore == null) {
            Object nbt = getTagElement(item, DISPLAY);
            if (nbt != null) {
                NMSCore.COMPOUND_TAG.remove(nbt, LORE);
            }
        } else {
            List<Object> nbtList = (List<Object>) NMSCore.TAGS.listTag();
            for (var re : lore) {
                nbtList.add(NMSCore.TAGS.stringTag(NMSChat.CHATCOMPONENT.toJson(re)));
            }
            NMSCore.COMPOUND_TAG.put(getOrCreateTagElement(item, DISPLAY), LORE, nbtList);
        }
    }

    default boolean hasCustomTagKey(Object stack, String key) {
        Object tag = getCustomTag(stack);
        return tag != null && NMSCore.COMPOUND_TAG.contains(tag, key);
    }

    default int getCustomTagInt(Object stack, String key) {
        Object tag = getCustomTag(stack);
        return tag != null ? NMSCore.COMPOUND_TAG.getInt(tag, key) : 0;
    }

    default boolean hasLore(Object stack) {
        Object nbt = getTagElement(stack, DISPLAY);
        return nbt != null && NMSCore.COMPOUND_TAG.getTagType(nbt, LORE) == 9;
    }

    default COWView<Object> getPersistentDataCompoundView(Object val, boolean forceCreate) {
        return new COWView<Object>() {
            @Override
            public Object getView0() {
                return getPersistentDataCompound(val, forceCreate);
            }

            @Override
            public Object getWritable() {
                return this.cache == null ? getPersistentDataCompound(val, true) : this.cache;
            }

            @Override
            public void write0(Object val0) {
                setPersistentDataCompound(val, val0);
            }
        };
    }

    default COWView<Object> getCustomedNbtView(Object val, boolean forceCreate) {
        return forceCreate
                ? new COWView<Object>() {
                    @Override
                    public Object getView0() {
                        return getCustomTag(val);
                    }

                    @Override
                    public Object getWritable() {
                        return this.cache == null ? getOrCreateCustomTag(val) : this.cache;
                    }

                    @Override
                    public void write0(Object val00) {
                        // needed for Enchantment order
                        setTag(val, val00);
                    }
                }
                : new COWView<Object>() {
                    @Override
                    public Object getView0() {
                        return getOrCreateCustomTag(val);
                    }

                    @Override
                    public Object getWritable() {
                        return getView();
                    }

                    @Override
                    public void write0(Object val00) {
                        // needed for Enchantment order
                        setTag(val, val00);
                    }
                };
    }

    default Object getPersistentDataCompound(Object val, boolean create) {

        if (create) {
            Object custom = getOrCreateCustomTag(val);
            return NMSCore.COMPOUND_TAG.getOrNewCompound(custom, "PublicBukkitValues");
        } else {
            Object custom = getCustomTag(val);
            return custom == null ? null : NMSCore.COMPOUND_TAG.get(custom, "PublicBukkitValues");
        }
    }

    default Object getPersistentDataCompoundCopy(Object val) {
        Object custom = getPersistentDataCompound(val, false);
        return custom == null ? NMSCore.COMPOUND_TAG.newComp() : NMSCore.COMPOUND_TAG.shallowCopy(custom);
    }

    default void setPersistentDataCompound(Object itemStack, Object compound) {
        if (compound == null || NMSCore.COMPOUND_TAG.isEmpty(compound)) {
            Object custom = getCustomTag(itemStack);
            if (custom != null) {
                NMSCore.COMPOUND_TAG.remove(custom, "PublicBukkitValues");
            }
        } else {
            Object custom = getOrCreateCustomTag(itemStack);
            NMSCore.COMPOUND_TAG.put(custom, "PublicBukkitValues", compound);
        }
    }

    static String DISPLAY = "display";
    static String NAME = "Name";
    static int NAME_HASH = NAME.hashCode();
    static String LORE = "Lore";

    default boolean matchItem(
            @Nullable Object item1,
            @Nullable Object item2,
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
        Object nbt1 = getCustomTag(item1);
        Object nbt2 = getCustomTag(item2);
        if (nbt1 == null || nbt2 == null) {
            return nbt1 == nbt2;
        }
        return matchTag(nbt1, nbt2, distinctLore, distinctName);
    }

    default boolean matchNbt(Object item1, Object item2, boolean distinctLore, boolean distinctName) {

        Object nbt1 = getCustomTag(item1);
        Object nbt2 = getCustomTag(item2);
        if (nbt1 == null || nbt2 == null) {
            return nbt1 == nbt2;
        }
        if (distinctLore && distinctName) {
            return nbt1.equals(nbt2);
        }
        return matchTag(nbt1, nbt2, distinctLore, distinctName);
    }

    @Internal
    default boolean matchTag(@Nonnull Object nbt1, @Nonnull Object nbt2, boolean matchLore, boolean matchName) {
        Map<String, ?> map1 = NMSCore.COMPOUND_TAG.tagsGetter(nbt1);
        Map<String, ?> map2 = NMSCore.COMPOUND_TAG.tagsGetter(nbt2);
        // get the DISPLAY tag here
        if (map1.size() != map2.size()) {
            return false;
        }
        Object obj1 = map1.get(DISPLAY);
        Set<? extends Map.Entry<String, ?>> key1 = map1.entrySet();
        for (var val : key1) {
            Object value = val.getValue();
            // escape certain tag value, which its key is DISPLAY, using Reference == instead of String.equals
            // in this situation, values are different from each other
            if (value == obj1) {
                continue;
                // not equal, otherside null(key absent) or sth(value not match)
            } else if (!Objects.equals(value, map2.get(val.getKey()))) {
                return false;
            }
        }
        // key-value都匹配
        // 考虑Display
        // 有一方没有display
        Object obj2 = map2.get(DISPLAY);
        if (obj1 == null || obj2 == null) {
            // 如果不匹配任何display, 或者均没有,返回true
            return obj1 == obj2 || (!matchLore && !matchName);
        }

        // Class<?> nbtCompClass = NMSCore.COMPOUND_TAG.getTargetClass();
        if (isCompoundTag(obj1) && isCompoundTag(obj2)) {
            if (matchName
                    && !Objects.equals(NMSCore.COMPOUND_TAG.get(obj1, NAME), NMSCore.COMPOUND_TAG.get(obj2, NAME))) {
                return false;
            }
            if (matchLore
                    && !Objects.equals(NMSCore.COMPOUND_TAG.get(obj1, LORE), NMSCore.COMPOUND_TAG.get(obj2, LORE))) {
                return false;
            }
            return true;
        } else {
            return obj1.getClass() == obj2.getClass();
        }
    }

    default int customHashcode(@Nonnull Object item) {
        int a = 79 * getItem(item).hashCode();
        var nbt = getCustomTag(item);
        if (nbt == null) {
            return a;
        }
        Map<String, ?> tag = NMSCore.COMPOUND_TAG.tagsGetter(nbt);
        Object val = tag.get(DISPLAY);
        if (val != null && NMSCore.TAGS.isCompound(val)) {
            var map = new HashMap<>(tag);
            map.remove(DISPLAY);
            Object nameVal = NMSCore.COMPOUND_TAG.get(val, NAME);
            // lore hashCode has bug, we only calculate its size
            Object loreVal = NMSCore.COMPOUND_TAG.get(val, LORE);
            return a
                    + map.hashCode()
                    + (nameVal == null ? 0 : NAME_HASH ^ nameVal.hashCode())
                    + (loreVal instanceof AbstractList<?> list ? 3419 * list.size() : 7);
        }
        return a + tag.hashCode();
        //        int a = 79* getItem(item).hashCode() ;
        //        var nbt = getCustomTag(item);
        //        return a + (nbt == null ? -1:
        //            31*nbt.hashCode());
        // 31*NMSCore.TAGS.sizeInBytes(nbt));
    }

    default int customHashWithoutDisplay(Object item) {
        int a = 79 * getItem(item).hashCode();
        var nbt = getCustomTag(item);
        if (nbt == null) {
            return a;
        }
        Map<String, ?> tag = NMSCore.COMPOUND_TAG.tagsGetter(nbt);
        Object val = tag.get(DISPLAY);
        if (val != null && NMSCore.TAGS.isCompound(val)) {
            var map = new HashMap<>(tag);
            map.remove(DISPLAY);
            Object nameVal = NMSCore.COMPOUND_TAG.get(val, NAME);
            return a + map.hashCode() + (nameVal == null ? 0 : NAME_HASH ^ nameVal.hashCode());
        }
        return a + tag.hashCode();
        //        var optionalDisplay = tag.get(DISPLAY);
        //        Object value;
        //        for (var entry: tag.entrySet()){
        //            value = entry.getValue();
        //            if(value != optionalDisplay){
        //                a += entry.getKey().hashCode() ^( value == null ? 0 : value.hashCode());
        //            }
        //        }
        //        if(optionalDisplay != null && NMSCore.TAGS.isCompound(optionalDisplay)){
        //            Object v = NMSCore.COMPOUND_TAG.get(optionalDisplay, NAME);
        //            if(v != null){
        //                a += NAME_HASH^ v.hashCode();
        //            }
        //        }
        //        return  a;
    }
}
