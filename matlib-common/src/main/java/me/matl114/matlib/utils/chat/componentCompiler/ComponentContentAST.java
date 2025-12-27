package me.matl114.matlib.utils.chat.componentCompiler;

import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.utils.chat.ComponentContentType;
import me.matl114.matlib.utils.chat.component.ComponentContentVisitor;

public abstract class ComponentContentAST {
    public abstract ComponentContentType contentType();

    public abstract void acceptContent(ComponentContentVisitor visitor, SimpleLinkList<BaseTypeAST> data);
}
