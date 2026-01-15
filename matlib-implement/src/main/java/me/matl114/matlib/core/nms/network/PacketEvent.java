package me.matl114.matlib.core.nms.network;

import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.designs.event.Event;
import me.matl114.matlib.nmsUtils.network.GamePacket;

public class PacketEvent extends Event {
    @Nonnull
    final Object packet;

    final boolean s2c;
    final GamePacket enumType;

    @Nonnull
    final ClientInformation info;

    final boolean bundle;

    public PacketEvent(@Nonnull Object packet, boolean s2c, @Nonnull ClientInformation info, boolean bundle) {
        this.packet = packet;
        this.s2c = s2c;
        this.info = info;
        // left as not complete
        this.enumType = GamePacket.ClassMap.getType(packet);
        this.bundle = bundle;
    }

    protected PacketEvent(GamePacket type, ClientInformation info) {
        this.packet = null;
        this.s2c = false;
        this.enumType = type;
        this.info = info;
        bundle = false;
    }

    public Object getPacket() {
        return this.packet;
    }

    public GamePacket getType() {
        return this.enumType;
    }

    public ClientInformation getClient() {
        return this.info;
    }

    public boolean isFromBundle() {
        return this.bundle;
    }
}
