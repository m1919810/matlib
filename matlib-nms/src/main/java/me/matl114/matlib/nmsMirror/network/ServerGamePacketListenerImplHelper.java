package me.matl114.matlib.nmsMirror.network;

import io.netty.channel.SimpleChannelInboundHandler;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "net.minecraft.server.network.ServerGamePacketListenerImpl")
public interface ServerGamePacketListenerImplHelper extends TargetDescriptor {
    @FieldTarget
    Object playerGetter(Object impl);

    @FieldTarget
    SimpleChannelInboundHandler<?> connectionGetter(Object impl);
}
