package me.matl114.matlib.core.nms.chat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.matl114.matlib.nmsUtils.network.PacketFlow;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.METHOD})
public @interface Translator {
    TranslateType type();

    PacketFlow flow();

    int priority() default 0;
}
