package me.matl114.matlib.utils.chat.component;

import java.util.List;
import java.util.function.Consumer;
import me.matl114.matlib.utils.chat.ComponentContentType;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;

public class ComponentVisitor {
    protected ComponentVisitor parent;

    public ComponentVisitor(ComponentVisitor p) {
        this.parent = p;
    }

    public void visitPlaceholderStyle(String path) {
        if (this.parent != null) {
            this.parent.visitPlaceholderStyle(path);
        }
    }

    public void visitStyle(Consumer<Style.Builder> style, String rawData) {
        if (this.parent != null) {
            this.parent.visitStyle(style, rawData);
        }
    }

    public void visitHoverEvent(HoverEvent.Action hoverEvent, Object rawData) {
        if (this.parent != null) {
            this.parent.visitHoverEvent(hoverEvent, rawData);
        }
    }

    public void visitClickEvent(ClickEvent.Action clickEvent, List rawData) {
        if (this.parent != null) {
            this.parent.visitClickEvent(clickEvent, rawData);
        }
    }

    public ComponentVisitor visitHoverEventComponent(Consumer<Object> hoverEventCallback) {
        return this.parent == null ? null : this.parent.visitHoverEventComponent(hoverEventCallback);
    }

    public ComponentContentVisitor visitComponentContent(ComponentContentType type) {
        return this.parent == null ? new ComponentContentVisitor(type, null) : this.parent.visitComponentContent(type);
    }

    public ComponentVisitor visitSibling() {
        return this.parent != null ? (this.parent).visitSibling() : new ComponentVisitor(null);
    }

    public void visitListEnd() {
        if (this.parent != null) {
            (this.parent).visitListEnd();
        }
    }

    public void visitEnd() {
        if (this.parent != null) {
            this.parent.visitEnd();
        }
    }
}
