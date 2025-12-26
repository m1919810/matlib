package me.matl114.matlib.unitTest.autoTests.nmsTests;

import java.util.concurrent.FutureTask;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.implement.nms.network.PacketEvent;
import me.matl114.matlib.implement.nms.network.PacketEventManager;
import me.matl114.matlib.implement.nms.network.PacketHandler;
import me.matl114.matlib.implement.nms.network.PacketListener;
import me.matl114.matlib.nmsUtils.network.GamePacket;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;

public class NMSNetworkTest implements TestCase {
    @OnlineTest(name = "GamePacket lookup test")
    public void test_lookupPacket() throws Throwable {
        GamePacket.ClassMap.getClass2TypeView();
    }

    FutureTask<Void> signal;

    @OnlineTest(name = "Packet Listener test", automatic = false)
    public void test_registerListen() throws Throwable {
        signal = ExecutorUtils.signal();
        PacketEventManager.getManager().registerListener(new DemoPacketListener(), this);
        Debug.logger(PacketEventManager.getManager().getEventChannels());
        signal.get();
        Debug.logger("get the signal here!");
    }

    public class DemoPacketListener implements PacketListener {
        @PacketHandler(type = GamePacket.SERVERBOUND_CHAT_COMMAND_SIGNED)
        public void onListen0_2(PacketEvent event) {
            Debug.logger("Successfully trigger the priority 0.2 listener, why I am 0.2?");
        }

        @PacketHandler(type = GamePacket.SERVERBOUND_CHAT_COMMAND_SIGNED, priority = 1)
        public void onListen2(PacketEvent event) {
            Debug.logger("Successfully trigger the priority 1 listener");
        }

        @PacketHandler(type = GamePacket.SERVERBOUND_CHAT_COMMAND_SIGNED, priority = 2)
        public void onListen3(PacketEvent event) {
            Debug.logger("Successfully trigger the priority 2 listener");
        }

        @PacketHandler(type = GamePacket.SERVERBOUND_CHAT_COMMAND_SIGNED)
        public void onListen(PacketEvent event) {
            Debug.logger("Successfully listened the event:", event.getPacket(), event.getClient());
            signal.run();
        }

        @PacketHandler(type = GamePacket.SERVERBOUND_CHAT_COMMAND_SIGNED)
        public void onListen0_0(PacketEvent event) {
            Debug.logger("Successfully trigger the priority 0.0 listener");
        }

        @PacketHandler(type = GamePacket.SERVERBOUND_CHAT_COMMAND_SIGNED)
        public void onListen0_1(PacketEvent event) {
            Debug.logger("Successfully trigger the priority 0.1 listener");
        }
    }
}
