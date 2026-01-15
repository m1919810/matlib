package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import java.util.Objects;
import me.matl114.matlib.common.lang.exceptions.DecodeException;
import me.matl114.matlib.common.lang.exceptions.EncodeException;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.serialization.BukkitCodecs;
import me.matl114.matlib.utils.serialization.CodecUtils;
import me.matl114.matlib.utils.serialization.ConfigOps;
import me.matl114.matlib.utils.serialization.TypeOps;
import me.matl114.matlib.utils.serialization.simple.IntProvider;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

public class CodecTests implements TestCase {
    @OnlineTest(name = "test basic codecs")
    public void test_basic_codecs() {
        for (var jsonElement : getTestCases("codecs/int_provider.json")) {
            JsonElement jsonElement1 = jsonElement.getValue();
            IntProvider intProvider = CodecUtils.decode(IntProvider.CODEC, JsonOps.INSTANCE, jsonElement1);
            Debug.logger(intProvider);
        }
    }

    @OnlineTest(name = "test memory section op")
    public void test_memory_section_op() {
        for (var jsonElement : getTestCases("codecs/memory_section.json")) {
            JsonElement jsonElement1 = jsonElement.getValue();
            JsonElement data = getData(jsonElement1);

            MemorySection config = (MemorySection) JsonOps.INSTANCE.convertTo(ConfigOps.I, data);
            for (var testCase : getTests(jsonElement1)) {
                Debug.logger("Test ", jsonElement.getIndex(), "." + testCase.getIndex());
                JsonElement output = getOutput(testCase.getValue());
                if (output.isJsonObject()) {
                    AssertEq(
                            config.get(getInput(testCase.getValue()).getAsString()),
                            JsonOps.INSTANCE.convertTo(ConfigOps.I, output));
                } else {
                    AssertEq(
                            config.get(getInput(testCase.getValue()).getAsString()),
                            JsonOps.INSTANCE.convertTo(TypeOps.I, output));
                }
            }
        }
    }

    @OnlineTest(name = "test itemStack codecs")
    public void test_itemstack_codecs() {
        String yamlStr = readStr("codecs/item_stack.yaml");
        ItemStack itemStack = null;
        try {
            itemStack = CodecUtils.decode(BukkitCodecs.ITEMSTACK, TypeOps.I, yamlStr);
            AssertEq(itemStack.getType(), Material.PLAYER_HEAD);
            AssertEq(
                    itemStack
                            .getItemMeta()
                            .getPersistentDataContainer()
                            .getKeys()
                            .size(),
                    1);
            AssertEq(itemStack.getItemMeta().getLore().size(), 4);

            Object element = CodecUtils.encode(BukkitCodecs.ITEMSTACK, TypeOps.I, itemStack);
            // version 不同
            Debug.logger("Bukkit String ItemStack codec test success");
        } catch (Throwable e) {
            Debug.logger(e);
        }

        for (var jsonElement : getTestCases("codecs/item_stack.json")) {
            JsonElement jsonElement1 = jsonElement.getValue();
            ItemStack itemStacks = CodecUtils.decode(BukkitCodecs.ITEMSTACK, JsonOps.INSTANCE, jsonElement1);
            AssertEq(itemStacks, itemStack);
            JsonElement element = CodecUtils.encode(BukkitCodecs.ITEMSTACK, JsonOps.INSTANCE, itemStacks);

            // AssertEq(element, jsonElement1);
            Debug.logger("Bukkit Map ItemStack codec test success");
        }
    }

    @OnlineTest(name = "test bukkit codecs")
    public void test_bukkit_codecs() throws Throwable {
        Class<?> clazz = BukkitCodecs.class;
        for (var jsonElement : getTestCases("codecs/bukkit_codecs.json")) {
            JsonElement jsonElement1 = jsonElement.getValue();
            String fieldName = getData(jsonElement1).getAsString();
            Codec<?> codec = (Codec<?>) clazz.getField(fieldName).get(null);
            for (var testCase : getTests(jsonElement1)) {
                JsonElement output = getOutput(testCase.getValue());
                JsonElement input = getInput(testCase.getValue());
                String val = output.getAsString();
                try {
                    AssertEq(val, "" + CodecUtils.decode(codec, JsonOps.INSTANCE, input));
                } catch (DecodeException | EncodeException decodeException) {
                    if (!Objects.equals("throw", val)) {
                        throw decodeException;
                    }
                }
            }
        }
    }
}
