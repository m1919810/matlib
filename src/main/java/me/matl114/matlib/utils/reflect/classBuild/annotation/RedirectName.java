package me.matl114.matlib.utils.reflect.classBuild.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * this annotation will let the builder find targeted pathed class by its provided name if return type and parameter type can not locate a field or method, if this annotation is absent, builder will generate potential method/field name from interface method name (method: using interface method name, field: name.removeSuffix("Accessor"), annotations of this on constructor target will be ignored
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD, ElementType.METHOD})
public @interface RedirectName {
    String value();
}
