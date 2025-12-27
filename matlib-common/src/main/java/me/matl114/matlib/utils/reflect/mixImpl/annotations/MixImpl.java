package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 这是一款简易字节码工具,用于制作某一类的子类,
 * 功能注解有
 * @Shadow 捕获子类的非private方法和字段
 * @Unique 定义新的字段和方法
 * @constructor 用于构造new方法，
 * 你可以将一个abstract class 或者class 或者interface当作一个MixImpl主体
 * 当标注在class时,创建的类会继承class的全部接口，否则标注在inteface上,会继承自身 这些无需显式指出!
 * 你在创建类的builder参数里还可以显式指派更多的接口
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MixImpl {
    String subClass() default "java.lang.Object";

    String[] interfaces() default {};
}
