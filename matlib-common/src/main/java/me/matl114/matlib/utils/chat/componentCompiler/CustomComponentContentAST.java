package me.matl114.matlib.utils.chat.componentCompiler;

import lombok.AllArgsConstructor;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.component.ComponentContentVisitor;

@AllArgsConstructor
public class CustomComponentContentAST extends ComponentContentAST {
    MutableComponentAST owner;
    String customType;

    @Override
    public ComponentContentType contentType() {
        return ComponentContentType.LITERAL;
    }

    @Override
    public void acceptContent(ComponentContentVisitor visitor, SimpleLinkList<BaseTypeAST> data) {
        if (visitor != null) {
            visitor.visitCustom(customType, owner);
        }
    }
}
