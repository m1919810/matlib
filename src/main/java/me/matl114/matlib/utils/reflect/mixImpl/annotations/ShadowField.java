package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.matl114.matlib.common.lang.annotations.Note;

/**
 * 捕获字段
 * NAME会受到@RedirectName重定向
 * TYPE会受到@RedirectType重定向
 *
 * 标注于field
 * 标注于method,NAME格式为getFIELD 或者setFIELD,需要严格保证参数数量,以便字节码转换
 * 捕获的字段都将是非private的!(至少目前是)
 * 可以用@RedirectClass重定向,但是当标记在FIELD时不能标记到其他类的非static字段
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD, ElementType.METHOD})
public @interface ShadowField {
    @Note(
            "This parameter controls whether create access to the field, if NOT, the method will be used as PlaceHolder of target Method, and method body will be replaced by MixImplException.placeholder()")
    boolean create() default false;
}
