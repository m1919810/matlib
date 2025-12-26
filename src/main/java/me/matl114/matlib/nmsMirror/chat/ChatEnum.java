package me.matl114.matlib.nmsMirror.chat;

import static me.matl114.matlib.nmsMirror.Utils.*;

import java.lang.reflect.Field;
import java.util.List;
import me.matl114.matlib.nmsMirror.impl.NMSChat;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.internel.ObfManager;

public class ChatEnum {
    public static final Object STYLE_EMPTY;
    public static final Object STYLE_EMPTY_COPY;
    public static final Object STYLE_LEGACY_EMPTY;
    public static final Object STYLE_RESET;
    // remove because of shit
    //    public static final Object FONT_DEFAULT ;
    public static final Object PLAIN_TEXT_EMPTY;

    static {
        Class<?> clazz;
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.network.chat.Style");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        List<Field> styleFields = ReflectUtils.getAllFieldsRecursively(clazz);
        STYLE_EMPTY = matchName(styleFields, "EMPTY");

        STYLE_LEGACY_EMPTY = NMSChat.FORMAT.withItalic(STYLE_EMPTY, false);
        STYLE_EMPTY_COPY = NMSChat.FORMAT.newStyle(null, null, null, null, null, null, null, null, null, null);
        Object val = NMSChat.FORMAT.withBold(STYLE_EMPTY, false);
        val = NMSChat.FORMAT.withItalic(val, false);
        val = NMSChat.FORMAT.withUnderlined(val, false);
        val = NMSChat.FORMAT.withStrikethrough(val, false);
        val = NMSChat.FORMAT.withObfuscated(val, false);
        STYLE_RESET = val;
        // versioned error
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.network.chat.contents.PlainTextContents");
        } catch (Throwable e) {
            try {
                clazz = ObfManager.getManager().reobfClass("net.minecraft.network.chat.ComponentContents");
            } catch (Throwable e1) {
                throw new RuntimeException(e1);
            }
        }
        List<Field> fields0 = ReflectUtils.getAllFieldsRecursively(clazz);
        PLAIN_TEXT_EMPTY = matchName(fields0, "EMPTY");
    }
}
