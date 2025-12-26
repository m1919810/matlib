package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建字段
 * NAME会受到@RedirectName重定向
 * TYPE会受到@RedirectType重定向
 * 创建的字段都将是public的,出于方便考虑
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD})
public @interface UniqueField {}
