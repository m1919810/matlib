package me.matl114.matlib.utils.language.componentCompiler;

import net.kyori.adventure.text.event.ClickEvent;

public class ClickEventAST implements EventAST<ClickEvent> ,ComponentAST<ClickEvent> {
    //todo complete ClickEvent
    @Override
    public ParameteredLanBuilder<ClickEvent> build(BuildContent content) {
        return null;
    }

    @Override
    public void walk(StringBuilder outputStream) {
        outputStream.append("ClickEvent{}");
    }
}
