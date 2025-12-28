package me.matl114.matlib.utils.reflect.mixImpl.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.matl114.matlib.common.lang.annotations.NotRecommended;

/**
 * 这个方法提供了从接口处访问子类static方法的方法
 * 将会生成返回static字段的代码
 * 将不会转换字节码
 * 不建议使用
 * 因为访问接口方法最终需要一个实例
 * 建议使用Descriptor创建MultiDescriptive实现调用
 */
@NotRecommended
@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.METHOD})
public @interface StaticMethodAccess {}
