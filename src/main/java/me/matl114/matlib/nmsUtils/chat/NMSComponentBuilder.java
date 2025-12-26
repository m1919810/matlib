package me.matl114.matlib.nmsUtils.chat;

import static me.matl114.matlib.nmsMirror.impl.NMSChat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSEntity;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.component.ComponentContentVisitor;
import me.matl114.matlib.utils.chat.component.ComponentVisitor;
import me.matl114.matlib.utils.chat.componentCompiler.BaseTypeAST;
import me.matl114.matlib.utils.chat.placeholder.ArgumentProvider;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

public class NMSComponentBuilder extends ComponentVisitor {
    @Getter
    Function<ArgumentProvider, MutableBuilder> finalFunction;

    private Consumer<Function<ArgumentProvider, MutableBuilder>> callback;
    MutableBuilder thisBuilder = MutableBuilder.builder();
    List<BiConsumer<ArgumentProvider, MutableBuilder>> processors = new ArrayList<>();
    List<BiConsumer<ArgumentProvider, MutableBuilder>> childProcessor = new ArrayList<>();

    List<BiConsumer<ArgumentProvider, StyleBuilder>> styleProcessor = new ArrayList<>();
    List<BiConsumer<ArgumentProvider, StyleBuilder>> eventProcessor = new ArrayList<>();
    boolean constValue = true;
    boolean constStyle = true;
    boolean constEvent = true;
    boolean constChild = true;

    private void initStyle() {
        if (this.thisBuilder.style == null) {
            this.thisBuilder.style = StyleBuilder.builder();
        }
    }

    public boolean isConst() {
        return constValue && constStyle && constEvent && constChild;
    }

    public NMSComponentBuilder() {
        super(null);
    }

    public NMSComponentBuilder(Consumer<Function<ArgumentProvider, MutableBuilder>> callback) {
        super(null);
        this.callback = callback;
    }

    @Override
    public void visitStyle(Consumer<Style.Builder> style, String rawData) {
        // styleProcessor.add((arg, sty)-> style.accept(sty));
        initStyle();
        if (constStyle) {
            ChatUtils.solveLegacyFormatString(thisBuilder.style(), rawData, true);
        } else {
            styleProcessor.add(((provider, builder) -> {
                ChatUtils.solveLegacyFormatString(builder, rawData, true);
            }));
        }
    }

