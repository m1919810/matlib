package me.matl114.matlib.nmsUtils.chat;

import me.matl114.matlib.utils.chat.componentCompiler.ComponentFormatParser;
import me.matl114.matlib.utils.chat.placeholder.ArgumentedValue;

public class NMSComponents {
    public static ArgumentedValue<MutableBuilder> compile(String input) {
        var ast = ComponentFormatParser.compile(input);
        var builder = new NMSComponentBuilder();
        ast.accept(builder);
        return builder.getFinalFunction()::apply;
    }

    public static MutableBuilder compileStatic(String input) {
        var ast = ComponentFormatParser.compile(input);
        var builder = new NMSComponentBuilder();
        ast.accept(builder);
        return builder.getFinalFunction().apply(null);
    }
}
