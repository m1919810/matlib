package me.matl114.matlib.nmsMirror.network;

import static me.matl114.matlib.nmsMirror.Import.*;

import io.netty.channel.ChannelFuture;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.List;
import java.util.Map;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import net.kyori.adventure.key.Key;

@MultiDescriptive(targetDefault = "net.minecraft.server.network.ServerConnectionListener")
public interface ServerConnectionHelper extends TargetDescriptor {
    @FieldTarget
    List<ChannelFuture> channelsGetter(Object instance);

    @MethodTarget
    List<? extends SimpleChannelInboundHandler<?>> getConnections(Object instance);

    @Note("if this presents, you can use lambda to bind (Channel)V method on it")
    Class<?> CHANNEL_INIT_LISTENER_ITF = me.matl114
            .matlib
            .algorithms
            .dataStructures
            .struct
            .Holder
            .empty()
            .thenApplyCaught((v) -> Class.forName(ChannelInitializeListenerClass))
            .runException((t) -> Debug.logger(t, "Error occurred while finding class", ChannelInitializeListenerClass))
            .get();

    @MethodTarget(isStatic = true)
    @RedirectClass(ChannelInitializeListenerHolderClass)
    public Object removeListener(Key key);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChannelInitializeListenerHolderClass)
    public void addListener(Key key, @RedirectType(ChannelInitializeListener) Object listener);

    @MethodTarget(isStatic = true)
    @RedirectClass(ChannelInitializeListenerHolderClass)
    public Map<Key, ?> getListeners();
}
