package me.matl114.matlib.utils.reflect.descriptor.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this marks a 'Helper' interface
 * a 'Helper' interface should also extend from  TargetDescriptor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Descriptive {
    /**
     * optional default target class, a descriptive interface can access to different class
     * @return
     */
    String target() default "";
}
