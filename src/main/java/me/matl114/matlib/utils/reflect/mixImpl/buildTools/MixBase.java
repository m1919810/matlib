package me.matl114.matlib.utils.reflect.mixImpl.buildTools;

import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.common.lang.annotations.Protected;

public interface MixBase {

    @Internal
    @Protected
    @Note("作为<clinit>方法体,你可以覆写他")
    default void clinit() {}

    @Internal
    @Protected
    @Note(
            value =
                    "占位符,用于生成CHECKCAST指令 你需要使用a = castInsn(a, path) -> 生成... CHECKCAST cls ...., 其中LOAD和STORE会沿用原方法调用所分配的指令. 尽量不要在参数或者返回值处嵌套表达式,否则可能生成失败",
            extra = {"当自动生成lambda表达式时,其参数类型可能不受控,如果有必要,要在lambda捕获的final var中增加cast,或者尽量少用lambda-expression"})
    static Object castInsn(Object tar, String type) {
        throw MixImplException.internal();
        //        @Note("This is a sample of how to use inner class as Static Content, instead of creating static fields
        // in Impl")
        //        Object ret = StaticContents.c();
        //        return ret;
    }

    @Internal
    @Protected
    @Note(
            "占位符,用于生成INSTANCEOF指令 你需要使用boolean x = instanceofInsn(a, path) -> 生成... INSTANCEOF cls ...., 其中LOAD和STORE会沿用原方法调用所分配的指令. 尽量不要在参数或者返回值处嵌套表达式,否则可能生成失败")
    static boolean instanceofInsn(Object tar, String type) {
        throw MixImplException.internal();
        //        @Note("This is a sample of how to use inner class as Static Content, instead of creating static fields
        // in Impl")
        //        Object astr = StaticContents.a;
        //        StaticContents.b = !StaticContents.b;
        //        return StaticContents.b;
    }
    //    @Note("This is a static fields sample, you can create your own sample in your own interface class, instead of
    // creating static fields in Impl")
    //    static class StaticContents {
    //        static String a = "I am fine";
    //        static boolean b = true;
    //        static MixBase c(){
    //            return null;
    //        }
    //
    //    }
    @Note("placeholders of common NMS methods")
    static class NMSMethodPlaceHolders {}
}
