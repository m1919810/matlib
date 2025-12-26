package me.matl114.matlib.utils.reflect.mixImpl.buildTools;

import me.matl114.matlib.common.lang.annotations.Note;

@Note(
        "This is a wrapper of Target MixImpl class, a so-called Content, which you can invoke constructor and get Class instance from here")
public interface MixContent<T extends MixBase> {
    Class<T> getImplClass();

    boolean isInstanceOf(Object val);

    @Note("cast method, instance of this should extends or implement from T, you can refer to")
    default T getImplContent() {
        return (T) this;
    }
}
