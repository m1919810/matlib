package me.matl114.matlib.nmsUtils.nbt;

import static me.matl114.matlib.nmsMirror.impl.CraftBukkit.PERSISTENT_DATACONTAINER;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.COMPOUND_TAG;
import static me.matl114.matlib.nmsUtils.CraftBukkitUtils.*;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.COWView;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

@Note("create a pdc from a Compound Tag or a map, No new Map will be created")
public class TagCompoundView implements PersistentDataContainer {
    public void flush() {
        this.compoundTagViews.flush();
    }

    @Getter
    @Nonnull
    protected final COWView<Object> compoundTagViews;

    //    private final Object registry;
    //    private final PersistentDataAdapterContext context;
    public TagCompoundView(COWView<Object> compoundTags) {
        compoundTagViews = compoundTags;
    }

    //    public TagCompoundView(Map<String, ?> rawMap){
    //        customDataTags = (Map<String, Object>) rawMap;
    //    }

    @Override
    @Note("writing to it should be careful, data may not apply to the original ItemStack")
    public <P, C> void set(@NotNull NamespacedKey key, @NotNull PersistentDataType<P, C> type, @NotNull C value) {
        Object customDataTags = compoundTagViews.getWritable();
        COMPOUND_TAG.put(
                customDataTags,
                key.toString(),
                PERSISTENT_DATACONTAINER.wrap(
                        getPdcDataTypeRegistry(), type, type.toPrimitive(value, getPdcAdaptorContext())));
        compoundTagViews.writeBack(customDataTags);
    }

    @Override
    public void remove(@NotNull NamespacedKey namespacedKey) {
        Object customDataTags = compoundTagViews.getWritable();
        COMPOUND_TAG.remove(customDataTags, namespacedKey.toString());
        compoundTagViews.writeBack(customDataTags);
    }

    @Override
    public void readFromBytes(byte @NotNull [] bytes, boolean b) throws IOException {

        getPdcDataTypeRegistry();
        PersistentDataContainer container = getPdcAdaptorContext().newPersistentDataContainer();
        container.readFromBytes(bytes);
        Object customDataTags = compoundTagViews.getWritable();
        if (b) {
            COMPOUND_TAG.clear(customDataTags);
        }
        for (var entry : PERSISTENT_DATACONTAINER.getRaw(container).entrySet()) {
            COMPOUND_TAG.put(customDataTags, entry.getKey(), entry.getValue());
        }
        compoundTagViews.writeBack(customDataTags);
    }

    @Override
    public <P, C> boolean has(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType) {
        Object customDataTags = compoundTagViews.getView();
        if (customDataTags == null) return false;
        Object raw = COMPOUND_TAG.get(
                customDataTags, namespacedKey.toString()); // customDataTags.get(namespacedKey.toString());
        if (raw == null) return false;
        return PERSISTENT_DATACONTAINER.isInstanceOf(getPdcDataTypeRegistry(), persistentDataType, raw);
    }

    @Override
    public boolean has(NamespacedKey namespacedKey) {
        Object customDataTags = compoundTagViews.getView();
        if (customDataTags == null) return false;
        return COMPOUND_TAG.contains(
                customDataTags, namespacedKey.toString()); // this.customDataTags.get(namespacedKey.toString()) != null;
    }

    @Override
    public <P, C> @Nullable C get(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType) {
        Object customDataTags = compoundTagViews.getView();
        if (customDataTags == null) return null;
        Object raw = COMPOUND_TAG.get(
                customDataTags, namespacedKey.toString()); // customDataTags.get(namespacedKey.toString());
        if (raw == null) return null;
        return persistentDataType.fromPrimitive(
                PERSISTENT_DATACONTAINER.extract(getPdcDataTypeRegistry(), persistentDataType, raw),
                getPdcAdaptorContext());
    }

    @Override
    public <P, C> C getOrDefault(NamespacedKey namespacedKey, PersistentDataType<P, C> persistentDataType, C c) {
        C value = get(namespacedKey, persistentDataType);
        return value == null ? c : value;
    }

