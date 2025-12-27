package me.matl114.matlib.utils.reflect.descriptor.buildTools;

import me.matl114.matlib.common.lang.annotations.ConstVal;
import me.matl114.matlib.common.lang.annotations.Note;

/**
 * descriptor 用于让使用者通过注解描述目标类的方法和字段,并使用paper reobf工具对运行时类进行反混淆以匹配，同时生成快速调用的方法体以支持高效反射
 * descriptor 会扫描所有继承树中的所有方法和字段,包括父类static,
 * descriptor 会以如下顺序扫描： 当前类->当前类的接口->当前类的父类-> 递归的查找, 当进行匹配时,匹配顺序靠前的(这一般代表这是被override的,或者这是一个interface method,需要通过InvokeInterface调用)
 * descriptor需要被@Descriptive标注,其中type = clazz.getName()仅作为默认时参考用,
 * descriptor 中的空闲和未匹配方法会被实现为抛出NotImplement异常
 * descriptor 中的getTargetClass()会被实现为返回目标类
 * descriptor 中标注为@FieldTarget的字段会被关联到一个字段, 会通过名字搜索字段(支持paper deobf),(aGetter,aSetter -> 字段a, 除非存在@RedirectName强制指定字段名称)如果方法存在@RedirectType则会强制指定type
 * @FieldTarget 中非static字段的getter应该是getter(instance), setter应该是setter(instace,value), static字段的getter应该是getter(), setter应该是(value),
 * @FieldTarget 中需要显示的通过isStatic标注是否是static字段
 * @FieldTarget 并不会查验目标字段是否final或者setter/getter参数类型是否匹配或者参数数量是否匹配,仅会对指定参数进行正常class cast,
 * descriptor 中标记为@MethodTarget的字段会被关联到一个方法, 会通过名字和参数类型搜索方法(支持paper deobf),(name = methodName, 除非存在@RedirectName强制指定方法名称)(参数列表通过第(static?0:1)个参数类型开始进行匹配,参数中如果存在@RedirectType则使用该注解中的类型代替参数类型), 方法体上的@RedirectType并不会起到作用
 * @MethodTarget 会进行类型匹配和强制转换,可能会抛出ClassCastException。
 * @MethodTarget 会尽可能的让返回值匹配,
 * descriptor 中标记为@ConstructorTarget的字段会关联到一个构造函数,匹配方式和生成方式同MethodTarget相同,名字随意
 */
@Note("please do not override access method frequently, or builder may locate the wrong method")
public interface TargetDescriptor {
    @ConstVal
    public Class getTargetClass();
}
