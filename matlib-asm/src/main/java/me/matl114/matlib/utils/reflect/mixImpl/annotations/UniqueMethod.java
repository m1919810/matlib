package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 创建方法
 * NAME会受到@RedirectName重定向
 * PARAM会受到@RedirectType重定向
 * 当标注在interface的method上时,其名为NAME，
 *  当matchSuper = false时,会在目标类中窗内创建形如matlib$NAME的方法,并将原方法的全部引用替换为inline调用matlib$NAME, 同时对原方法创建对字段的引用
 * 当matchSuper = true时, 会匹配父类方法并创建他的override,
 * 对该方法的调用会被替换为对matlib$NAME或者是override method的调用
 * 创建的方法都将是public的,出于方便考虑
 * 无法创建static方法，不过这并不重要(你可以通过创建内部类来实现类似static content的东西)
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.METHOD})
public @interface UniqueMethod {
    boolean overrideSuper() default false;
}
