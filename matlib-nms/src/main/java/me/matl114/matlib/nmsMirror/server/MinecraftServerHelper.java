package me.matl114.matlib.nmsMirror.server;

import static me.matl114.matlib.nmsMirror.Import.*;

import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "net.minecraft.server.MinecraftServer")
public interface MinecraftServerHelper extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    Object getServer();

    @MethodTarget
    void sendSystemMessage(Object server, @RedirectType(ChatComponent) Iterable<?> components);

    @MethodTarget
    Object getPlayerList(Object server);
}
