package me.matl114.matlib.nmsMirror.network;

public interface PacketType {
    String ServerboundHelloPacket = "Lnet/minecraft/network/protocol/login/ServerboundHelloPacket;";
    String ClientboundLoginFinishPacket = "Lnet/minecraft/network/protocol/login/ClientboundLoginFinishedPacket;";
    String ClientboundLoginPacket = "Lnet/minecraft/network/protocol/game/ClientboundLoginPacket;";
    String BundlePacket = "Lnet/minecraft/network/protocol/BundlePacket;";

    String ServerboundSetCreativeModeSlotPacket =
            "Lnet/minecraft.network/protocol/game/ServerboundSetCreativeModeSlotPacket;";

    String ClientboundSetCursorItemPacket = "Lnet/minecraft.network/protocol/game/ClientboundSetCursorItemPacket;";

    String ServerboundContainerClickPacket = "Lnet/minecraft.network/protocol/game/ServerboundContainerClickPacket;";

    String ClientboundSetPlayerInventoryPacket =
            "Lnet/minecraft.network/protocol/game/ClientboundSetPlayerInventoryPacket;";

    String ClientboundContainerSetSlotPacket =
            "Lnet/minecraft.network/protocol/game/ClientboundContainerSetSlotPacket;";
    String ClientboundSetEquipmentPacket = "Lnet/minecraft.network/protocol/game/ClientboundSetEquipmentPacket;";

    String ClientboundContainerSetContentPacket =
            "Lnet/minecraft.network/protocol/game/ClientboundContainerSetContentPacket;";
    String ClientboundSetEntityDataPacket = "Lnet/minecraft.network/protocol/game/ClientboundSetEntityDataPacket;";

    String ClientboundMerchantOffersPacket = "Lnet/minecraft.network/protocol/game/ClientboundMerchantOffersPacket;";

    String ServerboundClientInformationPacket =
            "Lnet/minecraft.network/protocol/common/ServerboundClientInformationPacket;";
    String ServerboundInteractEntityPacket =
        "Lnet/minecraft/network/protocol/game/ServerboundInteractPacket;";

}
