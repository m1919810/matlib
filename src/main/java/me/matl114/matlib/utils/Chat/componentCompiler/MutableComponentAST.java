package me.matl114.matlib.utils.chat.componentCompiler;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.common.lang.exceptions.CompileError;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.EnumFormat;
import me.matl114.matlib.utils.chat.component.ComponentContentVisitor;
import me.matl114.matlib.utils.chat.component.ComponentVisitor;
import net.kyori.adventure.text.format.*;

public class MutableComponentAST implements ComponentAST {
    public static final List<EventAST> EMPTY_LIST = java.util.List.of();

    @Nullable public SimpleLinkList<BaseTypeAST> styleAst;

    @Nullable public SimpleLinkList<BaseTypeAST> conponentContent;

    @Nullable public List<EventAST> optionalEvent = EMPTY_LIST;

    @Nonnull
    public ComponentContentAST componentType = TEXT_AST;

    @Nullable SimpleLinkList<MutableComponentAST> children;

    public static final ComponentContentAST TEXT_AST = new ComponentContentAST() {
        @Override
        public ComponentContentType contentType() {
            return ComponentContentType.LITERAL;
        }

        @Override
        public void acceptContent(ComponentContentVisitor visitor, SimpleLinkList<BaseTypeAST> conponentContent) {
            if (conponentContent != null)
                for (var base : conponentContent) {
                    visitor.visitText(base.getRaw(), base.isPlaceholder());
                }
            visitor.visitEnd();
        }
    };
    public static final ComponentContentAST TRANSLATABLE_AST = new ComponentContentAST() {
        // we decided to do something like "key$val"
        @Override
        public ComponentContentType contentType() {
            return ComponentContentType.TRANSLATABLE;
        }

        @Override
        public void acceptContent(ComponentContentVisitor visitor, SimpleLinkList<BaseTypeAST> data) {
            ;
            if (data == null) {
                visitor.visitEnd();
            }
            Iterator<BaseTypeAST> iter = data.iterator();
            if (!iter.hasNext()) {
                visitor.visitEnd();
                return;
            }
            String rawData = iter.next().getRaw();
            String[] value = rawData.split("[$]");
            if (value.length > 1) {
                visitor.visitTranslatable(value[0], value[1]);
            } else {
                visitor.visitTranslatable(value[0], null);
            }
            visitor.visitEnd();
        }
    };

    @Nonnull
    static MutableComponentAST resolveSpecial(
            String type, SimpleLinkList<MutableComponentAST> baseTypes, List<EventAST> eventList) {
        // todo: default solution,left as not done
        ComponentContentAST re;

        MutableComponentAST comp;
        if (!baseTypes.isEmpty()) {
            comp = baseTypes.pop();
        } else {
            comp = new MutableComponentAST();
        }
        comp.optionalEvent = eventList;
        if (!baseTypes.isEmpty()) {
            comp.children = baseTypes;
        }

        switch (type) {
            case "translatable:":
                re = TRANSLATABLE_AST;
                break;
            case "custom:":
                String customType;
                if (comp.conponentContent != null && !comp.conponentContent.isEmpty()) {
                    String firstData = comp.conponentContent.getFirst().rawData;
                    String[] splitData = firstData.split("[$]");
                    customType = splitData[0];
                } else {
                    customType = "";
                }
                re = new CustomComponentContentAST(comp, customType);
                break;
            default:
                re = TEXT_AST;
                break;
        }
        comp.componentType = re;
        return comp;
        //        return re
    }

    @Override
    public void walk(StringBuilder outputStream) {
        outputStream.append(this.getClass().getSimpleName());
        outputStream.append("{");
        outputStream.append("style= ");
        if (styleAst != null) {
            outputStream.append(styleAst);
        }
        outputStream.append(", data= ");
        outputStream.append(componentType.contentType());
        outputStream.append(":");
        outputStream.append(conponentContent);
        outputStream.append(", events= ");
        outputStream.append(optionalEvent);
        outputStream.append(", childs= [");
        if (children != null) {
            for (MutableComponentAST child : children) {
                child.walk(outputStream);
                outputStream.append(", ");
            }
        }
        outputStream.append("]}");
    }

    public void accept(ComponentVisitor visitor) {
        // visit style
        if (visitor == null) {
            return;
        }
        if (this.styleAst != null) {
            for (var ast : styleAst) {
                String raw = ast.getRaw();
                if (ast.isPlaceholder()) {
                    visitor.visitPlaceholderStyle(raw);
                } else {
                    visitor.visitStyle(applyStyleBuilder(raw), raw);
                }
            }
        }

        if (optionalEvent != null && !optionalEvent.isEmpty()) {
            for (var event : optionalEvent) {
                event.accept(visitor);
            }
        }

        var componentContent = visitor.visitComponentContent(componentType.contentType());
        if (componentContent != null) {
            componentType.acceptContent(componentContent, conponentContent);
        }
        if (children != null && !children.isEmpty()) {
            for (var child : children) {
                var sibling = visitor.visitSibling();
                child.accept(sibling);
            }
            visitor.visitListEnd();
        }
        visitor.visitEnd();
    }

