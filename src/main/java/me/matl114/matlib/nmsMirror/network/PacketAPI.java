package me.matl114.matlib.nmsMirror.network;

import static me.matl114.matlib.nmsMirror.Import.*;
import static me.matl114.matlib.nmsMirror.network.PacketType.*;

import com.mojang.authlib.GameProfile;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import java.util.ArrayList;
import java.util.List;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.*;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import me.matl114.matlib.utils.version.Version;

@MultiDescriptive(targetDefault = "net.minecraft.network.protocol.Packet")
public interface PacketAPI extends TargetDescriptor {
    @CastCheck(Packet)
    boolean isPacket(Object val);

    @MethodTarget
    void handle(Object packet, @RedirectType(PacketListener) Object listener);

    // out of date
    //    @MethodTarget
    //    void write(Object packet, @RedirectType(FriendlyByteBuf)ByteBuf buf);

    @CastCheck(ServerboundHelloPacket)
    boolean isServerboundHelloPacket(Object packet);

    @CastCheck(ClientboundLoginFinishPacket)
    boolean isClientboundLoginFinishPacket(Object packet);

    @RedirectClass(ClientboundLoginFinishPacket)
    @RedirectName("gameProfileGetter")
    @FieldTarget
    GameProfile clientboundLoginFinishPacket$GameProfile(Object object);

    @CastCheck(ClientboundLoginPacket)
    boolean isClientboundLoginPacket(Object packet);

    @CastCheck(BundlePacket)
    boolean isBundlePacket(Object packet);

    @MethodTarget
    @RedirectClass(BundlePacket)
    @RedirectName("subPackets")
    Iterable<?> bundlePacket$SubPackets(Object packet);

    @ConstructorTarget
    // @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = true)
    @RedirectClass("net.minecraft.network.protocol.game.ClientboundBundlePacket")
    Object newBundle(Iterable<?> packets);
    // nothing is wrong!
    //    {
    //        return newBundle0(packets);
    //    }

    //    @ConstructorTarget
    //    @IgnoreFailure(thresholdInclude = Version.v1_20_R4, below = false)
    //    @RedirectClass("net.minecraft.network.protocol.BundlePacket")
    //    @Internal
    //    public Object newBundle0(Iterable<?> packets);

    // itemStack fields

    @FieldTarget
    @RedirectName("itemStackGetter")
    @RedirectClass(ServerboundSetCreativeModeSlotPacket)
    Object serverboundSetCreativeModeSlotPacket$itemStack(Object packet);

    // already a copy
    @MethodTarget
    @RedirectClass(ClientboundSetCursorItemPacket)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2, below = true)
    @RedirectName("contents")
    Object clientboundSetCursorItemPacket$cursor(Object packet);

    @MethodTarget
    @RedirectClass(ServerboundContainerClickPacket)
    @RedirectName("getCarriedItem")
    Object serverboundContainerClickPacket$CarriedItem(Object packet);

    @MethodTarget
    @RedirectClass(ServerboundContainerClickPacket)
    @RedirectName("getChangedSlots")
    Int2ObjectMap<?> serverboundContainerClickPacket$ChangedSlots(Object packet);

    @MethodTarget
    @RedirectClass(ClientboundSetPlayerInventoryPacket)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R2, below = true)
    @RedirectName("contents")
    Object clientboundSetPlayerInventoryPacket$SlotContent(Object packet);

    @MethodTarget
    @RedirectClass(ClientboundContainerSetSlotPacket)
    @RedirectName("getItem")
    Object clientboundContainerSetSlotPacket$SlotItem(Object packet);

    @MethodTarget
    @RedirectClass(ClientboundSetEquipmentPacket)
    @RedirectName("getSlots")
    List<?> clientboundSetEquipmentPacket$EquipmentSlots(Object packet);

    @MethodTarget
    @RedirectClass(ClientboundContainerSetContentPacket)
    @RedirectName("getItems")
    List<?> clientboundContainerSetContentPacket$getItems(Object packet);

    @MethodTarget
    @RedirectClass(ClientboundContainerSetContentPacket)
    @RedirectName("getCarriedItem")
    Object clientboundContainerSetContentPacket$getCursorItem(Object packet);

    @MethodTarget
    @RedirectClass(ClientboundSetEntityDataPacket)
    @RedirectName("packedItems")
    List<?> clientboundSetEntityDataPacket$packedValues(Object packet);

    @MethodTarget
    @RedirectClass(ClientboundMerchantOffersPacket)
    @RedirectName("getOffers")
    ArrayList<?> clientboundMerchantOffersPacket$Offstes(Object packet);

    @MethodTarget
    @RedirectClass(ServerboundClientInformationPacket)
    @RedirectName("language")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = false)
    default String serverboundClientInformationPacket$language(Object packet) {
        Object info = serverboundClientInformationPacket$information(packet);
        return clientInformation$information(info);
    }

    @Internal
    @MethodTarget
    @RedirectClass(ServerboundClientInformationPacket)
    @RedirectName("information")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = true)
    Object serverboundClientInformationPacket$information(Object packet);

    @Internal
    @MethodTarget
    @RedirectClass(ClientInformation)
    @RedirectName("language")
    @IgnoreFailure(thresholdInclude = Version.v1_20_R3, below = true)
    String clientInformation$information(Object value);
}
