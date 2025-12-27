package me.matl114.matlib.utils.chat.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.componentCompiler.BaseTypeAST;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class ComponentBuilder extends ComponentVisitor {
    private Consumer<net.kyori.adventure.text.ComponentBuilder> callback;

    @Getter
    @Setter
    net.kyori.adventure.text.ComponentBuilder result;

    List<net.kyori.adventure.text.ComponentBuilder> siblings = new ArrayList<>();
    Style.Builder builder = Style.style();

    public ComponentBuilder() {
        super(null);
    }

    public ComponentBuilder(Consumer<net.kyori.adventure.text.ComponentBuilder> callback) {
        super(null);
        this.callback = callback;
    }

    @Override
    public void visitStyle(Consumer<Style.Builder> style, String rawData) {
        style.accept(builder);
    }

    @Override
    public void visitClickEvent(ClickEvent.Action clickEvent, List rawData) {
        if (rawData != null) {
            builder.clickEvent(ClickEvent.clickEvent(
                    clickEvent,
                    ((Stream<String>) (rawData.stream().map((Function<Object, String>) str -> {
                                if (str instanceof BaseTypeAST ss) return (String) ss.getRaw();
                                else return String.valueOf(str);
                            })))
                            .collect(Collectors.joining())));
        }
    }

    @Override
    public void visitHoverEvent(HoverEvent.Action hoverEvent, Object rawData) {
        //  builder.hoverEvent(hoverEvent);
        if (hoverEvent == HoverEvent.Action.SHOW_TEXT) {
            if (rawData instanceof Component comp) {
                builder.hoverEvent(HoverEvent.showText(comp));
            }
        } else if (hoverEvent == HoverEvent.Action.SHOW_ITEM) {
            if (rawData instanceof ItemStack stack) {
                builder.hoverEvent(Bukkit.getItemFactory().asHoverEvent(stack, UnaryOperator.identity()));
            }
        } else if (hoverEvent == HoverEvent.Action.SHOW_ENTITY) {
            if (rawData instanceof Entity entity) {
                builder.hoverEvent(entity.asHoverEvent());
            }
        }
    }

    @Override
    public ComponentVisitor visitHoverEventComponent(Consumer<Object> hoverEventCallback) {
        return new ComponentBuilder((comp) -> {
            Component component = comp.build();
            hoverEventCallback.accept(component);
        });
    }

    @Override
    public ComponentContentVisitor visitComponentContent(ComponentContentType type) {
        return new ComponentContentBuilder(type, this::setResult);
    }

    @Override
    public void visitEnd() {
        if (this.result != null) {
            this.result = this.result.style(builder.build());
            if (callback != null) {
                callback.accept(this.result);
            }
        }
    }

    public ComponentVisitor visitSibling() {

        return new ComponentBuilder(this.siblings::add);
    }

    @Override
    public void visitListEnd() {
        //        result = Component.empty().children(siblings);
        //        if(callback != null){
        //            callback.accept(result);
        //        }
        if (result != null) {
            for (var sib : this.siblings) {
                result.append(sib);
            }
        }
    }
}
