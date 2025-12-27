package me.matl114.matlib.nmsUtils.network;

public enum LoginPacket {
    CLIENTBOUND_CUSTOM_QUERY(PacketFlow.S2C, "custom_query"),
    CLIENTBOUND_LOGIN_FINISHED(PacketFlow.S2C, "login_finished"),
    CLIENTBOUND_HELLO(PacketFlow.S2C, "hello"),
    CLIENTBOUND_LOGIN_COMPRESSION(PacketFlow.S2C, "login_compression"),
    CLIENTBOUND_LOGIN_DISCONNECT(PacketFlow.S2C, "login_disconnect"),
    SERVERBOUND_CUSTOM_QUERY_ANSWER(PacketFlow.C2S, "custom_query_answer"),
    SERVERBOUND_HELLO(PacketFlow.C2S, "hello"),
    SERVERBOUND_KEY(PacketFlow.C2S, "key"),
    SERVERBOUND_LOGIN_ACKNOWLEDGED(PacketFlow.C2S, "login_acknowledged");

    LoginPacket(PacketFlow flow, String name) {}
}
