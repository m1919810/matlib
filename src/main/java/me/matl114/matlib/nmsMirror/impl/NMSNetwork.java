package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.network.*;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;

public class NMSNetwork {
    public static final ServerConnectionHelper SERVER_CONNECTION;
    public static final ConnectionHelper CONNECTION;
    public static final PacketAPI PACKETS;
    public static final ServerGamePacketListenerImplHelper PLAY;
    public static final SyncherHelper SYNCHER;

    static {
        SERVER_CONNECTION = DescriptorImplBuilder.createMultiHelper(ServerConnectionHelper.class);
        CONNECTION = DescriptorImplBuilder.createMultiHelper(ConnectionHelper.class);
        PACKETS = DescriptorImplBuilder.createMultiHelper(PacketAPI.class);
        PLAY = DescriptorImplBuilder.createHelperImpl(ServerGamePacketListenerImplHelper.class);
        SYNCHER = DescriptorImplBuilder.createMultiHelper(SyncherHelper.class);
    }
}
