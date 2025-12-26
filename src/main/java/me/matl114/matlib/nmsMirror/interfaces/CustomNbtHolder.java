package me.matl114.matlib.nmsMirror.interfaces;

import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.COWView;
import me.matl114.matlib.common.lang.annotations.Note;

public interface CustomNbtHolder {
    @Note(
            value = "look at COWView 's note before you use",
            extra = {
                "Manual deep copy required when modifying nested CompoundTags, e.g. view[\"nested\"][\"key\"] = value  -> view[\"nested\"] =  copy(view[\"nested\"]).put(key, value)",
                "Why we design like that? most operations involve single-layer key-value access, Deep CompoundTags will be copy/created by PersistentDataAdapterContext"
            })
    @Nonnull
    COWView<Object> getCustomedNbtView(Object val, boolean forceCreate);
}
