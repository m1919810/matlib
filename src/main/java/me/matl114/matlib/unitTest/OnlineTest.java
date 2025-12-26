package me.matl114.matlib.unitTest;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OnlineTest {
    boolean async() default true;

    String name();

    boolean automatic() default true;
}