    @Override
    public void visitPlaceholderStyle(String path) {
        initStyle();
        // only when we apply more on this can we realize application on Style
        styleProcessor.add((arg, sty) -> {
            String style = arg.getAsString(path);
            if (style != null) {
                ChatUtils.solveLegacyFormatString(sty, style, true);
            }
        });
        constStyle = false;
    }
    //
    @Override
    public void visitClickEvent(ClickEvent.Action clickEvent, List rawData) {
        initStyle();
        boolean isConst = true;
        for (var bb : rawData) {
            if (bb instanceof BaseTypeAST bbb && bbb.isPlaceholder()) {
                isConst = false;
                break;
            }
        }
        Object typeNMS = ChatUtils.toNMSClickAction(clickEvent);
        if (isConst) {
            String value = (String) rawData.stream()
                    .map(i -> {
                        if (i instanceof BaseTypeAST bbbb) return bbbb.getRaw();
                        else return String.valueOf(i);
                    })
                    .collect(Collectors.joining());
            Object eventConst = FORMAT.newClickEvent(typeNMS, value);
            if (constEvent) {
                thisBuilder.style.clickEvent(eventConst);
            } else {
                eventProcessor.add((arg, sty) -> sty.clickEvent(eventConst));
            }

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
                sty.clickEvent(FORMAT.newClickEvent(typeNMS, value));
            });
        }
    }

    @Override
    public void visitHoverEvent(HoverEvent.Action hoverEvent, Object rawData) {
        initStyle();
        // builder.hoverEvent(hoverEvent);
        if (hoverEvent == HoverEvent.Action.SHOW_TEXT) {
            if (CHATCOMPONENT.isComponent(rawData)) {
                Object hoverEvent0 = FORMAT.createHoverEventByRaw(HoverEvent.Action.SHOW_TEXT, rawData);
                if (constEvent) {
                    thisBuilder.style().hoverEvent(hoverEvent0);
                } else {
                    eventProcessor.add((arg, sty) -> sty.hoverEvent(hoverEvent0));
                }
            } else if (rawData instanceof MutableBuilder builder) {
                Object comp = builder.toNMS();

                Object hoverEvent0 = FORMAT.createHoverEventByRaw(
                        HoverEvent.Action.SHOW_TEXT,
                        comp); //  .newHoverEvent(ChatUtils.toNMSHoverAction(HoverEvent.Action.SHOW_TEXT), comp);
                if (constEvent) {
                    thisBuilder.style().hoverEvent(hoverEvent0);
                } else {
                    eventProcessor.add((arg, sty) -> sty.hoverEvent(hoverEvent0));
                }
            } else if (rawData instanceof Function processor) {
                eventProcessor.add((arg, sty) -> {
                    Object optionalComp = processor.apply(arg);
                    if (CHATCOMPONENT.isComponent(optionalComp)) {
                        sty.hoverEvent(FORMAT.createHoverEventByRaw(HoverEvent.Action.SHOW_TEXT, optionalComp));
                    } else if (optionalComp instanceof MutableBuilder builder) {
                        Object comp = builder.toNMS();
                        Object hoverEvent0 = FORMAT.createHoverEventByRaw(HoverEvent.Action.SHOW_TEXT, comp);
                        sty.hoverEvent(hoverEvent0);
                    }
                });
                this.constEvent = false;
            }
        } else if (hoverEvent == HoverEvent.Action.SHOW_ITEM) {
            if (rawData instanceof ItemStack stack) {
                Object nms = ItemUtils.unwrapHandle(stack);
                Object hoverEventConst = FORMAT.createHoverEventByRaw(HoverEvent.Action.SHOW_ITEM, nms);
                if (constEvent) {
                    thisBuilder.style().hoverEvent(hoverEventConst);
                } else {
                    eventProcessor.add((arg, sty) -> sty.hoverEvent(hoverEventConst));
                }
            } else if (rawData instanceof String key) {
                eventProcessor.add((arg, sty) -> {
                    ItemStack stack = arg.getAsItemStack(key);
                    if (stack != null) {
                        Object nms = ItemUtils.unwrapHandle(stack);
                        Object hoverEvent0 = FORMAT.createHoverEventByRaw(HoverEvent.Action.SHOW_ITEM, nms);
                        sty.hoverEvent(hoverEvent0);
                    }
                });
                this.constEvent = false;
            }
        } else if (hoverEvent == HoverEvent.Action.SHOW_ENTITY) {
            if (rawData instanceof Entity entity) {
                Object handle = CraftBukkit.ENTITY.getHandle(entity);
                Object hoverEventConst = NMSEntity.ENTITY.createHoverEvent(handle);
                if (constEvent) {
                    thisBuilder.style().hoverEvent(hoverEventConst);
                } else {
                    eventProcessor.add((arg, sty) -> sty.hoverEvent(hoverEventConst));
                }

            } else if (rawData instanceof String key) {
                eventProcessor.add((arg, sty) -> {
                    Entity entity = arg.getAsEntity(key);
                    if (entity != null) {

                        Object handle = CraftBukkit.ENTITY.getHandle(entity);
                        Object hoverEvent0 = NMSEntity.ENTITY.createHoverEvent(handle);
                        sty.hoverEvent(hoverEvent0);
                    }
                });
                this.constEvent = false;
            }
        }
    }

    @Override
    public ComponentVisitor visitHoverEventComponent(Consumer<Object> hoverEventCallback) {
        initStyle();
        Holder<NMSComponentBuilder> holder = Holder.of(null);
        holder.setValue(new NMSComponentBuilder((result) -> {
            var thi = holder.get();
            if (thi.isConst()) {
                hoverEventCallback.accept(result.apply(null));
            } else {
                hoverEventCallback.accept(result);
            }
        }));
        return holder.get();
    }
    //
    public static ContentBuilder initBuilder(ComponentContentType type) {
        return switch (type) {
            case TRANSLATABLE -> new TranslatableContentBuilder();
            case LITERAL -> new LiteralContentBuilder();
            case EMPTY -> null;
            case NBT -> throw new NotImplementedYet();
                // not impl yet
            case SCORE -> new LiteralContentBuilder();
            case KEYBIND -> new LiteralContentBuilder();
            case SELECTOR -> new LiteralContentBuilder();
        };
    }

    @Override
    public ComponentContentVisitor visitComponentContent(ComponentContentType type) {
        ContentBuilder builder0 = initBuilder(type);
        thisBuilder.componentContent(builder0);
        return new ComponentContentVisitor(type, null) {
            // todo complete this dynamic builder

            @Override
            public void visitText(String val, boolean placeholder) {
                boolean constValue1 = constValue;
                if (placeholder) {
                    constValue = false;
                    processors.add((arg, compBase) -> {
                        var builder = (LiteralContentBuilder) compBase.componentContent();
                        String value = arg.getAsString(val);
                        if (value != null) {
                            builder.appendStr(value);
                        }
                    });
                } else {
                    if (constValue1) {
                        var builder = (LiteralContentBuilder) thisBuilder.componentContent();
                        builder.appendStr(val);
                    } else {
                        processors.add((arg, compBase) -> {
                            var builder = (LiteralContentBuilder) compBase.componentContent();
                            builder.appendStr(val);
                        });
                    }
                }
            }

            @Override
            public void visitTranslatable(String key, String fallback) {
                var builder = (TranslatableContentBuilder) thisBuilder.componentContent();
                builder.translateKey(key);
                if (fallback != null) {
                    builder.fallback(fallback);
                }
            }

            @Override
            public void visitEnd() {}
        };
    }
    //
    public ComponentVisitor visitSibling() {
        Holder<NMSComponentBuilder> holder = Holder.of(null);

        holder.setValue(new NMSComponentBuilder((argfunc) -> {
            NMSComponentBuilder thi = holder.get();
            if (thi.isConst()) {
                MutableBuilder sub = argfunc.apply(null);
                if (constChild) {
                    thisBuilder.siblings().add(sub);
                } else {
                    childProcessor.add(((argumentProvider, componentBuilder) ->
                            componentBuilder.siblings().add(sub)));
                }
            } else {
                constChild = false;
                childProcessor.add(((argumentProvider, componentBuilder) -> componentBuilder.siblings.add(
                        argfunc.apply(argumentProvider).toImmutable())));
            }
        }));
        return holder.get();
    }
    //
    private MutableBuilder processArgs(ArgumentProvider provider) {
        // copy a builder;
        MutableBuilder builder0 = thisBuilder.clone(); // .build().toBuilder();
        if (!styleProcessor.isEmpty())
            for (var style : styleProcessor) {
                style.accept(provider, builder0.style());
            }
        if (!eventProcessor.isEmpty())
            for (var style : eventProcessor) {
                style.accept(provider, builder0.style());
            }
        if (!processors.isEmpty())
            for (var process : processors) {
                process.accept(provider, builder0);
            }
        if (!childProcessor.isEmpty())
            for (var process : childProcessor) {
                process.accept(provider, builder0);
            }

        return builder0.toImmutable();
    }

    @Override
    public void visitEnd() {
        if (isConst()) {
            MutableBuilder func = processArgs(null);
            finalFunction = (arg) -> func;
        } else {
            StyleBuilder styleConstants = thisBuilder.style();
            List<BiConsumer<ArgumentProvider, StyleBuilder>> styleDynamic = new ArrayList<>();
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
            if (styleDynamic.isEmpty() && styleConstants != null) {
                thisBuilder.style(styleConstants.toImmutable());
            }
            List<BiConsumer<ArgumentProvider, MutableBuilder>> builderDynamic = new ArrayList<>();
            if (constValue) {
                for (var build : processors) {
                    build.accept(null, thisBuilder);
                }
            } else {
                builderDynamic.addAll(processors);
            }
            if (constChild) {
                for (var build : childProcessor) {
                    build.accept(null, thisBuilder);
                }
            } else {
                builderDynamic.addAll(childProcessor);
            }
            BiConsumer[] styleDynamicArray = styleDynamic.toArray(BiConsumer[]::new);
            BiConsumer[] builderDynamicArray = builderDynamic.toArray(BiConsumer[]::new);
            {
                if (styleDynamicArray.length == 0) {

                    finalFunction = (args) -> {
                        MutableBuilder builder = thisBuilder.clone();
                        for (var build : builderDynamicArray) {
                            build.accept(args, builder);
                        }
                        return builder.toImmutable();
                    };
                } else {
                    if (builderDynamicArray.length == 0) {

                        StyleBuilder styleBase = thisBuilder.style();
                        finalFunction = (args) -> {
                            Iterable<?> valueNoStyle = thisBuilder.toNMS();
                            StyleBuilder newStyle = styleBase.clone();
                            for (var style : styleDynamicArray) {
                                style.accept(args, newStyle);
                            }
                            CHATCOMPONENT.setStyle(valueNoStyle, newStyle.toNMS());
                            return MutableBuilder.immutable(valueNoStyle);
                        };
                    } else {
                        finalFunction = (args) -> {
                            MutableBuilder builder = thisBuilder.clone();
                            StyleBuilder mutableStyle = builder.style();
                            for (var style : styleDynamicArray) {
                                style.accept(args, mutableStyle);
                            }
                            for (var build : builderDynamicArray) {
                                build.accept(args, builder);
                            }
                            return builder.toImmutable();
                        };
                    }
                }
            }
        }
        if (callback != null) {
            callback.accept(finalFunction);
        }
    }
}
