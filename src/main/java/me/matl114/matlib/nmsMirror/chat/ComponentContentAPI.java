package me.matl114.matlib.nmsMirror.chat;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.Optional;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.ComponentContents")
public interface ComponentContentAPI extends TargetDescriptor {
    @MethodTarget
    Optional visit(Object val, @RedirectType(FormattedText$ContentConsumer) Object val2);

    @CastCheck(TranslatableContents)
    boolean isTranslatable(Object val);

    @MethodTarget
    @RedirectClass(TranslatableContents)
    @RedirectName("getKey")
    String translatable$getKey(Object val);

    @MethodTarget
    @RedirectClass(TranslatableContents)
    @RedirectName("getFallback")
    String translatable$getFallback(Object val);

    @FieldTarget
    @RedirectClass(TranslatableContents)
    @RedirectName("fallbackSetter")
    void translatable$setFallback(Object val, String fallback);

    @MethodTarget
    @RedirectClass(TranslatableContents)
    @RedirectName("getArgs")
    Object[] translatable$getArgs(Object val);

    @CastCheck(LiteralContents)
    boolean isLiteral(Object val);

    @ConstructorTarget
    @RedirectClass(TranslatableContents)
    Object newTranslatable(String key, String fallback, Object[] args);

    @MethodTarget
    @RedirectClass(LiteralContents)
    String text(Object val);

    @ConstructorTarget
    @RedirectClass(LiteralContents)
    Object newLiteral(String finalValue);
}
