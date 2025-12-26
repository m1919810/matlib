package me.matl114.matlib.nmsMirror.chat;

import static me.matl114.matlib.nmsMirror.Import.*;

import com.google.gson.JsonElement;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import net.kyori.adventure.text.Component;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.Component")
public interface ComponentHelper extends TargetDescriptor {
    @CastCheck(ChatComponent)
    boolean isComponent(Object comp);

    @MethodTarget
    Stream<? extends Iterable<?>> stream(Iterable<?> comp);

    @MethodTarget
    Object getStyle(Iterable<?> comp);

    @MethodTarget
    Object getContents(Iterable<?> comp);

    @MethodTarget
    String getString(Iterable<?> comp);

    @MethodTarget
    List<Iterable<?>> getSiblings(Iterable<?> comp);

    @MethodTarget
    Iterable<?> copy(Iterable<?> comp);

    @MethodTarget
    List<?> toFlatList(Iterable<?> comp);

    @MethodTarget(isStatic = true)
    Iterable<?> literal(String string);

    @MethodTarget(isStatic = true)
    Iterable<?> translatable(String key);

    @MethodTarget(isStatic = true)
    Iterable<?> translatableWithFallback(String key, @Nullable String fallback);

    @MethodTarget(isStatic = true)
    Iterable<?> empty();

    @MethodTarget(isStatic = true)
    Iterable<?> keybind(String string);

    @MethodTarget(isStatic = true)
    Iterable<?> score(String name, String objective);

    @MethodTarget(isStatic = true)
    Iterable<?> nbt(
            String rawPath, boolean interpret, Optional<?> separator, @RedirectType(DataSource) Object dataSource);

    //    @MethodTarget(isStatic = true)
    //    Iterable<?> selector(String pattern, Optional<?> separator);

    @CastCheck(AdventureComponent)
    boolean isAdventure(Iterable<?> comp);

    @CastCheck(MutableComponent)
    boolean isMutableComp(Iterable<?> comp);

    @MethodTarget(isStatic = true)
    @RedirectClass(MutableComponent)
    Iterable<?> create(@RedirectType(ComponentContents) Object content);

    @RedirectClass(MutableComponent)
    @MethodTarget
    Iterable<?> append(Iterable<?> thi, @RedirectType(ChatComponent) Iterable<?> text);

    @RedirectClass(MutableComponent)
    @MethodTarget
    Iterable<?> withStyle(Iterable<?> thi, @RedirectType(Style) Object style);

    @RedirectClass(MutableComponent)
    @MethodTarget
    Iterable<?> setStyle(Iterable<?> thi, @RedirectType(Style) Object style);

    @RedirectClass(MutableComponent)
    @FieldTarget
    @RedirectName("siblingsSetter")
    void forceReplaceSiblings(Iterable<?> thi, List<Iterable<?>> newList);

    // remove because of version
    //    @RedirectClass(MutableComponent)
    //    @MethodTarget
    //    Iterable<?> withColor(Iterable<?> thi, int color);

    @MethodTarget
    @RedirectClass(AdventureComponent)
    Iterable<?> deepConverted(Iterable<?> adv);

    @ConstructorTarget
    @RedirectClass(AdventureComponent)
    Iterable<?> newAdventure(Component comp);

    @MethodTarget
    @RedirectClass(AdventureComponent)
    Component adventure$component(Iterable<?> adv);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    String toJson(@RedirectType(ChatComponent) Iterable<?> comp);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    JsonElement toJsonTree(@RedirectType(ChatComponent) Iterable<?> comp);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    Iterable<?> fromJson(String json);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    Iterable<?> fromJson(JsonElement json);

    @GetType(FormattedText$ContentConsumer)
    Class<?> getContentConsumerType();
}
