package me.matl114.matlib.nmsUtils.chat;

import lombok.*;
import lombok.experimental.Accessors;
import me.matl114.matlib.nmsMirror.impl.NMSChat;

@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
@Getter
@Setter
@ToString
public class TranslatableContentBuilder implements ContentBuilder, Cloneable {
    String translateKey;
    String fallback;

    static Object[] NO_ARGS = new Object[0];

    @Override
    public Object toNMS() {
        return NMSChat.COMP_CONTENT.newTranslatable(translateKey, fallback, NO_ARGS);
    }

    @Override
    public TranslatableContentBuilder clone() {
        try {
            return (TranslatableContentBuilder) TranslatableContentBuilder.super.clone();
        } catch (Throwable e) {
            throw new AssertionError(e);
        }
    }
}
