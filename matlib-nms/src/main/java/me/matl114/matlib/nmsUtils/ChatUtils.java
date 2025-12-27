package me.matl114.matlib.nmsUtils;

import static me.matl114.matlib.nmsMirror.impl.NMSChat.*;
import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Type.*;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import me.matl114.matlib.nmsMirror.Utils;
import me.matl114.matlib.nmsMirror.chat.ChatEnum;
import me.matl114.matlib.nmsMirror.impl.NMSChat;
import me.matl114.matlib.nmsUtils.chat.StyleBuilder;
import me.matl114.matlib.utils.chat.EnumFormat;
import me.matl114.matlib.utils.reflect.ASMUtils;
import me.matl114.matlib.utils.reflect.ByteCodeUtils;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import me.matl114.matlib.utils.reflect.asm.CustomClassLoader;
import me.matl114.matlib.utils.reflect.internel.ObfManager;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Type;

public class ChatUtils {
    private static final Pattern INCREMENTAL_PATTERN = Pattern.compile(
            "(§[0-9a-fk-orx])|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[§ \\n]|$))))|(\\n)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern FORMAT_PATTERN = Pattern.compile("(§[0-9a-fk-orx])|(\\n)", Pattern.CASE_INSENSITIVE);

    //  private static final Pattern FORMAT_PATTERN = Pattern.compile("(§[0-9a-fk-orx])|(\\n)",
    // Pattern.CASE_INSENSITIVE);
    public static Iterable<?> deserializeLegacy(String value) {
        return deserializeLegacyNoUrl(value);
    }

    private static Iterable<?> deserializeLegacyNoUrl(String value) {
        if (value == null) {
            return NMSChat.CHATCOMPONENT.empty();
        }
        Iterable<?> base = NMSChat.CHATCOMPONENT.empty();
        // Object currentStyle = ChatEnum.STYLE_EMPTY;
        StyleBuilder currentStyle = StyleBuilder.formatFrom(ChatEnum.STYLE_EMPTY);
        Matcher matcher = FORMAT_PATTERN.matcher(value);
        String match = null;
        StringBuilder hexColor = null;
        int currentIndex = 0;
        boolean hasReset = false;
        boolean needsAdd = false;
        find_any:
        while (matcher.find()) {
            int groupId = 0;
            while ((match = matcher.group(++groupId)) == null) {}
            int index = matcher.start(groupId);
            if (index > currentIndex) {
                needsAdd = false;
                Iterable<?> addition = NMSChat.CHATCOMPONENT.literal(value.substring(currentIndex, index));
                addition = NMSChat.CHATCOMPONENT.setStyle(addition, currentStyle.toNMS());
                currentIndex = index;
                NMSChat.CHATCOMPONENT.append(base, addition);
            }
            switch (groupId) {
                case 1:
                    char c = match.toLowerCase(java.util.Locale.ENGLISH).charAt(1);
                    if (c == 'x') {
                        hexColor = new StringBuilder("#");
                    } else if (hexColor != null) {
                        hexColor.append(c);
                        if (hexColor.length() == 7) {
                            hasReset = solveLegacyFormatString(currentStyle, hexColor.toString(), hasReset);

                            hexColor = null;
                        }
                    } else {
                        hasReset = solveLegacyFormatString(currentStyle, match, hasReset);
                    }
                    needsAdd = true;
                    break;
                case 2:
                    if (needsAdd) {
                        Iterable<?> addition = NMSChat.CHATCOMPONENT.literal(value.substring(currentIndex, index));
                        addition = NMSChat.CHATCOMPONENT.setStyle(addition, currentStyle.toNMS());
                        NMSChat.CHATCOMPONENT.append(base, addition);
                    }
                    // 换行 means end
                    return base;
            }
            currentIndex = matcher.end(groupId);
        }
        int len = value.length();
        if (currentIndex < value.length() || needsAdd) {
            Iterable<?> addition = NMSChat.CHATCOMPONENT.literal(value.substring(currentIndex, len));
            addition = NMSChat.CHATCOMPONENT.setStyle(addition, currentStyle.toNMS());
            NMSChat.CHATCOMPONENT.append(base, addition);
        }
        return base;
    }

    public static String serializeToLegacy(Iterable<?> value) {
        if (value == null) {
            return "";
        }
        if (NMSChat.CHATCOMPONENT.isAdventure(value)) {
            value = NMSChat.CHATCOMPONENT.deepConverted(value);
        }
        StringBuilder out = new StringBuilder();
        boolean hasFormat = false;
        for (Object sub0 : value) {
            Iterable<?> sub = (Iterable<?>) sub0;
            Object style = NMSChat.CHATCOMPONENT.getStyle(sub);
            Object contents = NMSChat.CHATCOMPONENT.getContents(sub);
            hasFormat = getLegacyFormatString(out, style, contents == ChatEnum.PLAIN_TEXT_EMPTY, hasFormat);
            Object visitor = CREATOR.createVisitorImpl((str) -> {
                out.append(str);
                return Optional.empty();
            });
            NMSChat.COMP_CONTENT.visit(contents, visitor);
        }
        return out.toString();
    }

    public static String getPlainString(Iterable<?> value) {
        if (value == null) {
            return "";
        }
        if (NMSChat.CHATCOMPONENT.isAdventure(value)) {
            value = NMSChat.CHATCOMPONENT.deepConverted(value);
        }
        StringBuilder out = new StringBuilder();
        for (Object sub0 : value) {
            Iterable<?> sub = (Iterable<?>) sub0;
            Object contents = NMSChat.CHATCOMPONENT.getContents(sub);
            Object visitor = CREATOR.createVisitorImpl((str) -> {
                out.append(str);
                return Optional.empty();
            });
            NMSChat.COMP_CONTENT.visit(contents, visitor);
        }
        return out.toString();
    }

    private static final MethodHandle lambdaFactory$ContentVisitor$1;

    public static interface VisitorCreator {
        Object createVisitorImpl(Function<String, Optional<?>> delegate);
    }
    // private static final Class<?> visitorImpl;
    private static final VisitorCreator CREATOR;

    private static synchronized VisitorCreator c(Class<?> itf) throws Throwable {
        Method method = itf.getMethods()[0];
        Preconditions.checkArgument(Modifier.isAbstract(method.getModifiers()));
        var cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        String implName = ChatUtils.class.getName() + "VisitorCreatorImpl";
        String implPath = implName.replace(".", "/");
        String interfacePath = getInternalName(VisitorCreator.class);
        cw.visit(V21, ACC_PUBLIC | ACC_SUPER, implPath, null, getInternalName(Object.class), new String[] {interfacePath
        });
        ASMUtils.generateEmptyInit(cw, null);
        Method target = VisitorCreator.class.getMethod("createVisitorImpl", Function.class);
        var mv = ASMUtils.createOverrideMethodImpl(cw, target);
        {
            mv.visitCode();
            mv.visitVarInsn(ALOAD, 1);
            mv.visitInvokeDynamicInsn(
                    method.getName(),
                    "(Ljava/util/function/Function;)" + ByteCodeUtils.toJvmType(itf),
                    new Handle(
                            H_INVOKESTATIC,
                            "java/lang/invoke/LambdaMetafactory",
                            "metafactory",
                            "(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;",
                            false),
                    new Object[] {
                        Type.getType(method),
                        new Handle(
                                H_INVOKEINTERFACE,
                                "java/util/function/Function",
                                "apply",
                                "(Ljava/lang/Object;)Ljava/lang/Object;",
                                true),
                        Type.getType("(Ljava/lang/String;)Ljava/util/Optional;")
                    });
            mv.visitInsn(ARETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
        }
        cw.visitEnd();
        byte[] code = cw.toByteArray();
        synchronized (CustomClassLoader.getInstance()) {
            CustomClassLoader.getInstance().defineAccessClass(implName, code);
            Class<?> instance = CustomClassLoader.getInstance().loadAccessClass(implName);
            return (VisitorCreator) instance.getDeclaredConstructor().newInstance();
        }
    }

    static {
        Method method = Objects.requireNonNull(
                ReflectUtils.getMethodPrivate(ChatUtils.class, "contentVisitor$1", StringBuilder.class, String.class));
        lambdaFactory$ContentVisitor$1 =
                LambdaUtils.createLambdaWithOuterArgument(NMSChat.CHATCOMPONENT.getContentConsumerType(), method, 1);
        //        Class<?> visitorImpl0;
        //        try{
        //            visitorImpl0 = a(CHATCOMPONENT.getContentConsumerType());
        //        }catch (Throwable e){
        //            throw new RuntimeException(e);
        //        }
        //        visitorImpl = visitorImpl0;
        VisitorCreator instance;
        try {
            instance = c(NMSChat.CHATCOMPONENT.getContentConsumerType()); // b(visitorImpl0);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        CREATOR = instance;
    }

    private static Optional<?> contentVisitor$1(StringBuilder builder, String asString) {
        builder.append(asString);
        return Optional.empty();
    }

    @Deprecated
    public static Object solveLegacyFormatString(Object currentStyle, String singleFormat, boolean hasResetFlag) {
        if (singleFormat.length() == 2) {
            char c = singleFormat.toLowerCase(java.util.Locale.ENGLISH).charAt(1);
            EnumFormat format = EnumFormat.getFormat(c);
            if (format.isFormat() && format != EnumFormat.RESET) {
                switch (format) {
                    case EnumFormat.BOLD:
                        currentStyle = NMSChat.FORMAT.withBold(currentStyle, Boolean.TRUE);
                        break;
                    case EnumFormat.ITALIC:
                        currentStyle = NMSChat.FORMAT.withItalic(currentStyle, Boolean.TRUE);
                        break;
                    case EnumFormat.STRIKETHROUGH:
                        currentStyle = NMSChat.FORMAT.withStrikethrough(currentStyle, Boolean.TRUE);
                        break;
                    case EnumFormat.UNDERLINE:
                        currentStyle = NMSChat.FORMAT.withUnderlined(currentStyle, Boolean.TRUE);
                        break;
                    case EnumFormat.OBFUSCATED:
                        currentStyle = NMSChat.FORMAT.withObfuscated(currentStyle, Boolean.TRUE);
                        break;
                    default:
                        throw new AssertionError("Unexpected message format" + format + " from " + c);
                }
                return currentStyle;
            } else {
                return setColorFrom(currentStyle, hasResetFlag, format);
            }
        } else {
            String hex = singleFormat.replaceAll("[&§#x]", "");
            if (hex.length() == 6) {
                return NMSChat.FORMAT.withColor(ChatEnum.STYLE_RESET, parseHexNoPrefix(hex));
            } else {
                throw new IllegalArgumentException("No Such Format " + singleFormat);
            }
        }
    }

    public static boolean solveLegacyFormatString(StyleBuilder builder, String singleFormat, boolean hasReset) {
        if (singleFormat.length() == 2) {
            char c = singleFormat.toLowerCase(java.util.Locale.ENGLISH).charAt(1);
            EnumFormat format = EnumFormat.getFormat(c);
            if (format.isFormat() && format != EnumFormat.RESET) {
                switch (format) {
                    case EnumFormat.BOLD:
                        builder.bold(Boolean.TRUE);
                        break;
                    case EnumFormat.ITALIC:
                        builder.italic(Boolean.TRUE);
                        break;
                    case EnumFormat.STRIKETHROUGH:
                        builder.strikethrough(Boolean.TRUE);
                        break;
                    case EnumFormat.UNDERLINE:
                        builder.underlined(Boolean.TRUE);
                        break;
                    case EnumFormat.OBFUSCATED:
                        builder.obfuscated(Boolean.TRUE);
                        break;
                    default:
                        throw new AssertionError("Unexpected message format" + format + " from " + c);
                }
                return hasReset;
            } else {
                applyColorFrom(builder, hasReset, format);
                return true;
            }
        } else {
            String hex = singleFormat.replaceAll("[&§#x]", "");
            if (hex.length() == 6) {
                builder.applyFormatFrom(ChatEnum.STYLE_RESET).withColor(parseHexNoPrefix(hex));
            } else {
                throw new IllegalArgumentException("No Such Format " + singleFormat);
            }

            return hasReset;
        }
    }

    public static String getLegacyFormatString(Object style, boolean hasFormat) {
        StringBuilder temp = new StringBuilder();
        getLegacyFormatString(temp, style, false, hasFormat);
        return temp.toString();
    }

    public static boolean getLegacyFormatString(
            StringBuilder out, Object style, boolean isEmptyContent, boolean hasFormat) {
        StyleBuilder builder = StyleBuilder.formatFrom(style);
        return getLegacyFormatString(out, builder, isEmptyContent, hasFormat);
    }

    public static boolean getLegacyFormatString(
            StringBuilder out, StyleBuilder builder, boolean isEmptyContent, boolean hasFormat) {

        Object textColor = builder.color();
        if (!isEmptyContent || textColor != null) {
            if (textColor != null) {
                Object enumChat = NMSChat.FORMAT.textColor$formatGetter(textColor);
                if (enumChat != null) {
                    out.append(String.valueOf(enumChat));
                } else {
                    out.append("§x");
                    String format = String.format(Locale.ROOT, "%06X", NMSChat.FORMAT.textColor$getValue(textColor));
                    for (char magic : format.toCharArray()) {
                        out.append('§').append(magic);
                    }
                }
                hasFormat = true;
            } else if (hasFormat) {
                out.append("§r");
            }
        }
        if (builder.isBold()) {
            out.append(EnumFormat.BOLD.toString());
            hasFormat = true;
        }
        if (builder.isItalic()) {
            out.append(EnumFormat.ITALIC.toString());
            hasFormat = true;
        }
        if (builder.isUnderlined()) {
            out.append(EnumFormat.UNDERLINE.toString());
            hasFormat = true;
        }
        if (builder.isStrikethrough()) {
            out.append(EnumFormat.STRIKETHROUGH.toString());
            hasFormat = true;
        }
        if (builder.isObfuscated()) {
            out.append(EnumFormat.OBFUSCATED.toString());
            hasFormat = true;
        }
        return hasFormat;
    }

    private static final EnumMap<EnumFormat, ?> FORMAT_TO_NMS = new EnumMap<>(EnumFormat.class);

    public static Object chatFormatFromEnum(EnumFormat enumFormat) {
        return FORMAT_TO_NMS.get(enumFormat);
    }

    public static Object textcolorFromEnum(EnumFormat enumFormat) {
        return NMSChat.FORMAT.textcolorFromChatFormat(FORMAT_TO_NMS.get(enumFormat));
    }

    static {
        Class<?> clazz;
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.ChatFormatting");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Map<String, ?> val = Arrays.stream(clazz.getEnumConstants())
                .collect(ImmutableMap.toImmutableMap(i -> ((Enum) i).name(), Function.identity()));
        for (var enumF : EnumFormat.values()) {
            ((Map) FORMAT_TO_NMS).put(enumF, val.get(enumF.name()));
        }
    }

    private static int parseHex(String val) {
        try {
            return Integer.parseInt(val.substring(1), 16);
        } catch (Throwable e) {
            throw new RuntimeException("Invalid hex color string: " + val, e);
        }
    }

    private static int parseHexNoPrefix(String val) {
        try {
            return Integer.parseInt(val, 16);
        } catch (Throwable e) {
            throw new RuntimeException("Invalid hex color string: " + val, e);
        }
    }

    public static Object setColorFrom(Object val, boolean hasResetFlag, EnumFormat color) {
        Object val0 = (hasResetFlag ? ChatEnum.STYLE_LEGACY_EMPTY : ChatEnum.STYLE_RESET);

        val0 = NMSChat.FORMAT.withColor(val0, FORMAT_TO_NMS.get(color));
        return hasResetFlag ? resetTextFormat(val, val0) : val0;
    }

    public static void applyColorFrom(StyleBuilder builder, boolean hasResetFlag, EnumFormat color) {
        Object val0 = (hasResetFlag ? ChatEnum.STYLE_LEGACY_EMPTY : ChatEnum.STYLE_RESET);
        StyleBuilder builder1 = StyleBuilder.builder().applyFormatFrom(val0).withColor(color);
        if (hasResetFlag) {
            resetTextFormat(builder, builder1);
        }
        builder.applyFormatFrom(builder1).resetEvent();
    }

    private static Object resetTextFormat(Object parent, Object child) {
        if (NMSChat.FORMAT.isBold(parent)) {
            child = NMSChat.FORMAT.withBold(child, Boolean.FALSE);
        }
        if (NMSChat.FORMAT.isItalic(parent)) {
            child = NMSChat.FORMAT.withItalic(child, Boolean.FALSE);
        }
        if (NMSChat.FORMAT.isObfuscated(parent)) {
            child = NMSChat.FORMAT.withObfuscated(child, Boolean.FALSE);
        }
        if (NMSChat.FORMAT.isStrikethrough(parent)) {
            child = NMSChat.FORMAT.withStrikethrough(child, Boolean.FALSE);
        }
        if (NMSChat.FORMAT.isUnderlined(parent)) {
            child = NMSChat.FORMAT.withUnderlined(child, Boolean.FALSE);
        }
        return child;
    }

    private static void resetTextFormat(StyleBuilder parent, StyleBuilder child) {
        if (parent.isBold()) {
            child.bold(Boolean.FALSE); // = FORMAT.withBold(child, Boolean.FALSE);
        }
        if (parent.isItalic()) {
            child.italic(Boolean.FALSE);
            // child = FORMAT.withItalic(child, Boolean.FALSE);
        }
        if (parent.isObfuscated()) {
            child.obfuscated(Boolean.FALSE);
            // child = FORMAT.withObfuscated(child, Boolean.FALSE);
        }
        if (parent.isStrikethrough()) {
            child.strikethrough(Boolean.FALSE);
            // child = FORMAT.withStrikethrough(child, Boolean.FALSE);
        }
        if (parent.isUnderlined()) {
            child.underlined(Boolean.FALSE);
            // child = FORMAT.withUnderlined(child, Boolean.FALSE);
        }
    }

    private static final EnumMap<ClickEvent.Action, Object> CLICK_MAP = new EnumMap<>(ClickEvent.Action.class);
    private static final Reference2ObjectMap<HoverEvent.Action, Object> HOVER_MAP = new Reference2ObjectOpenHashMap<>();

    static {
        Class<?> clazz;
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.network.chat.ClickEvent$Action");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Arrays.stream(clazz.getEnumConstants()).forEach(i -> {
            String name = ((Enum) i).name();
            CLICK_MAP.put(ClickEvent.Action.valueOf(name), i);
        });
        try {
            clazz = ObfManager.getManager().reobfClass("net.minecraft.network.chat.HoverEvent$Action");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        List<Field> fields = ReflectUtils.getAllFieldsRecursively(clazz);
        HOVER_MAP.put(HoverEvent.Action.SHOW_ENTITY, Utils.matchName(fields, "SHOW_ENTITY"));
        HOVER_MAP.put(HoverEvent.Action.SHOW_ITEM, Utils.matchName(fields, "SHOW_ITEM"));
        HOVER_MAP.put(HoverEvent.Action.SHOW_TEXT, Utils.matchName(fields, "SHOW_TEXT"));
    }

    public static Object toNMSClickAction(ClickEvent.Action adventure) {
        return CLICK_MAP.get(adventure);
    }

    public static Object toNMSHoverAction(HoverEvent.Action adventure) {
        return HOVER_MAP.get(adventure);
    }

    public static HoverEvent.Action fromNMSHoverAction(Object obj) {
        return HOVER_MAP.entrySet().stream()
                .filter(i -> obj == i.getValue())
                .findAny()
                .map(Map.Entry::getKey)
                .orElseThrow();
    }

    public static ClickEvent.Action fromNMSClickEvent(Object obj) {
        return ClickEvent.Action.valueOf(((Enum) obj).name());
    }
}
