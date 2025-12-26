package me.matl114.matlib.implement.nms.network;

import static me.matl114.matlib.nmsMirror.impl.NMSNetwork.CONNECTION;

import com.google.common.base.Preconditions;
import com.mojang.authlib.GameProfile;
import io.netty.channel.Channel;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.ToString;
import me.matl114.matlib.algorithms.dataStructures.frames.cache.Metadata;
import me.matl114.matlib.nmsMirror.impl.NMSLevel;
import me.matl114.matlib.nmsUtils.network.ConnectionLifeCycle;

@ToString
@Getter
public class ClientInformation {
    public ClientInformation(SimpleChannelInboundHandler<?> conection, Object player) {
        Preconditions.checkArgument(CONNECTION.isConnection(conection));
        Preconditions.checkArgument(NMSLevel.PLAYER.isPlayer(player));
        this.originalChannel = CONNECTION.channelGetter(conection);
        initConnection(conection, player);

        //
    }

    protected void initConnection(SimpleChannelInboundHandler<?> conection, Object player) {
        this.connection = conection;
        this.player = player;
        this.state = ConnectionLifeCycle.PLAY;
        this.play = NMSLevel.PLAYER.connectionGetter(this.player);
        this.all = true;
        this.gameProfile = NMSLevel.PLAYER.getGameProfile(player);
    }

    public ClientInformation(Channel channel) {
        this.originalChannel = Objects.requireNonNull(channel);
        this.state = null;
        this.all = false;
    }

    public boolean ready() {
        return state == ConnectionLifeCycle.PLAY;
    }

    public boolean deprecated() {
        return state == ConnectionLifeCycle.DISCONNECT;
    }

    SimpleChannelInboundHandler<?> connection;

    @Nonnull
    final Channel originalChannel;

    Object player;
    GameProfile gameProfile;
    ConnectionLifeCycle state;
    boolean all;
    Object play;
    final Metadata metadata = new Metadata();

    public void addDisconnectListener(GenericFutureListener<? extends Future<? super Void>> listener) {
        originalChannel.closeFuture().addListener(listener);
    }
}
