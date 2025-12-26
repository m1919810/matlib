package me.matl114.matlib.utils.reflect.descriptor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD, ElementType.METHOD})
public @interface MethodTarget {
    boolean isStatic() default false;
}
