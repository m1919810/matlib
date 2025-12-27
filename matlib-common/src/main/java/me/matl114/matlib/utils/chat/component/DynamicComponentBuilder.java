package me.matl114.matlib.utils.chat.component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.componentCompiler.BaseTypeAST;
import me.matl114.matlib.utils.chat.placeholder.ArgumentProvider;
import net.kyori.adventure.text.*;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class DynamicComponentBuilder extends ComponentVisitor {
    @Getter
    Function<ArgumentProvider, Component> finalFunction;

    private Consumer<Function<ArgumentProvider, Component>> callback;

    List<BiConsumer<ArgumentProvider, ComponentBuilder>> processors = new ArrayList<>();
    List<BiConsumer<ArgumentProvider, ComponentBuilder>> childProcessor = new ArrayList<>();

    List<BiConsumer<ArgumentProvider, Style.Builder>> styleProcessor = new ArrayList<>();
    List<BiConsumer<ArgumentProvider, Style.Builder>> eventProcessor = new ArrayList<>();
    boolean constValue = true;
    boolean constStyle = true;
    boolean constEvent = true;
    boolean constChild = true;

    public boolean isConst() {
        return constValue && constStyle && constEvent && constChild;
    }

    net.kyori.adventure.text.ComponentBuilder componentBase;

    public DynamicComponentBuilder() {
        super(null);
    }

    public DynamicComponentBuilder(Consumer<Function<ArgumentProvider, Component>> callback) {
        super(null);
        this.callback = callback;
    }

    @Override
    public void visitStyle(Consumer<Style.Builder> style, String rawData) {
        styleProcessor.add((arg, sty) -> style.accept(sty));
    }

    @Override
    public void visitPlaceholderStyle(String path) {
        styleProcessor.add((arg, sty) -> {
            Consumer<Style.Builder> style = arg.getAsDecorator(path);
            if (style != null) {
                style.accept(sty);
            }
        });
        constStyle = false;
    }

    @Override
    public void visitClickEvent(ClickEvent.Action clickEvent, List rawData) {
        boolean isConst = true;
        for (var bb : rawData) {
            if (bb instanceof BaseTypeAST bbb && bbb.isPlaceholder()) {
                isConst = false;
                break;
            }
        }
        if (isConst) {
            String value = (String) rawData.stream()
                    .map(i -> {
                        if (i instanceof BaseTypeAST bbbb) return bbbb.getRaw();
                        else return String.valueOf(i);
                    })
                    .collect(Collectors.joining());
            eventProcessor.add((arg, sty) -> sty.clickEvent(ClickEvent.clickEvent(clickEvent, value)));
        } else {
            constEvent = false;
            eventProcessor.add((arg, sty) -> {
                String value = (String) rawData.stream()
                        .map(i -> {
                            if (i instanceof BaseTypeAST bbbb)
                                return bbbb.isPlaceholder() ? arg.getAsString(bbbb.getRaw()) : bbbb.getRaw();
                            else return String.valueOf(i);
                        })
                        .collect(Collectors.joining());
                sty.clickEvent(ClickEvent.clickEvent(clickEvent, value));
            });
        }
    }
    // Reference2BooleanOpenHashMap<Component> constComponent = new Reference2BooleanOpenHashMap<>();
    @Override
    public void visitHoverEvent(HoverEvent.Action hoverEvent, Object rawData) {
        // builder.hoverEvent(hoverEvent);
        if (hoverEvent == HoverEvent.Action.SHOW_TEXT) {
            if (rawData instanceof Component comp) {
                HoverEvent text = HoverEvent.showText(comp);

                eventProcessor.add((arg, sty) -> sty.hoverEvent(text));
            } else if (rawData instanceof Function processor) {
                eventProcessor.add((arg, sty) -> {
                    if (processor.apply(arg) instanceof Component comp) {
                        sty.hoverEvent(HoverEvent.showText(comp));
                    }
                });
                this.constEvent = false;
            }
        } else if (hoverEvent == HoverEvent.Action.SHOW_ITEM) {
            if (rawData instanceof ItemStack stack) {
                HoverEvent item = Bukkit.getItemFactory().asHoverEvent(stack, UnaryOperator.identity());
                eventProcessor.add((arg, sty) -> sty.hoverEvent(item));
            } else if (rawData instanceof String key) {
                eventProcessor.add((arg, sty) -> {
                    ItemStack stack = arg.getAsItemStack(key);
                    if (stack != null) {
                        sty.hoverEvent(Bukkit.getItemFactory().asHoverEvent(stack, UnaryOperator.identity()));
                    }
                });
                this.constEvent = false;
            }
        } else if (hoverEvent == HoverEvent.Action.SHOW_ENTITY) {
            if (rawData instanceof Entity entity) {
                HoverEvent entity1 = entity.asHoverEvent();
                eventProcessor.add((arg, sty) -> sty.hoverEvent(entity1));
            } else if (rawData instanceof String key) {
                eventProcessor.add((arg, sty) -> {
                    Entity entity = arg.getAsEntity(key);
                    if (entity != null) {
                        sty.hoverEvent(entity.asHoverEvent());
                    }
                });
                this.constEvent = false;
            }
        }
    }

    @Override
    public ComponentVisitor visitHoverEventComponent(Consumer<Object> hoverEventCallback) {
        Holder<DynamicComponentBuilder> holder = Holder.of(null);
        holder.setValue(new DynamicComponentBuilder((result) -> {
            var thi = holder.get();
            if (thi.isConst()) {
                hoverEventCallback.accept(result.apply(null));
            } else {
                hoverEventCallback.accept(result);
            }
        }));
        return holder.get();
    }

    @Override
    public ComponentContentVisitor visitComponentContent(ComponentContentType type) {
        this.componentBase = ComponentContentType.initBuilder(type);
        return new ComponentContentVisitor(type, null) {
            // todo complete this dynamic builder

            @Override
            public void visitText(String val, boolean placeholder) {
                boolean constValue1 = constValue;
                if (placeholder) {
                    constValue = false;
                    processors.add((arg, compBase) -> {
                        var builder = (TextComponent.Builder) compBase;
                        String value = arg.getAsString(val);
                        if (value != null) {
                            builder.content(builder.content() + value);
                        }
                    });
                } else {
                    if (constValue1) {
                        var builder = (TextComponent.Builder) componentBase;
                        builder.content(builder.content() + val);
                    } else {
                        processors.add((arg, compBase) -> {
                            var builder = (TextComponent.Builder) compBase;
                            builder.content(builder.content() + val);
                        });
                    }
                }
            }

            @Override
            public void visitTranslatable(String key, String fallback) {
                var builder = (TranslatableComponent.Builder) componentBase;
                builder.key(key);
                if (fallback != null) {
                    builder.fallback(fallback);
                }
            }

            @Override
            public void visitEnd() {
                if (componentBase == null) {
                    componentBase = Component.empty().toBuilder();
                }
            }
        };
    }

    public ComponentVisitor visitSibling() {
        Holder<DynamicComponentBuilder> holder = Holder.of(null);

        holder.setValue(new DynamicComponentBuilder((argfunc) -> {
            DynamicComponentBuilder thi = holder.get();
            if (thi.isConst()) {
                Component sub = argfunc.apply(null);
                if (constChild) {
                    componentBase.append(sub);
                } else {
                    childProcessor.add(((argumentProvider, componentBuilder) -> componentBuilder.append(sub)));
                }
            } else {
                constChild = false;
                childProcessor.add(((argumentProvider, componentBuilder) ->
                        componentBuilder.append(argfunc.apply(argumentProvider))));
            }
        }));
        return holder.get();
    }

    private Component processArgs(ArgumentProvider provider) {
        // copy a builder;
        ComponentBuilder builder0 = componentBase.build().toBuilder();
        for (var process : processors) {
            process.accept(provider, builder0);
        }
        for (var process : childProcessor) {
            process.accept(provider, builder0);
        }
        Style.Builder builder = Style.style();
        for (var style : styleProcessor) {
            style.accept(provider, builder);
        }
        for (var style : eventProcessor) {
            style.accept(provider, builder);
        }
        builder0.style(builder.build());
        return builder0.build();
    }

    @Override
    public void visitEnd() {

        if (isConst()) {
            Component func = processArgs(null);
            finalFunction = (arg) -> func;
        } else {
            Style.Builder styleConstants = Style.style();
            List<BiConsumer<ArgumentProvider, Style.Builder>> styleDynamic = new ArrayList<>();
            if (constStyle) {
                for (var style : styleProcessor) {
                    style.accept(null, styleConstants);
                }
            } else {
                styleDynamic.addAll(styleProcessor);
            }
            if (constEvent) {
                for (var style : eventProcessor) {
                    style.accept(null, styleConstants);
                }
            } else {
                styleDynamic.addAll(eventProcessor);
            }
            Style styleConstantBuild = styleConstants.build();
            List<BiConsumer<ArgumentProvider, ComponentBuilder>> builderDynamic = new ArrayList<>();
            if (constValue) {
                for (var build : processors) {
                    build.accept(null, componentBase);
                }
            } else {
                builderDynamic.addAll(processors);
            }
            if (constChild) {
                for (var build : childProcessor) {
                    build.accept(null, componentBase);
                }
            } else {
                builderDynamic.addAll(childProcessor);
            }
            BuildableComponent componentBaseBuild = componentBase.build();
            if (styleDynamic.isEmpty()) {
                finalFunction = (args) -> {
                    ComponentBuilder builder = componentBaseBuild.toBuilder();
                    builder.style(styleConstantBuild);
                    for (var p : builderDynamic) {
                        p.accept(args, builder);
                    }
                    var comp = builder.build();
                    return comp;
                };
            } else {
                finalFunction = (args) -> {
                    Style.Builder style = styleConstantBuild.toBuilder();
                    for (var p : styleDynamic) {
                        p.accept(args, style);
                    }
                    ComponentBuilder builder = componentBaseBuild.toBuilder();
                    builder.style(style.build());
                    for (var p : builderDynamic) {
                        p.accept(args, builder);
                    }
                    return builder.build();
                };
            }
        }
        if (callback != null) {
            callback.accept(finalFunction);
        }
    }
}
