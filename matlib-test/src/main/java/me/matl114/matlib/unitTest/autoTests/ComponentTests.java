package me.matl114.matlib.unitTest.autoTests;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import me.matl114.matlib.nmsUtils.ServerUtils;
import me.matl114.matlib.nmsUtils.chat.MutableBuilder;
import me.matl114.matlib.nmsUtils.chat.NMSComponentBuilder;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.chat.component.ComponentBuilder;
import me.matl114.matlib.utils.chat.component.DynamicComponentBuilder;
import me.matl114.matlib.utils.chat.component.PlaceholderedBuilder;
import me.matl114.matlib.utils.chat.componentCompiler.ComponentFormatParser;
import me.matl114.matlib.utils.chat.componentCompiler.MutableComponentAST;
import me.matl114.matlib.utils.chat.placeholder.ArgumentProvider;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import org.bukkit.Bukkit;

public class ComponentTests implements TestCase {
    @OnlineTest(name = "Component Compile Test")
    public void test_component() {
        var reg = TranslationRegistry.create(Key.key("test"));
        reg.register("me.matl114.test.message1", Locale.US, new MessageFormat("what the fuck"));
        reg.register("me.matl114.test.message1", Locale.CHINESE, new MessageFormat("你是一个一个"));
        GlobalTranslator.translator().addSource(reg);

        String t1 =
                "Hello {entity:ababab}, your score is {0}! && {text: &aGreen &cRed{click_suggest:ababab?{click_placeholder}}} §#FF0000 &x&6&6&6&6&6&6CustomColor {player_placeholder} is here. translate test{translatable:&a&lme.matl114.test.message1$fallback{hover_text:try_inner?}}{hover_text:&6&lcdcdcd{hover_placeholder}{player_placeholder}}";

        MutableComponentAST ast = ComponentFormatParser.compile(t1);
        var output = new StringBuilder();

        ast.walk(output);
        Debug.logger(output.toString());
        Map<String, String> arguments = Map.of(
                "0",
                "first parameter",
                "player_placeholder",
                "test-fucking-player-name",
                "hover_placeholder",
                "test-fucking-hover-placeholder",
                "click_placeholder",
                "test-fucking-click-placeholder");
        var parent = new ComponentBuilder();
        var builder = new PlaceholderedBuilder(parent, ArgumentProvider.of(arguments));
        ast.accept(builder);
        Bukkit.getServer().sendMessage(parent.getResult());
        //        for (int i=0;i<5;++i){
        //            var builder = (TextComponent) re.build(Parameter.wrap("666"));
        //            Bukkit.getServer().sendMessage(builder);
        //        }
        Debug.logger("test Dynamic build");
        var dynamic = new DynamicComponentBuilder();
        ast.accept(dynamic);
        Function<ArgumentProvider, Component> compFactory = dynamic.getFinalFunction();
        Component value = compFactory.apply(ArgumentProvider.of(arguments));
        Bukkit.getServer().sendMessage(value);
        long a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            compFactory.apply(ArgumentProvider.of(arguments));
        }
        long b = System.nanoTime();
        Debug.logger("check time cost", b - a);
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            var parent0 = new ComponentBuilder();
            var builder0 = new PlaceholderedBuilder(parent0, ArgumentProvider.of(arguments));
            ast.accept(builder0);
            parent0.getResult().build();
        }
        b = System.nanoTime();
        Debug.logger("check time cost 2", b - a);

        String t2 = "&6-> &7{energy} J/ {energy_total}J 已存储,桀桀桀{hover_text:&8{energy} J}";
        MutableComponentAST ast2 = ComponentFormatParser.compile(t2);

        var dynamic2 = new DynamicComponentBuilder();
        ast2.accept(dynamic2);
        Function<ArgumentProvider, Component> compFactory2 = dynamic2.getFinalFunction();
        Map<String, String> argument2 = Map.of("energy", "114", "energy_total", "5141919");
        Bukkit.getServer().sendMessage(compFactory2.apply(ArgumentProvider.of(argument2)));
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            compFactory2.apply(ArgumentProvider.of(argument2));
        }
        b = System.nanoTime();
        Debug.logger("check time cost 3", b - a);
        // test NMSComponent test

        Debug.logger("start test nms builder");
        var nmsBuilder = new NMSComponentBuilder();
        ast.accept(nmsBuilder);
        Function<ArgumentProvider, MutableBuilder> builderFactory = nmsBuilder.getFinalFunction();
        MutableBuilder chatBuilder = builderFactory.apply(ArgumentProvider.of(arguments));
        ;
        ServerUtils.broadCastMessage(chatBuilder.toNMS());
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            builderFactory.apply(ArgumentProvider.of(arguments));
        }
        b = System.nanoTime();
        Debug.logger("check time cost", b - a);

        var nmsbuidler2 = new NMSComponentBuilder();
        ast2.accept(nmsbuidler2);
        Function<ArgumentProvider, MutableBuilder> builderFactory2 = nmsbuidler2.getFinalFunction();
        MutableBuilder chatBuilder2 = builderFactory2.apply(ArgumentProvider.of(argument2));
        ;
        ServerUtils.broadCastMessage(chatBuilder2.toNMS());
        a = System.nanoTime();
        for (int i = 0; i < 100; ++i) {
            builderFactory2.apply(ArgumentProvider.of(argument2));
        }
        b = System.nanoTime();
        Debug.logger("check time cost 2", b - a);
    }

    @OnlineTest(name = "Translation conflict test")
    public void test_translation_key_conflict() {
        var reg = TranslationRegistry.create(Key.key("test"));
        GlobalTranslator.translator().addSource(reg);

        reg.register("item.minecraft.apple", Locale.US, new MessageFormat("林檎"));
        reg.register("item.minecraft.apple", Locale.CHINESE, new MessageFormat("你是一个一个"));
        Bukkit.getServer().sendMessage(Component.translatable("item.minecraft.apple"));
        reg.unregister("item.minecraft.apple");
        Bukkit.getServer().sendMessage(Component.translatable("item.minecraft.apple"));
    }

    // 我们实现了直接转nms的方法 这下就没有paper adventure的问题了， 而且在高版本这应该是恒同的
    // todo 检测当服务端和客户端同时有相同键的时候会发生什么
}
