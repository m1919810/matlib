package me.matl114.matlib.unitTest.autoTests.nmsTests;

import com.google.gson.JsonElement;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.PairList;
import me.matl114.matlib.implement.nms.serialization.ItemStackNbtCodec;
import me.matl114.matlib.implement.serialization.ItemStackCodec;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.DynamicOpUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.nbt.ItemMetaView;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.serialization.CodecUtils;
import me.matl114.matlib.utils.serialization.StringifyOps;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class ItemMatchTests implements TestCase {
    @OnlineTest(name = "test fish item match")
    public void test_bigitem() {
        ItemStackNbtCodec.init();
        for (var iter : getTestCases("itemstack/big_fish_item.json")) {
            JsonElement element = iter.getValue().getAsJsonObject();
            ItemStackCodec itemCodec =
                    CodecUtils.decode(ItemStackCodec.CODEC, new StringifyOps<>(DynamicOpUtils.jsonOp()), element);

            ItemStack itemStack = itemCodec.getItemStack();
            Random rand = new Random();
            // nm
            PairList<ItemKey, Object> list = new PairList<>();
            for (var i = 0; i < 1_000_000; ++i) {
                ItemStack stackCopy = itemStack.clone();
                Object nms = ItemUtils.unwrapHandle(stackCopy);
                double d = rand.nextDouble();
                ItemMetaView metaView = ItemMetaView.of(nms);
                metaView.getPersistentDataContainer()
                        .set(NamespacedKey.fromString("magicexpansion:fish_weight"), PersistentDataType.DOUBLE, d);
                metaView.removeLore(2);
                metaView.addLoreAt(
                        1,
                        TextUtils.resolveColor(
                                "&x&f&d&b&7&d&4重&x&f&b&9&1&b&e量&x&f&b&7&7&b&3:&x&f&f&6&9&b&4 &f%.3f kg".formatted(d)));
                list.put(new ItemKey(nms), new Object());
            }
            Debug.logger("check sample");
            Debug.logger(ItemUtils.asBukkitCopy(list.get(0).getA().nms));
            Map<ItemKey, Object> obj = new ConcurrentHashMap<>();
            for (var entry : list) {
                obj.put(entry.getA(), entry.getB());
            }
            long startTime = System.nanoTime();
            for (var entry : list) {
                Assert(obj.get(entry.getA()) == entry.getB());
            }
            long endTime = System.nanoTime();
            Debug.logger("using time", endTime - startTime);
        }
    }

    @AllArgsConstructor
    public static class ItemKey {
        public ItemKey(Object nms) {
            this.nms = nms;
        }

        Object nms;

        @Override
        public boolean equals(Object obj) {
            return obj instanceof ItemKey key && NMSItem.ITEMSTACK.matchItem(nms, key.nms, true, true);
        }

        int integer;

        @Override
        public int hashCode() {
            if (integer == 0) {
                integer = NMSItem.ITEMSTACK.customHashWithoutDisplay(nms);
            }
            return integer;
        }
    }
}
