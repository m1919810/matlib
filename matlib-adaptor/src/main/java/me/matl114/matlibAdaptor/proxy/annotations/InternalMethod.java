package me.matl114.matlibAdaptor.proxy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InternalMethod {
    /**
     * this annotation marks the internal method in {@link AdaptorInterface}
     * method marks as this should not be lookup from other packages' proxy utils
     */
}
