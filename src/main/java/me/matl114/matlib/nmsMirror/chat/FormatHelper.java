package me.matl114.matlib.nmsMirror.chat;

import lombok.val;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.nmsMirror.impl.NMSEntity;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.ConstructorTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import javax.annotation.Nullable;

import java.net.URI;

import static me.matl114.matlib.nmsMirror.Import.*;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.Style")
public interface FormatHelper extends TargetDescriptor {
    @MethodTarget
    Object withColor(Object val, int rgb);

    @MethodTarget
    Object withColor(Object val, @RedirectType("Lnet/minecraft/ChatFormatting;")Object chat);

    @MethodTarget
    Object withBold(Object val, Boolean bold);

    @MethodTarget
    Object withItalic(Object val, Boolean italic);

    @MethodTarget
    Object withUnderlined(Object val, Boolean underlined);

    @MethodTarget
    Object withStrikethrough(Object val, Boolean strikethrough);

    @MethodTarget
    Object withObfuscated(Object val, Boolean obfuscated);

    // some thing related to clickEvent
    @MethodTarget
    Object withInsertion(Object val, String insertion);

    @MethodTarget
    Object withFont(Object val, @RedirectType(ResourceLocation)Object font);

    @MethodTarget
    Object applyTo(Object val, @RedirectType(Style)Object parent);

    @MethodTarget
    boolean isBold(Object val);

    @MethodTarget
    boolean isItalic(Object val);

    @MethodTarget
    boolean isStrikethrough(Object val);

    @MethodTarget
    boolean isUnderlined(Object val);

    @MethodTarget
    boolean isObfuscated(Object val);
    @MethodTarget
    boolean isEmpty(Object val);

    @MethodTarget
    Object getClickEvent(Object val);

    @MethodTarget
    Object getHoverEvent(Object val);

    @MethodTarget
    String getInsertion(Object val);

    @MethodTarget
    Object getFont(Object val);

    @MethodTarget
    Object getColor(Object val);

    @FieldTarget
    @RedirectClass(TextColor)
    @RedirectName("formatGetter")
    Enum<?> textColor$formatGetter(Object val);

    @MethodTarget
    @RedirectClass(TextColor)
    @RedirectName("getValue")
    int textColor$getValue(Object val);

    @MethodTarget(isStatic = true)
    @RedirectClass(TextColor)
    @RedirectName("fromRgb")
    Object textcolorFromRgb(int rgb);

    @MethodTarget(isStatic = true)
    @RedirectClass(TextColor)
    @RedirectName("fromLegacyFormat")
    Object textcolorFromChatFormat(@RedirectType(ChatFormatting) Object chatFormat);

    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R3)
    default Object newStyle(@RedirectType(TextColor)Object color, @Nullable Boolean bold,
                    @Nullable Boolean italic,
                    @Nullable Boolean underlined,
                    @Nullable Boolean strikethrough,
                    @Nullable Boolean obfuscated, @RedirectType(ClickEvent)Object clickevent, @RedirectType(HoverEvent)Object hoverEvent, String insertion, @RedirectType(ResourceLocation)Object resourceLocation){
        return newStyle0(color, null, bold, italic, underlined, strikethrough, obfuscated, clickevent, hoverEvent, insertion, resourceLocation );
    }
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R3, below = true)
    @Internal
    public Object newStyle0(@RedirectType(TextColor)Object color, Integer shadowColor, @Nullable Boolean bold,
                            @Nullable Boolean italic,
                            @Nullable Boolean underlined,
                            @Nullable Boolean strikethrough,
                            @Nullable Boolean obfuscated, @RedirectType(ClickEvent)Object clickevent, @RedirectType(HoverEvent)Object hoverEvent, String insertion, @RedirectType(ResourceLocation)Object resourceLocation);

    default Object copy(Object style0){
        return applyTo(style0, ChatEnum.STYLE_EMPTY_COPY);
    }

    default Object createClickEvent(ClickEvent.Action adventure, String content){
        return switch (adventure){
            case CHANGE_PAGE -> newChangePage(Integer.parseInt(content));
            case COPY_TO_CLIPBOARD -> newCopyToClipboard(content);
            case OPEN_FILE -> newOpenFile(content);
            case OPEN_URL -> newOpenUrl(URI.create(content));
            case RUN_COMMAND -> newRunCommand(content);
            case SUGGEST_COMMAND -> newSuggestCommand(content);
        };
    }
    @ConstructorTarget
    @RedirectClass("Lnet/minecraft/network/chat/ClickEvent$ChangePage;")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    public Object newChangePage(int val);
    @ConstructorTarget
    @RedirectClass("Lnet/minecraft/network/chat/ClickEvent$CopyToClipboard;")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    public Object newCopyToClipboard(String val);
    @ConstructorTarget
    @RedirectClass("Lnet/minecraft/network/chat/ClickEvent$OpenFile;")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    public Object newOpenFile(String val);
    @ConstructorTarget
    @RedirectClass("Lnet/minecraft/network/chat/ClickEvent$OpenUrl;")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    public Object newOpenUrl(URI val);
    @ConstructorTarget
    @RedirectClass("Lnet/minecraft/network/chat/ClickEvent$RunCommand;")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    public Object newRunCommand(String val);
    @ConstructorTarget
    @RedirectClass("Lnet/minecraft/network/chat/ClickEvent$SuggestCommand;")
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    public Object newSuggestCommand(String val);


    @ConstructorTarget
    @RedirectClass(ClickEvent)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    default Object newClickEvent(@RedirectType(ClickEventAction)Object action, String value){
        return createClickEvent(ChatUtils.fromNMSClickEvent(action), value);
    }

    default Object createHoverEventByRaw(HoverEvent.Action action, Object rawContent){
        if(action == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_ITEM){
            return createShowItem(rawContent);
        }else if(action == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_TEXT){
            return createShowText(rawContent);
        }else if(action == net.kyori.adventure.text.event.HoverEvent.Action.SHOW_ENTITY){
            return createShowEntity(rawContent);
        }else{
            throw new RuntimeException("HoverEvent.Action not supported: " + action.toString());
        }
    }
    @RedirectClass(HoverEventShowItem)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    default Object createShowItem(@RedirectType(ItemStack) Object rawContent){
        return newHoverEvent(ChatUtils.toNMSHoverAction(net.kyori.adventure.text.event.HoverEvent.Action.SHOW_ITEM), newHoverEventItemInfo(rawContent));
    }
    @RedirectClass(HoverEventShowText)
    @ConstructorTarget
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = true)
    default Object createShowText(@RedirectType(ChatComponent) Object textContent){
        return newHoverEvent(ChatUtils.toNMSHoverAction(net.kyori.adventure.text.event.HoverEvent.Action.SHOW_TEXT), textContent);
    }
    default Object createShowEntity(Object entityContent){
        return NMSEntity.ENTITY.createHoverEvent(entityContent);
    }

    @ConstructorTarget
    @RedirectClass(HoverEvent)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    Object newHoverEvent(@RedirectType(HoverEventAction)Object action, Object content);

    @ConstructorTarget
    @RedirectClass(HoverEventItemStackInfo)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R4, below = false)
    Object newHoverEventItemInfo(@RedirectType(ItemStack)Object itemStack);
}
