package me.matl114.matlib.utils.chat.component;

import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.componentCompiler.BaseTypeAST;
import me.matl114.matlib.utils.chat.placeholder.ArgumentProvider;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class PlaceholderedBuilder extends ComponentVisitor {
    ArgumentProvider provider;

    public PlaceholderedBuilder(ComponentVisitor visitor, ArgumentProvider placeholder) {
        super(visitor);
        this.provider = placeholder;
    }

    @Override
    public ComponentVisitor visitSibling() {
        var sib = super.visitSibling();
        return new PlaceholderedBuilder(sib, provider);
    }

    @Override
    public void visitPlaceholderStyle(String path) {
        Consumer<Style.Builder> style = provider.getAsDecorator(path);
        if (style != null) {
            super.visitStyle(style, path);
        } else {
            super.visitPlaceholderStyle(path);
        }
    }

    @Override
    public ComponentContentVisitor visitComponentContent(ComponentContentType type) {
        var ccv = super.visitComponentContent(type);
        return new ComponentContentVisitor(type, ccv) {
            @Override
            public void visitText(String val, boolean placeholder) {
                if (placeholder) {
                    String value = provider.getAsString(val);
                    super.visitText(value, true);
                    return;
                }
                super.visitText(val, false);
            }
        };
    }

    @Override
    public ComponentVisitor visitHoverEventComponent(Consumer<Object> consumer) {
        var cb = super.visitHoverEventComponent(consumer);
        return new PlaceholderedBuilder(cb, provider);
    }

    @Override
    public void visitHoverEvent(HoverEvent.Action hoverEvent, Object rawData) {
        if (parent == null) {
            return;
        }
        if (hoverEvent == HoverEvent.Action.SHOW_TEXT) {

            super.visitHoverEvent(hoverEvent, rawData);
        } else if (hoverEvent == HoverEvent.Action.SHOW_ITEM) {
            if (rawData instanceof String stringData) {
                ItemStack stack = provider.getAsItemStack(stringData);
                if (stack != null) {
                    super.visitHoverEvent(hoverEvent, stack);
                }
            }
        } else if (hoverEvent == HoverEvent.Action.SHOW_ENTITY) {
            if (rawData instanceof String stringData) {
                Entity stack = provider.getAsEntity(stringData);
                if (stack != null) {
                    super.visitHoverEvent(hoverEvent, stack);
                }
            }
        }
    }

    static final Pattern pat = Pattern.compile("[{].*[}]");

    @Override
    public void visitClickEvent(ClickEvent.Action clickEvent, List rawData) {
        if (parent != null && rawData != null)
            super.visitClickEvent(
                    clickEvent,
                    rawData.stream()
                            .map(str -> {
                                if (str instanceof BaseTypeAST ast) {
                                    if (ast.isPlaceholder()) {
                                        return provider.getAsString(ast.getRaw());
                                    }
                                    return ast.getRaw();
                                } else return String.valueOf(str);
                            })
                            .toList());
    }
}
