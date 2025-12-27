package me.matl114.matlib.utils.chat.componentCompiler;

import java.util.ArrayList;
import java.util.List;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.common.lang.exceptions.CompileError;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.component.ComponentVisitor;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

public interface EventAST<T> {
    static EventAST resolve(String type, SimpleLinkList<MutableComponentAST> base) {
        // todo left as not done
        // todo left as not done
        if (type.startsWith("hover")) {
            return new HoverEventAST(type, base);
        } else {
            return new ClickEventAST(type, base);
        }
    }

    T getEvent();

    public void accept(ComponentVisitor visitor);

    public static class HoverEventAST implements EventAST<HoverEvent.Action> {
        public HoverEvent.Action actionType;
        public Object content;

        public HoverEventAST(String type, SimpleLinkList<MutableComponentAST> base) {
            MutableComponentAST ast;
            actionType = switch (type) {
                case "hover_text:":
                    content = base;
                    yield HoverEvent.Action.SHOW_TEXT;
                case "hover_item:":
                    ast = base.getFirst();
                    if (ast != null) {
                        var texts = ast.conponentContent;
                        if (texts != null && !texts.isEmpty()) {
                            content = (String) texts.getFirst().rawData;
                        }
                    }
                    yield HoverEvent.Action.SHOW_ITEM;
                case "hover_entity:":
                    ast = base.getFirst();
                    if (ast != null) {
                        var texts = ast.conponentContent;
                        if (texts != null && !texts.isEmpty()) {
                            content = (String) texts.getFirst().rawData;
                        }
                    }
                    yield HoverEvent.Action.SHOW_ENTITY;
                default:
                    throw new CompileError(CompileError.CompilePeriod.SDT_TRANSLATE, -1, "No such event type: " + type);};
        }
        // todo complete hoverEvent

        @Override
        public HoverEvent.Action getEvent() {
            return actionType;
        }

        public void accept(ComponentVisitor visitor) {
            if (content instanceof SimpleLinkList<?> textlist) {
                var newBuilder = visitor.visitHoverEventComponent(obj -> visitor.visitHoverEvent(actionType, obj));
                newBuilder.visitComponentContent(ComponentContentType.LITERAL).visitEnd();
                for (Object var0 : textlist) {
                    MutableComponentAST sub = (MutableComponentAST) var0;
                    var compBuilder = newBuilder.visitSibling();
                    sub.accept(compBuilder);
                }
                newBuilder.visitListEnd();
                newBuilder.visitEnd();
            } else {
                visitor.visitHoverEvent(actionType, content);
            }
        }

        public Object getRawData() {
            return content;
        }

        @Override
        public String toString() {
            return "Hover:" + content;
        }
    }

    public static class ClickEventAST implements EventAST<ClickEvent.Action> {
        ClickEvent.Action actionType;
        List<BaseTypeAST> content;

        public ClickEventAST(String type, SimpleLinkList<MutableComponentAST> base) {
            content = new ArrayList<>(8);
            for (var con : base) {
                if (con.styleAst != null) {
                    for (var st : con.styleAst) {
                        content.add(st);
                    }
                }
                if (con.conponentContent != null) {
                    for (var st : con.conponentContent) {
                        content.add(st);
                    }
                }
            }
            actionType = switch (type) {
                case "click_url:":
                    yield ClickEvent.Action.OPEN_URL;
                case "click_run:":
                    yield ClickEvent.Action.RUN_COMMAND;
                case "click_suggest:":
                    yield ClickEvent.Action.SUGGEST_COMMAND;
                case "click_copy:":
                    yield ClickEvent.Action.COPY_TO_CLIPBOARD;
                default:
                    throw new CompileError(CompileError.CompilePeriod.SDT_TRANSLATE, -1, "No such event type: " + type);};
        }
        // todo complete ClickEvent

        @Override
        public ClickEvent.Action getEvent() {
            return actionType;
        }

        public void accept(ComponentVisitor visitor) {
            visitor.visitClickEvent(actionType, content);
        }

        public List<BaseTypeAST> getRawData() {
            return content;
        }

        @Override
        public String toString() {
            return "Click:" + content;
        }
    }
}
