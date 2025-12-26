package me.matl114.matlib.utils.chat.componentCompiler;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BaseTypeAST {
    //    BaseType type;
    String rawData;
    static final int TYPE_MASK = 1;
    static final int TYPE_STRING = 0;
    static final int TYPE_STYLE = 1;
    static final int PLACEHOLDER_MASK = 2;

    public boolean isPlaceholder() {
        return (code & PLACEHOLDER_MASK) != 0;
    }

    public boolean isStyle() {
        return (code & TYPE_MASK) == TYPE_STYLE;
    }

    public boolean isString() {
        return (code & TYPE_MASK) == TYPE_STRING;
    }

    int code;
    //    public final T get(PlaceholderProvider placeholder, Object[] arguments){
    //        throw new NoLongerSupport();
    //    }

    public String toString() {
        return rawData;
    }

    public String getRaw() {
        return rawData;
    }

    public static BaseTypeAST ofRawString(String raw) {
        return new BaseTypeAST(raw, TYPE_STRING);
    }

    public static BaseTypeAST ofPlaceholderString(String string) {
        return new BaseTypeAST(string, TYPE_STRING | PLACEHOLDER_MASK);
    }

    public static BaseTypeAST ofRawFormat(String raw) {
        return new BaseTypeAST(raw, TYPE_STYLE);
    }

    public static BaseTypeAST ofPlaceholderFormat(String string) {
        return new BaseTypeAST(string, TYPE_STYLE | PLACEHOLDER_MASK);
    }
    //    public static enum BaseType{
    //        STRING,
    //        STYLE,
    //        UNKNOWN
    //    }
}
