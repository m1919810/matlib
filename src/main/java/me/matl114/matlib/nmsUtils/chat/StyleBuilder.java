package me.matl114.matlib.nmsUtils.chat;

import static me.matl114.matlib.nmsMirror.impl.NMSChat.*;

import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.utils.chat.EnumFormat;

@Accessors(chain = true, fluent = true)
@Setter
@ToString
public class StyleBuilder implements Cloneable, BuildResult<Object> {
    private static final StyleBuilder INSTANCE = new StyleBuilder();

    @Nullable @Getter
    Object color;

    @Nullable Boolean bold;

    @Nullable Boolean italic;

    @Nullable Boolean underlined;

    @Nullable Boolean strikethrough;

    @Nullable Boolean obfuscated;

    @Getter
    @Nullable Object clickEvent;

    @Getter
    @Nullable Object hoverEvent;

    @Getter
    @Nullable String insertion;

    @Getter
    @Nullable Object font;

    public boolean isBold() {
        return bold == Boolean.TRUE;
    }

    public boolean isItalic() {
        return italic == Boolean.TRUE;
    }

    public boolean isUnderlined() {
        return underlined == Boolean.TRUE;
    }

    public boolean isStrikethrough() {
        return strikethrough == Boolean.TRUE;
    }

    public boolean isObfuscated() {
        return obfuscated == Boolean.TRUE;
    }

    public static StyleBuilder builder() {
        return INSTANCE.clone();
    }

    public Object toNMS() {
        return FORMAT.newStyle(
                this.color,
                this.bold,
                this.italic,
                this.underlined,
                this.strikethrough,
                this.obfuscated,
                this.clickEvent,
                this.hoverEvent,
                this.insertion,
                this.font);
    }

    public StyleBuilder withColor(EnumFormat format) {
        return color(ChatUtils.textcolorFromEnum(format));
    }

    public StyleBuilder withColor(int rgb) {
        return color(FORMAT.textcolorFromRgb(rgb));
    }

    public static StyleBuilder formatFrom(Object value) {
        return builder().applyFormatFrom(value);
    }

    public StyleBuilder applyFormatFrom(Object style) {
        this.color = FORMAT.getColor(style);
        this.bold = FORMAT.isBold(style);
        this.italic = FORMAT.isItalic(style);
        this.underlined = FORMAT.isUnderlined(style);
        this.strikethrough = FORMAT.isStrikethrough(style);
        this.obfuscated = FORMAT.isObfuscated(style);
        return this;
    }

    public StyleBuilder applyEventFrom(Object style) {
        this.clickEvent = FORMAT.getClickEvent(style);
        this.hoverEvent = FORMAT.getHoverEvent(style);
        this.font = FORMAT.getFont(style);
        this.insertion = FORMAT.getInsertion(style);
        return this;
    }

    public StyleBuilder reset() {
        resetFormat();
        resetEvent();
        return this;
    }

    public StyleBuilder resetFormat() {
        this.color = null;
        this.bold = null;
        this.italic = null;
        this.underlined = null;
        this.strikethrough = null;
        this.obfuscated = null;
        return this;
    }

    public StyleBuilder resetEvent() {
        this.clickEvent = null;
        this.hoverEvent = null;
        this.insertion = null;
        this.font = null;
        return this;
    }

    public StyleBuilder applyFormatFrom(StyleBuilder other) {
        this.color = other.color;
        this.bold = other.bold;
        this.italic = other.italic;
        this.underlined = other.underlined;
        this.strikethrough = other.strikethrough;
        this.obfuscated = other.obfuscated;
        return this;
    }

    public StyleBuilder applyEventFrom(StyleBuilder other) {
        this.clickEvent = other.clickEvent;
        this.hoverEvent = other.hoverEvent;
        this.insertion = other.insertion;
        this.font = other.font;
        return this;
    }

    @Override
    public StyleBuilder clone() {
        try {
            return (StyleBuilder) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public StyleBuilder toImmutable() {
        Object value = toNMS();
        return new StyleBuilder() {
            @Override
            public Object toNMS() {
                return value;
            }

            @Override
            public StyleBuilder clone() {
                return (StyleBuilder) super.clone();
            }

            public StyleBuilder toImmutable() {
                return this;
            }

            @Override
            public boolean isImmutable() {
                return true;
            }
        };
    }
}
