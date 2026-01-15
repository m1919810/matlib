package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 捕获方法
 * NAME会受到@RedirectName重定向
 * PARAM会受到@RedirectType重定向
 * 当标注在method上时,会在目标类中窗内捕获形如NAME的方法, 并转为INVOKE_VIRTUAL
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.METHOD})
public @interface ShadowMethod {}
