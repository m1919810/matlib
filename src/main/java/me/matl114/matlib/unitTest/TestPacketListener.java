package me.matl114.matlib.unitTest;

import com.mojang.authlib.GameProfile;
import io.github.thebusybiscuit.slimefun4.utils.tags.SlimefunTag;
import me.matl114.matlib.implement.nms.network.ClientInformation;
import me.matl114.matlib.implement.nms.network.PacketEvent;
import me.matl114.matlib.implement.nms.network.PacketHandler;
import me.matl114.matlib.implement.nms.network.PacketListener;
import me.matl114.matlib.nmsUtils.network.GamePacket;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import org.bukkit.Tag;

public class TestPacketListener implements PacketListener {

    @PacketHandler(type = GamePacket.CLIENTBOUND_PLAYER_POSITION)
    public void onBounceback(PacketEvent event){
        Debug.logger("On player position packet", event.getClient().getPlayer());
    }


    @PacketHandler(type = GamePacket.SERVERBOUND_PLAYER_COMMAND)
    public void onClientCommand(PacketEvent event){
        Debug.logger("On Client Command Receive", ReflectUtils.dumpObject(event.getPacket()));
    }


}
