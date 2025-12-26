package me.matl114.matlib.implement.nms.network;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.nmsUtils.network.GamePacket;

@Retention(RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.METHOD})
@Note(
        "Use to mark the PacketHandler methods in target class, note that the class should be public, and method be non-static and public")
public @interface PacketHandler {
    GamePacket type() default GamePacket.ALL_PLAY;

    int priority() default 0;

    boolean ignoreIfCancel() default false;
}
