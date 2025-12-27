package me.matl114.matlib.common.lang.annotations;

public @interface CodecLike {
    String value();

    String instance() default "";
}
