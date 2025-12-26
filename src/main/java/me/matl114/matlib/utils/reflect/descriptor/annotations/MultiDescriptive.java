package me.matl114.matlib.utils.reflect.descriptor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this marks a 'APIHelper' interface
 * a 'APIHelper' interface should also extend from  TargetDescriptor
 * they collect static(mostly) methods from different class,
 * each target method should use {@RedirectClass} for class lookup
 * or collect different derivative class method implementation
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MultiDescriptive {
    String targetDefault() default "";
}
