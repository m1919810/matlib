package me.matl114.matlibAdaptor.proxy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AdaptorInterface {
    /**
     * this Annotation marks the Adaptor Interface, any Interface marked as this is public to any package
     * their method should be either callable(with no class-cast) through its method signature or marked as {@link InternalMethod}
     * via proxy Utils, any package can get an adaptor to a class object in another package implementing different interface of same definition
     * any of the method (in annotated class and its super interfaces) which is not marked as {@link InternalMethod}, must not contains any class definition in matlib,otherwise {@link ClassCastException} may be thrown
     */
}
