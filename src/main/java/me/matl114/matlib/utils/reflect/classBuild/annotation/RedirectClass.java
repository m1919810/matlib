package me.matl114.matlib.utils.reflect.classBuild.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * used in MultiDescriptive class,
 * targeting to different class
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface RedirectClass {
    // can be jvm type name or Class name
    String value();
}
