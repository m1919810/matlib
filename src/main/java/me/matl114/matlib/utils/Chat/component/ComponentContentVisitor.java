package me.matl114.matlib.utils.chat.component;

import javax.annotation.Nonnull;
import me.matl114.matlib.common.lang.annotations.NotCompleted;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.componentCompiler.MutableComponentAST;

public class ComponentContentVisitor {
    protected ComponentContentType TYPE;
    protected ComponentContentVisitor parent;

    public ComponentContentVisitor(@Nonnull ComponentContentType type, ComponentContentVisitor parent) {
        this.parent = parent;
        this.TYPE = type;
    }

    public void visitText(
            String val, @Note("This argument should not be modified when passing to parent") boolean placeholder) {

        if (this.parent != null) {
            this.parent.visitText(val, placeholder);
        }
    }

    @NotCompleted
    public void visitScore(String name, Object entity, String objective) {}

    @NotCompleted
    public void visitNbt(String name, Object nbt, String seperator) {}

    @NotCompleted
    public void visitSelector(Object entity, String seperator) {}

    public void visitTranslatable(String key, String fallback) {
        if (this.parent != null) {
            this.parent.visitTranslatable(key, fallback);
        }
    }

    public void visitKeybind(String key) {
        if (this.parent != null) {
            this.parent.visitKeybind(key);
        }
    }
    // todo complete CustomFormat thing
    public void visitCustom(String customKey, MutableComponentAST self) {
        if (this.parent != null) {
            this.parent.visitCustom(customKey, self);
        }
    }

    public void visitEnd() {
        if (this.parent != null) {
            this.parent.visitEnd();
        }
    }
}
