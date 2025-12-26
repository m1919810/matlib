package me.matl114.matlib.nmsMirror.network;

import static me.matl114.matlib.nmsMirror.Import.*;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.function.Supplier;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.CastCheck;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@MultiDescriptive(targetDefault = "net.minecraft.network.Connection")
public interface ConnectionHelper extends TargetDescriptor {
    @FieldTarget
    Channel channelGetter(SimpleChannelInboundHandler<?> conn);

    @MethodTarget
    SocketAddress getRemoteAddress(SimpleChannelInboundHandler<?> conn);

    //  don't know what's this for
    //    @FieldTarget
    //    @RedirectName("spoofedUUIDGetter")
    //    UUID getUUID(SimpleChannelInboundHandler<?> conn);

    // out of date
    //    @MethodTarget
    //    public void setListener(SimpleChannelInboundHandler<?> conn, @RedirectType(PacketListener)Object listener);

    @MethodTarget
    public Object getPacketListener(SimpleChannelInboundHandler<?> conn);

    @MethodTarget
    public void send(SimpleChannelInboundHandler<?> conn, @RedirectType(Packet) Object packet);

    @MethodTarget(isStatic = true)
    @RedirectClass(PacketSendListenerClass)
    Object thenRun(Runnable postTask);

    @MethodTarget(isStatic = true)
    @RedirectClass(PacketSendListenerClass)
    Object exceptionallySend(Supplier<?> failurePacket);

    @MethodTarget
    public void send(
            SimpleChannelInboundHandler<?> conn,
            @RedirectType(Packet) Object packet,
            @RedirectType(PacketSendListener) Object post);

    // having versioned problem
    //    @MethodTarget
    //    public void send(SimpleChannelInboundHandler<?> conn, @RedirectType(Packet)Object
    // packet,@RedirectType(PacketSendListener)Object post, boolean flush);

    //    @MethodTarget
    //    public void flushChannel(SimpleChannelInboundHandler<?> conn);

    @MethodTarget
    public void disconnect(SimpleChannelInboundHandler<?> conn, @RedirectType(ChatComponent) Object comp);

    @MethodTarget(isStatic = true)
    ChannelFuture connect(
            InetSocketAddress address, boolean useEpoll, @RedirectType(Connection) SimpleChannelInboundHandler<?> conn);

    @MethodTarget
    public boolean isConnected(SimpleChannelInboundHandler<?> conn);

    @MethodTarget
    Object getPlayer(SimpleChannelInboundHandler<?> conn);

    @CastCheck("net.minecraft.network.Connection")
    boolean isConnection(SimpleChannelInboundHandler<?> conn);
}
