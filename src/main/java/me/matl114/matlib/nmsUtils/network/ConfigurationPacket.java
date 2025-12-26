package me.matl114.matlib.nmsUtils.network;

public enum ConfigurationPacket {
    CLIENTBOUND_FINISH_CONFIGURATION(PacketFlow.S2C, "finish_configuration"),
    CLIENTBOUND_REGISTRY_DATA(PacketFlow.S2C, "registry_data"),
    CLIENTBOUND_UPDATE_ENABLED_FEATURES(PacketFlow.S2C, "update_enabled_features"),
    CLIENTBOUND_SELECT_KNOWN_PACKS(PacketFlow.S2C, "select_known_packs"),
    CLIENTBOUND_RESET_CHAT(PacketFlow.S2C, "reset_chat"),
    SERVERBOUND_FINISH_CONFIGURATION(PacketFlow.C2S, "finish_configuration"),
    SERVERBOUND_SELECT_KNOWN_PACKS(PacketFlow.C2S, "select_known_packs");

    ConfigurationPacket(PacketFlow flow, String name) {}

    public static class ClassMap {}
}
