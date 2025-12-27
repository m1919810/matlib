package me.matl114.matlib.utils.reflect.proxy.methodMap;

import java.lang.reflect.Method;
import me.matl114.matlib.common.lang.annotations.Note;

public record MethodIndex(
        @Note("target class method") Method target,
        @Note("interface method signature") MethodSignature signature,
        @Note("internal index") int index,
        boolean hasDefault) {}
