package me.matl114.matlib.unitTest.autoTests.dependTests;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import me.matl114.matlib.algorithms.dataStructures.frames.lazyCollection.LazyArray;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.stackCache.ItemStackMetaCache;
import org.bukkit.inventory.ItemStack;

public class SlimefunItemMatchTests implements TestCase {
    // lets create a item match test to estimate these
    // in the form of craftItemStack

    @OnlineTest(name = "Slimefun items matches tests")
    public void test_itemMatches() throws Throwable {
        List<ItemStack> allSlimefunItems = Slimefun.getRegistry().getAllSlimefunItems().stream()
                .map(SlimefunItem::getItem)
                .map(ItemUtils::copyStack)
                .toList();
        // use clean ItemStack for copy,to avoid potential copy-on-write privilege
        List<ItemStack> cloned = allSlimefunItems.stream()
                .map(CleanItemStack::new)
                .map(ItemUtils::copyStack)
                .toList();
        boolean matchLore = true;
        long a = System.nanoTime();
        ItemProcessor<ItemStack> processor1 = (cis) -> cis;
        ItemMatcher<ItemStack> matcher1 = ItemUtils::matchItemStack;
        boolean[][] amount1 = crossMatch(allSlimefunItems, cloned, processor1, matcher1, matchLore);
        long b = System.nanoTime();
        Debug.logger("match result for 1st", b - a, "ns");
        a = System.nanoTime();
        ItemProcessor<ItemStackMetaCache> processor2 = ItemStackMetaCache::get;
        ItemMatcher<ItemStackMetaCache> matcher2 = (ItemStackMetaCache::matchItem);
        boolean[][] amount2 = crossMatch(allSlimefunItems, cloned, processor2, matcher2, matchLore);
        b = System.nanoTime();
        Debug.logger("match result for 2st", b - a, "ns");
        a = System.nanoTime();
        ItemProcessor<ItemStackMetaCache> processor3 = ItemStackMetaCache::get;
        ItemMatcher<ItemStackMetaCache> matcher3 = (t, w, c) -> {
            return t.getType() == w.getType() && (Objects.equals(t.getMeta(), w.getMeta()));
        };
        boolean[][] amount3 = crossMatch(allSlimefunItems, cloned, processor3, matcher3, matchLore);
        b = System.nanoTime();
        Debug.logger("match result for 3st", b - a, "ns");
        Class<?> networkUtils = Class.forName("io.github.sefiraat.networks.utils.StackUtils");
        Method method = Arrays.stream(networkUtils.getMethods())
                .filter(m -> m.getName().equals("itemsMatchCore"))
                .findAny()
                .orElseThrow();
        NetworkMatcher matcher = LambdaUtils.createLambdaForStaticMethod(NetworkMatcher.class, method);
        Class<?> cacheUtils = Class.forName("io.github.sefiraat.networks.network.stackcaches.ItemStackCache");
        Method method1 = cacheUtils.getMethod("of", ItemStack.class);
        ItemProcessor processor4 = LambdaUtils.createLambdaForStaticMethod(ItemProcessor.class, method1);
        ItemMatcher<?> matcher4 = (t, w, c) -> matcher.match(t, w, c, false, true);
        a = System.nanoTime();
        boolean[][] amount4 = crossMatch(allSlimefunItems, cloned, processor4, matcher4, matchLore);
        b = System.nanoTime();
        Debug.logger("match result for 4st", b - a, "ns");
        Debug.logger("check correctness");
        int size = allSlimefunItems.size();
        Debug.logger("match finish, total", size, "item matches");
        int cnt = 0;
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                boolean val1 = amount1[i][j];
                boolean val2 = amount2[i][j];
                boolean val3 = amount3[i][j];
                boolean val4 = amount4[i][j];
                if (val1 != val2 || val2 != val3 || val3 != val4) {
                    Debug.logger("Unmatched result at", i, j, "itemsatck");
                    Debug.logger(allSlimefunItems.get(i));
                    Debug.logger(allSlimefunItems.get(j));
                    Debug.logger("resulting:", val1, val2, val3, val4);
                } else {
                    if (val1) {
                        cnt += 1;
                    }
                }
            }
        }
        Debug.logger("total ", cnt, "pair of equal itemStack");
    }

    private <T> boolean[][] crossMatch(
            List<ItemStack> cis,
            List<ItemStack> totallyCopied,
            ItemProcessor<T> processor,
            ItemMatcher<T> matcher,
            boolean match) {
        int size = cis.size();
        LazyArray<T> val = new LazyArray<>((is) -> (T[]) new Object[is], size, (is) -> processor.process(cis.get(is)));
        LazyArray<T> val2 =
                new LazyArray<>((is) -> (T[]) new Object[is], size, (is) -> processor.process(totallyCopied.get(is)));
        boolean[][] values = new boolean[size][size];
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                values[i][j] = matcher.match(val.get(i), val2.get(j), match);
            }
        }
        return values;
    }

    public static interface ItemProcessor<T extends Object> {
        public T process(ItemStack val);
    }

    public static interface ItemMatcher<T extends Object> {
        public boolean match(T a1, T a2, boolean matchLore);
    }

    public static interface NetworkMatcher {
        public boolean match(
                Object val1, Object val2, boolean checkLore, boolean checkAmount, boolean checkCustomModelId);
    }
}
