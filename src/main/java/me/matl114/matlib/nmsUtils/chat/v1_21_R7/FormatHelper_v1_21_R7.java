package me.matl114.matlib.nmsUtils.chat.v1_21_R7;

import me.matl114.matlib.nmsMirror.chat.FormatHelper;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;

import javax.annotation.Nullable;

import static me.matl114.matlib.nmsMirror.Import.ResourceLocation;
import static me.matl114.matlib.nmsMirror.Import.*;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.Style")
public interface FormatHelper_v1_21_R7 extends FormatHelper {

    default Object withFont(Object val, @RedirectType(ResourceLocation)Object font){
        return _withFont(val, newResource(font));
    }
    @MethodTarget
    @RedirectName("withFont")
    Object _withFont(Object val, @RedirectType(FontDescription)Object font);

    @ConstructorTarget
    @RedirectClass(FontAtlasSprite)
    Object newAtlas(@RedirectType(ResourceLocation)Object atlasId, @RedirectType(ResourceLocation)Object spriteId);

    @ConstructorTarget
    @RedirectClass(FontResource)
    Object newResource(@RedirectType(ResourceLocation)Object spriteId);
    //null ptr
    default Object newStyle0(@RedirectType(TextColor)Object color, Integer shadowColor, @Nullable Boolean bold,
                            @Nullable Boolean italic,
                            @Nullable Boolean underlined,
                            @Nullable Boolean strikethrough,
                            @Nullable Boolean obfuscated, @RedirectType(ClickEvent)Object clickevent, @RedirectType(HoverEvent)Object hoverEvent, String insertion, @RedirectType(ResourceLocation)Object resourceLocation){
        return newStyle00(color, shadowColor, bold, italic, underlined, strikethrough, obfuscated, clickevent, hoverEvent, insertion, resourceLocation == null ? null : newResource(resourceLocation));
    }
    @ConstructorTarget
    @RedirectClass(Style)
    public Object newStyle00(@RedirectType(TextColor)Object color, Integer shadowColor, @Nullable Boolean bold,
                            @Nullable Boolean italic,
                            @Nullable Boolean underlined,
                            @Nullable Boolean strikethrough,
                            @Nullable Boolean obfuscated, @RedirectType(ClickEvent)Object clickevent, @RedirectType(HoverEvent)Object hoverEvent, String insertion, @RedirectType(FontDescription)Object resourceLocation);
}
