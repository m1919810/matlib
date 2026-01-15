package me.matl114.matlib.unitTest;

import me.matl114.matlib.core.nms.network.PacketEvent;
import me.matl114.matlib.core.nms.network.PacketHandler;
import me.matl114.matlib.core.nms.network.PacketListener;
import me.matl114.matlib.nmsUtils.network.GamePacket;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;

public class TestPacketListener implements PacketListener {

//    @PacketHandler(type = GamePacket.CLIENTBOUND_PLAYER_POSITION)
//    public void onBounceback(PacketEvent event) {
//        Debug.logger("On player position packet", event.getClient().getPlayer());
//    }
//
//    @PacketHandler(type = GamePacket.SERVERBOUND_PLAYER_COMMAND)
//    public void onClientCommand(PacketEvent event) {
//        Debug.logger("On Client Command Receive", ReflectUtils.dumpObject(event.getPacket()));
//    }
}
