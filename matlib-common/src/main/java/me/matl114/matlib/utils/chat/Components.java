package me.matl114.matlib.utils.chat;

import me.matl114.matlib.utils.chat.component.ComponentBuilder;
import me.matl114.matlib.utils.chat.component.DynamicComponentBuilder;
import me.matl114.matlib.utils.chat.componentCompiler.ComponentFormatParser;
import me.matl114.matlib.utils.chat.placeholder.ArgumentedValue;
import net.kyori.adventure.text.Component;

public class Components {
    public static ArgumentedValue<Component> compile(String input) {
        var ast = ComponentFormatParser.compile(input);
        var builder = new DynamicComponentBuilder();
        ast.accept(builder);
        return builder.getFinalFunction()::apply;
    }

    public static net.kyori.adventure.text.ComponentBuilder compileStatic(String input) {
        var ast = ComponentFormatParser.compile(input);
        var builder = new ComponentBuilder();
        ast.accept(builder);
        return builder.getResult();
    }
}
