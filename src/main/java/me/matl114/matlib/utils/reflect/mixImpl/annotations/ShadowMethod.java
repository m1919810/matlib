package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.matl114.matlib.common.lang.annotations.Note;

/**
 * 捕获方法
 * NAME会受到@RedirectName重定向
 * PARAM会受到@RedirectType重定向
 * 当标注在interface的method上时,会在目标类中窗内捕获形如NAME的字段,并尝试将原方法的全部引用替换为对目标方法的调用, 同时对原方法创建对目标方法的安全访问(?)
 * 不会捕获override的方法，也就是说,假如你调用了一个方法A,同时你使用@UniqueMethod创建了方法A的覆写方法,那么该方法不会调用覆写方法,而是调用super.().
 * 你可以通过在@UniqueMethod中调用shadow方法来达成在method a中调用super.a()的操作
 * 目标的static状态应该和方法的static状态相匹配!static方法的读写方法在接口中可以使用@StaticMethodAccess注解实现
 * 创建的字段都将是非private的!(至少目前是)
 *
 * 所定义的类可以用@RedirectClass重定向,但是需要加上this作为首参数,而且最好设置create = false以减少字节码数量
 */
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.METHOD})
public @interface ShadowMethod {
    @Note(
            "This parameter controls whether create impl for the method, if NOT, the method will be used as PlaceHolder of target Method, and method body will be replaced by MixImplException.placeholder() if not default")
    boolean create() default false;
}
