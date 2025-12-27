package me.matl114.matlib.nmsMirror.interfaces;

import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.COWView;
import me.matl114.matlib.common.lang.annotations.Note;

public interface PdcCompoundHolder {
    @Note(
            value = "look at COWView 's note before you use",
            extra = {
                "Manual deep copy required when modifying nested CompoundTags, e.g. view[\"nested\"][\"key\"] = value  -> view[\"nested\"] =  copy(view[\"nested\"]).put(key, value)",
                "Why we design like that? most operations involve single-layer key-value access, Deep CompoundTags will be copy/created by PersistentDataAdapterContext",
                "Why do we have to copy? because of 1.20.5's components update, custom data becomes copy on write CompoundTags"
            })
    @Nonnull
    COWView<Object> getPersistentDataCompoundView(Object val, boolean forceCreate);

    @Note(
            value = "This is a shallow copy of pdc compound",
            extra = {
                "Manual deep copy required when modifying nested CompoundTags, e.g. view[\"nested\"][\"key\"] = value  -> view[\"nested\"] =  copy(view[\"nested\"]).put(key, value)"
            })
    Object getPersistentDataCompoundCopy(Object val);
}
