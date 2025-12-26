package me.matl114.matlib.utils.reflect.classBuild.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * this annotation will let the builder find targeted pathed class instead of announced class in the interface(probably Object.class or sth)
 * if FieldAccess do not annotate this, we will match the first field find with certain name
 * write mojangNamed jvm descriptor here, we will use deobf name to match this flag
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.PARAMETER})
public @interface RedirectType {
    // must be jvm type name
    String value();
}
