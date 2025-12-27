package me.matl114.matlib.nmsUtils.chat.v1_21_R7;

import static me.matl114.matlib.nmsMirror.Import.*;
import static me.matl114.matlib.nmsMirror.Import.ResourceLocation;

import javax.annotation.Nullable;

import me.matl114.matlib.nmsMirror.Import;
import me.matl114.matlib.nmsMirror.chat.FormatHelper;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.Style")
public interface FormatHelper_v1_21_R7 extends FormatHelper {

    default Object withFont(Object val, @RedirectType(Import.ResourceLocation) Object font) {
        return _withFont(val, newResource(font));
    }

    @MethodTarget
    @RedirectName("withFont")
    Object _withFont(Object val, @RedirectType(Import.FontDescription) Object font);

    @ConstructorTarget
    @RedirectClass(Import.FontAtlasSprite)
    Object newAtlas(@RedirectType(Import.ResourceLocation) Object atlasId, @RedirectType(Import.ResourceLocation) Object spriteId);

    @ConstructorTarget
    @RedirectClass(Import.FontResource)
    Object newResource(@RedirectType(Import.ResourceLocation) Object spriteId);
    // null ptr
    default Object newStyle0(
            @RedirectType(Import.TextColor) Object color,
            Integer shadowColor,
            @Nullable Boolean bold,
            @Nullable Boolean italic,
            @Nullable Boolean underlined,
            @Nullable Boolean strikethrough,
            @Nullable Boolean obfuscated,
            @RedirectType(Import.ClickEvent) Object clickevent,
            @RedirectType(Import.HoverEvent) Object hoverEvent,
            String insertion,
            @RedirectType(Import.ResourceLocation) Object resourceLocation) {
        return newStyle00(
                color,
                shadowColor,
                bold,
                italic,
                underlined,
                strikethrough,
                obfuscated,
                clickevent,
                hoverEvent,
                insertion,
                resourceLocation == null ? null : newResource(resourceLocation));
    }

    @ConstructorTarget
    @RedirectClass(Import.Style)
    public Object newStyle00(
            @RedirectType(Import.TextColor) Object color,
            Integer shadowColor,
            @Nullable Boolean bold,
            @Nullable Boolean italic,
            @Nullable Boolean underlined,
            @Nullable Boolean strikethrough,
            @Nullable Boolean obfuscated,
            @RedirectType(Import.ClickEvent) Object clickevent,
            @RedirectType(Import.HoverEvent) Object hoverEvent,
            String insertion,
            @RedirectType(Import.FontDescription) Object resourceLocation);
}
