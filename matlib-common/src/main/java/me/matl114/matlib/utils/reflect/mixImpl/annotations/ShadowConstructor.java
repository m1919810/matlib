package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 同method，用于获得super()等
 * 所定义的类可以用@RedirectClass重定向, 会生成被方法包裹的new指令
 * 否则方法类型应当形如void ...
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.CONSTRUCTOR})
public @interface ShadowConstructor {}
