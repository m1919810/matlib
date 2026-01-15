package me.matl114.matlib.nmsUtils.chat;

import lombok.ToString;
import me.matl114.matlib.nmsMirror.chat.ChatEnum;
import me.matl114.matlib.nmsMirror.impl.NMSChat;

@ToString
public class LiteralContentBuilder implements ContentBuilder {
    public LiteralContentBuilder() {
        this.stringBuilder = new StringBuilder();
    }

    public LiteralContentBuilder(String origin) {
        this.stringBuilder = new StringBuilder(origin);
    }

    final StringBuilder stringBuilder;

    public LiteralContentBuilder appendStr(String value) {
        this.stringBuilder.append(value);
        return this;
    }

    @Override
    public Object toNMS() {
        return stringBuilder.isEmpty()
                ? ChatEnum.PLAIN_TEXT_EMPTY
                : NMSChat.COMP_CONTENT.newLiteral(stringBuilder.toString());
    }

    @Override
    public ContentBuilder clone() {
        return new LiteralContentBuilder(this.stringBuilder.toString());
    }
}