    @Override
    public Set<NamespacedKey> getKeys() {
        Object customDataTags = compoundTagViews.getView();
        if (customDataTags == null) return Set.of();
        Set<NamespacedKey> keys = new HashSet<>();
        COMPOUND_TAG.getAllKeys(customDataTags).forEach(key -> {
            String[] keyData = key.split(":", 2);
            if (keyData.length == 2) {
                keys.add(new NamespacedKey(keyData[0], keyData[1]));
            }
        });

        return keys;
    }

    @Override
    public boolean isEmpty() {
        Object customDataTags = compoundTagViews.getView();
        if (customDataTags == null) return true;
        return COMPOUND_TAG.isEmpty(customDataTags);
    }

    @Override
    public void copyTo(PersistentDataContainer persistentDataContainer, boolean b) {
        if (persistentDataContainer instanceof TagCompoundView view) {
            Object tags0 = this.compoundTagViews.getView();
            if (tags0 != null) {
                Object customDataTags = view.compoundTagViews.getWritable();
                if (b) {
                    COMPOUND_TAG.tagsGetter(customDataTags).putAll((Map) COMPOUND_TAG.tagsGetter(tags0));
                } else {
                    Map map = COMPOUND_TAG.tagsGetter(customDataTags);
                    COMPOUND_TAG.tagsGetter(tags0).forEach((k, v) -> map.putIfAbsent(k, v));
                }
                view.compoundTagViews.writeBack(customDataTags);
            }
        } else if (PERSISTENT_DATACONTAINER.isCraftContainer(persistentDataContainer)) {
            Object val = this.compoundTagViews.getView();
            if (val != null) {
                Map<String, ?> tags = PERSISTENT_DATACONTAINER.getRaw(persistentDataContainer);
                Map map0 = COMPOUND_TAG.tagsGetter(val);
                if (b) {
                    tags.putAll((Map) map0);
                } else {
                    map0.forEach((k, v) -> ((Map) tags).putIfAbsent(k, v));
                }
            }

        } else {
            throw new UnsupportedOperationException(
                    "Persistent Data Container Class not supported: " + persistentDataContainer.getClass());
        }
    }

    @Override
    public PersistentDataAdapterContext getAdapterContext() {
        return getPdcAdaptorContext();
    }

    public void copyFrom(PersistentDataContainer craftPersistentDataContainer, boolean b) {
        if (craftPersistentDataContainer instanceof TagCompoundView view) {
            Object value = view.compoundTagViews.getView();
            if (value != null) {
                Object tags = this.compoundTagViews.getWritable();
                if (b) {
                    ((Map) COMPOUND_TAG.tagsGetter(tags)).putAll(COMPOUND_TAG.tagsGetter(value));
                } else {
                    Map map0 = (Map) COMPOUND_TAG.tagsGetter(tags);
                    COMPOUND_TAG.tagsGetter(value).forEach((k, v) -> map0.putIfAbsent(k, v));
                }
                this.compoundTagViews.writeBack(tags);
            }

        } else if (PERSISTENT_DATACONTAINER.isCraftContainer(craftPersistentDataContainer)) {
            Map<String, ?> tags = PERSISTENT_DATACONTAINER.getRaw(craftPersistentDataContainer);
            if (!tags.isEmpty()) {
                Object tags0 = this.compoundTagViews.getWritable();
                if (b) {
                    COMPOUND_TAG.tagsGetter(tags0).putAll((Map) tags);
                } else {
                    Map map0 = COMPOUND_TAG.tagsGetter(tags0);
                    tags.forEach((k, v) -> ((Map) map0).putIfAbsent(k, v));
                }
                this.compoundTagViews.writeBack(tags0);
            }

        } else {
            throw new UnsupportedOperationException(
                    "Persistent Data Container Class not supported: " + craftPersistentDataContainer.getClass());
        }
    }

    public PersistentDataContainer toCraftContainer() {
        Object val = this.compoundTagViews.getView();
        return val == null
                ? null
                : PERSISTENT_DATACONTAINER.newPersistentDataContainer(
                        COMPOUND_TAG.tagsGetter(val), getPdcDataTypeRegistry());
    }

    @Override
    public byte[] serializeToBytes() throws IOException {
        PersistentDataContainer craftPersistentDataContainer = toCraftContainer();
        return craftPersistentDataContainer == null ? new byte[0] : craftPersistentDataContainer.serializeToBytes();
    }
}
