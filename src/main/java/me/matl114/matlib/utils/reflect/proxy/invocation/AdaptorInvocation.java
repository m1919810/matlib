package me.matl114.matlib.utils.reflect.proxy.invocation;

import it.unimi.dsi.fastutil.objects.ReferenceArraySet;
import java.util.*;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodIndex;
import me.matl114.matlib.utils.reflect.reflectasm.MethodAccess;

@Deprecated
public class AdaptorInvocation extends FastRemappingInvocation {
    private final MethodAccess fastAccess;

    //    private final Map<MethodSignature,Integer> methods2 ;

    private AdaptorInvocation(Set<MethodIndex> methods, MethodAccess asm) {
        super(methods);
        this.fastAccess = asm;
    }

    /**
     * create Adaptor of an object
     */
    public static AdaptorInvocation createASM(Class<?> proxyClass, Set<MethodIndex> methodIndex) {
        MethodAccess fastAccess = MethodAccess.get(proxyClass);
        Set<MethodIndex> remappedToASM = new ReferenceArraySet<>();
        for (var val : methodIndex) {
            if (val.index() < 0) {
                remappedToASM.add(val);
            } else {
                try {
                    remappedToASM.add(new MethodIndex(
                            val.target(),
                            val.signature(),
                            fastAccess.getIndex(
                                    val.target().getName(), val.target().getParameterTypes()),
                            false));
                } catch (Throwable e) {
                    if (val.hasDefault()) {
                        remappedToASM.add(
                                new MethodIndex(val.target(), val.signature(), DEFAULT_INVOCATION_INDEX, true));
                    } else {
                        Debug.severe("Error while creating Adaptor invocation context");
                        throw e;
                    }
                }
            }
        }
        return new AdaptorInvocation(remappedToASM, fastAccess);
    }

    public Object invoke0(Object proxy, Object val, MethodIndex index, Object[] args) {
        return fastAccess.invoke(val, index.index(), args);
    }
}
