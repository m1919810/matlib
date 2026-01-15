package me.matl114.matlib.unitTest.autoTests.nmsTests;

import java.util.ArrayList;
import me.matl114.matlib.nmsMirror.impl.NMSCore;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.ServerUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import org.bukkit.inventory.ItemStack;

public class ItemDataTests implements TestCase {
    @OnlineTest(name = "test item nbtData save and show")
    public void test_byd1() {
        ItemStack stack = ItemMetaViewTests.generateComplex();
        Object nms = ItemUtils.unwrapHandle(stack);
        Debug.logger(NMSItem.ITEMSTACK.saveNbtAsTag(nms));
        Object val = NMSItem.ITEMSTACK.saveNbtAsTag(nms);
        Debug.logger(NMSCore.TAGS.printAsSnbt(val, "", 0, new ArrayList<>()));
        Debug.logger(NMSCore.TAGS.printAsNbtString(val));
        Debug.logger(NMSCore.TAGS.printAsChatComponent(val, ""));
        ServerUtils.broadCastMessage(NMSCore.TAGS.printAsChatComponent(val, ""));
    }
}
