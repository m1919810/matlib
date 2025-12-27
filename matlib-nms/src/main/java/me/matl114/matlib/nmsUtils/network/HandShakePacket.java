package me.matl114.matlib.nmsUtils.network;

public enum HandShakePacket {
    CLIENT_INTENTION(PacketFlow.C2S, "intention");

    HandShakePacket(PacketFlow flow, String name) {}
}
