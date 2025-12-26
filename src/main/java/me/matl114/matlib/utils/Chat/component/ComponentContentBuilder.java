package me.matl114.matlib.utils.chat.component;

import java.util.function.Consumer;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.utils.chat.ComponentContentType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;

public class ComponentContentBuilder extends ComponentContentVisitor {
    private Consumer<ComponentBuilder> callback;

    @Getter
    ComponentBuilder result;

    @Getter
    @Setter
    boolean constValue = true;

    public ComponentContentBuilder(ComponentContentType type) {
        super(type, null);
        this.result = ComponentContentType.initBuilder(type);
    }

    public ComponentContentBuilder(ComponentContentType type, Consumer<ComponentBuilder> callback) {
        super(type, null);
        this.callback = callback;
        this.result = ComponentContentType.initBuilder(type);
    }

    @Override
    public void visitText(String val, boolean placeholder) {
        if (placeholder) {
            setConstValue(false);
        }
        var builder = (TextComponent.Builder) result;
        builder.content(builder.content() + val);
    }

    @Override
    public void visitTranslatable(String key, String fallback) {
        var builder = (TranslatableComponent.Builder) result;
        builder.key(key);
        if (fallback != null) {
            builder.fallback(fallback);
        }
    }

    @Override
    public void visitEnd() {
        if (this.result == null) {
            this.result = Component.empty().toBuilder();
        }
        if (this.callback != null) {
            this.callback.accept(result);
        }
    }
}
