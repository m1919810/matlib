package me.matl114.matlib.utils.chat.componentCompiler;

public interface ComponentAST {
    //    public abstract ParameteredLanBuilder<T> build(BuildContent content);

    public abstract void walk(StringBuilder outputStream);
}
