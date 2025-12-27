package me.matl114.matlib.utils.reflect.classBuild.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import me.matl114.matlib.utils.version.Version;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
/**
 * if no IgnoreFailure present ,
 * throw exception and interrupt when fail
 */
public @interface FailHard {

    Version thresholdInclude();

    boolean below();
}
