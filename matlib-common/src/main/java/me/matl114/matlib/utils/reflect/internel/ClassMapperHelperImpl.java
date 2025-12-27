package me.matl114.matlib.utils.reflect.internel;

import java.lang.invoke.MethodHandle;
import java.util.Map;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.reflect.ReflectUtils;

@Note("this is a HAND-MADE impl for ObfHelper because we need to get the ObfSource before anything")
@Internal
class ClassMapperHelperImpl implements ClassMapperHelper {

    static final Class<?> clazz0 = ReflectUtils.findClass("io.papermc.paper.util.ObfHelper$ClassMapping");
    private static final MethodHandle handle1 = ReflectUtils.getMethodHandle(clazz0, "obfName");
    private static final MethodHandle handle2 = ReflectUtils.getMethodHandle(clazz0, "mojangName");
    private static final MethodHandle handle3 = ReflectUtils.getMethodHandle(clazz0, "methodsByObf");

    // private final Constructor<?> init0;
    public ClassMapperHelperImpl() {}

    @Override
    public String obfNameGetter(Object obj) {
        try {
            return (String) handle1.invoke(obj);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String mojangNameGetter(Object obj) {
        try {
            return (String) handle2.invoke(obj);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Map methodsByObf(Object self) {
        try {
            return (Map) handle3.invoke(self);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<?> getTargetClass() {
        return clazz0;
    }
}