    //    @Override
    //    public ParameteredLanBuilder<Component> build(BuildContent content) {
    //        //todo if builder.size = 1, try use
    //        //todo rebuild with List<Function<param,component>?>, and use textofChildren or self if single
    //        List<BiConsumer<TextComponent.Builder,Parameter>> builder = new ArrayList<>();
    //        boolean containsDynamic = false;
    //      //  boolean firstComponent = true;
    //        final Style defaultStyle = content.getStyle().build();
    //        for (BaseTypeAST ast:baseElements){
    //            if(ast.isPlaceholder()){
    //                PlaceholderProvider provider = content.getPlaceholderProvider();
    //                if (ast.getType() == BaseTypeAST.BaseType.STRING){
    //                    Function<Parameter,?> re = provider.getDynamic(ast.getRaw());
    //                    if(re != null){
    //                        containsDynamic = true;
    //                        content.markDynamic();
    //                        builder.add(buildDynamicPlaceholder(content,re));
    //                    }else {
    //                        Component val = buildStaticPlaceholder(content,provider,ast.getRaw());
    //                      //  Debug.logger("build const val",val);
    //                        builder.add((textComponent, parameter) -> {
    //                            textComponent.append(val);
    //                        });
    //
    //                    }
    //                }else if(ast.getType() == BaseTypeAST.BaseType.STYLE) {
    //                    //STYLE should always be static ,not dynamic
    //                    // Function<Parameter,Style> re = provider.getDynamic(ast.getRaw());
    //                    Style.Builder val = provider.getStyleOrDefault(ast.getRaw()).toBuilder();
    //
    //                    content.getStyle().merge(val.build(), Style.Merge.Strategy.ALWAYS);
    ////                    if(firstComponent){
    ////                        defaultStyle = content.getStyle().build();
    ////                    }
    //                  //  Debug.logger("builder change ",content.getStyle());
    //                }
    //            }else {
    //                if(ast.getType() == BaseTypeAST.BaseType.STRING){
    //                    Component val = buildStaticString(content,ast.getRaw());
    //                    content.markDynamic();
    //                 //   Debug.logger("build const val",val);
    //                    builder.add((textComponent, parameter) -> {
    //                        textComponent.append(val);
    //                    });
    //                }else if(ast.getType() == BaseTypeAST.BaseType.STYLE){
    //                    applyStyleBuilder(ast.getRaw()).accept(content.getStyle());
    ////                    if(firstComponent){
    ////                        defaultStyle = content.getStyle().build();
    ////                    }
    //                   // Debug.logger("builder change ",content.getStyle());
    //                }
    //            }
    //          //  firstComponent = false;
    //        }
    //        ParameteredLanBuilder<Function<Component, Component>> builderEvent = buildEvent(content);
    //        if(containsDynamic){
    //            //dynamic builder
    //            return (parametered)->{
    //                TextComponent.Builder builders = Component.text().style(defaultStyle);
    //                for (var re: builder){
    //                    re.accept(builders,parametered);
    //                }
    //                return builderEvent==null ? builders.build():
    // builderEvent.build(parametered).apply(builders.build());
    //            };
    //        }else {
    //            TextComponent.Builder builders = Component.text().style(defaultStyle);
    //            for (var re: builder){
    //                re.accept(builders,null);
    //            }
    //            //static builder
    //            //calculate static
    //            Component constVal = builders.build();
    //            if(builderEvent == null){
    //                return (parametered -> constVal);
    //            }else{
    //                return (parametered -> {
    //                    return builderEvent.build(parametered).apply(constVal);
    //                });
    //            }
    //        }
    //
    //    }

    public static Consumer<Style.Builder> applyStyleBuilder(String value) {
        int len = value.length();
        if (len == 2) {
            EnumFormat format = EnumFormat.getFormat(value.charAt(1));
            if (format.isFormat() && format != EnumFormat.RESET) {
                switch (format) {
                    case BOLD:
                        return (builder) -> {
                            builder.decoration(TextDecoration.BOLD, true);
                        };
                    case ITALIC:
                        return (builder) -> {
                            builder.decoration(TextDecoration.ITALIC, true);
                        };

                    case STRIKETHROUGH:
                        return (builder) -> {
                            builder.decoration(TextDecoration.STRIKETHROUGH, true);
                        };

                    case UNDERLINE:
                        return (builder) -> {
                            builder.decoration(TextDecoration.UNDERLINED, true);
                        };

                    case OBFUSCATED:
                        return (builder) -> {
                            builder.decoration(TextDecoration.OBFUSCATED, true);
                        };

                    default:
                        throw new CompileError(
                                CompileError.CompilePeriod.IR_BUILDING, -1, "Unexpected message format name: " + value);
                }
            } else { // Color resets formatting
                return (builder) -> {
                    builder.merge(Style.empty().color(format.toAdventure()), Style.Merge.Strategy.ALWAYS);
                };
            }
        } else {
            String hex = value.replaceAll("[&§#x]", "");
            if (hex.length() == 6) {
                try {
                    int i = Integer.parseInt(hex, 16);
                    return (builder) -> {
                        builder.color(TextColor.color(i));
                    };
                } catch (Throwable e) {
                    throw new CompileError(
                            CompileError.CompilePeriod.IR_BUILDING,
                            -1,
                            "Unexpected color value: " + value + ", to hex: " + hex);
                }
            } else {
                throw new CompileError(CompileError.CompilePeriod.IR_BUILDING, -1, "Unexpected color format: " + value);
            }
        }
    }

    //    /**
    //     * get component with Event ,must not change origin component, may create new component
    //     * @param content
    //     * @return
    //     */
    //    @Nullable
    //    public final ParameteredLanBuilder<Function<Component,Component>> buildEvent(BuildContent content){
    //        //solve
    //        if(eventList==null||eventList.isEmpty()){
    //            return null;
    //        }
    //
    //        return (param)->(component)->{
    //            return component;
    //        };
    //    }
}
