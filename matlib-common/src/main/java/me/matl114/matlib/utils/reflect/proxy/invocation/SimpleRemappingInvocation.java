package me.matl114.matlib.utils.reflect.proxy.invocation;

import java.util.Set;
import me.matl114.matlib.utils.reflect.proxy.methodMap.MethodIndex;

/**
 * it is asm-free
 */
public class SimpleRemappingInvocation extends FastRemappingInvocation {
    public SimpleRemappingInvocation(Set<MethodIndex> rawData) {
        super(rawData);
    }

    @Override
    public Object invoke0(Object proxy, Object target, MethodIndex methodIndex, Object[] args) {
        try {
            return methodIndex.target().invoke(target, args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
