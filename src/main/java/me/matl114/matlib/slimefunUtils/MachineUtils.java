package me.matl114.matlib.slimefunUtils;

import static me.matl114.matlib.utils.CraftUtils.*;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.function.IntFunction;
import me.matl114.matlib.algorithms.dataStructures.frames.lazyCollection.LazyArray;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.common.lang.enums.Flags;
import me.matl114.matlib.implement.slimefun.manager.BlockDataCache;
import me.matl114.matlib.slimefunUtils.itemCache.*;
import me.matl114.matlib.utils.CraftUtils;
import me.matl114.matlib.utils.itemCache.*;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.inventory.ItemStack;

public class MachineUtils {
    public static ItemConsumer getConsumer(ItemStack a) {
        if (a == null) return null;
        if (a instanceof RandOutItem ro) {
            // return new ItemConsumer(a.clone());
            // 当物品是随机输出物品时候,取其中的随机实例
            return ItemConsumer.get(ro.getInstance());
        }
        return ItemConsumer.get(a);
    }

    public static ItemGreedyConsumer getGreedyConsumer(ItemStack a) {
        if (a == null) return null;
        if (a instanceof RandOutItem ro) {
            // 当物品是随机输出物品时候,取其中的随机实例
            // return new ItemConsumer(a.clone());
            return ItemGreedyConsumer.get(ro.getInstance());
        }
        return ItemGreedyConsumer.get(a);
    }

    /**
     * a huge project to adapt sth...
     * use .get(mod,inv,slot) to get ItemPusher
     * mod should be in {Settings.INPUT,Settings.OUTPUT}
     */
    public static final ItemPusherProvider getpusher = (Flags mod, ItemStack it, int slot) -> {
        if (mod == Flags.INPUT || it != null) {
            return ItemPusher.get(it);
        } else {
            return ItemSlotPusher.get(it, slot);
        }
    };

    public static ItemPusher getPusher(ItemStack it) {
        return ItemPusher.get(it);
    }
    /**
     * change the object in the slot to a different object ,in order to trigger save at this slot when server down ,
     * will return the ref to the current object in the slot
     * @return
     */
    public static ItemStack syncSlot(BlockMenu inv, int slot) {
        ItemStack item = inv.getItemInSlot(slot);
        return syncSlot(inv, slot, item);
    }
    /**
     * change the object in the slot to a different object ,in order to trigger save at this slot when server down ,
     * will return the ref to the current object in the slot
     * @return
     */
    public static ItemStack syncSlot(BlockMenu inv, int slot, ItemStack item) {
        if (item instanceof AbstractItemStack ast) {
            item = item.clone();
        }
        inv.replaceExistingItem(slot, item, false);
        return inv.getItemInSlot(slot);
    }
    /**
     * can be later modified to card-implements (water card or storage card(great idea wtf))
     * @param ?
     * @return
     */
    public static void clearAmount(BlockMenu inv, ItemPusher... counters) {
        ItemPusher ip;
        for (int i = 0; i < counters.length; ++i) {
            ip = counters[i];
            if (ip != null) {
                ip.setAmount(0);
                ip.updateMenu(inv);
            }
            // this is safe,I said it
        }
    }
    /**
     * builtin Method for developments
     */
    public static ItemConsumer[] matchRecipe(List<ItemPusher> slotCounters, MachineRecipe recipe) {
        int len2 = slotCounters.size();
        ItemStack[] recipeInput = recipe.getInput();
        int cnt = recipeInput.length;
        if (cnt > len2) return null;
        ItemConsumer[] result = new ItemConsumer[cnt];
        ItemConsumer results;
        ItemPusher itemCounter2;
        final boolean[] visited = new boolean[len2];
        for (int i = 0; i < cnt; ++i) {
            result[i] = getConsumer(recipeInput[i]);
            results = result[i];
            boolean allMatched = false;
            for (int j = 0; j < len2; ++j) {
                itemCounter2 = slotCounters.get(j);
                if (itemCounter2 == null) continue;
                if (!visited[j]) {
                    itemCounter2.syncData();
                    visited[j] = true;
                } else if (itemCounter2.isDirty()) {
                    continue;
                }
                if (CraftUtils.matchItemCounter(results, itemCounter2, false)) {
                    results.consume(itemCounter2);
                    if (results.getAmount() <= 0) {
                        allMatched = true;
                        break;
                    }
                }
            }
            if (!allMatched) {
                return null;
            }
        }
        return result;
    }

    /**
     * a better version of matchRecipe for
     * @param slotCounters
     * @param recipe
     * @return
     */
    public static ItemGreedyConsumer[] matchMultiRecipe(
            List<ItemPusher> slotCounters, MachineRecipe recipe, int maxMatchCount) {
        int len2 = slotCounters.size();
        ItemStack[] recipeInput = recipe.getInput();
        int cnt = recipeInput.length;
        if (cnt > len2) return null;
        ItemGreedyConsumer[] result = new ItemGreedyConsumer[cnt];
        // 模拟时间加速 减少~
        maxMatchCount = calMaxCraftAfterAccelerate(maxMatchCount, recipe.getTicks());
        if (maxMatchCount == 0) {
            return null;
        }
        // int maxAmount;
        final boolean[] visited = new boolean[len2];
        for (int i = 0; i < cnt; ++i) {
            ItemGreedyConsumer itemCounter = getGreedyConsumer(recipeInput[i]);
            // in case some idiots! put 0 in recipe
            // maxAmount=Math.min( itemCounter.getAmount()*maxMatchCount,1);
            for (int j = 0; j < len2; ++j) {
                ItemPusher itemCounter2 = slotCounters.get(j);
                if (itemCounter2 == null) continue;
                if (!visited[j]) {
                    itemCounter2.syncData();
                    visited[j] = true;
                } else if (itemCounter2.isDirty()) {
                    continue;
                }
                if (CraftUtils.matchItemCounter(itemCounter, itemCounter2, false)) {
                    itemCounter.consume(itemCounter2);
                    if (itemCounter.getStackNum() >= maxMatchCount) break;
                }
            }
            // 不够一份的量
            if (itemCounter.getStackNum() < 1) {
                return null;
            }
            result[i] = itemCounter;
        }
        return result;
    }

    /**
     * try input every OutputItem in slotCounters, just a plan ,will return plans
     * @param
     * @param recipe
     * @return
     */
    public static ItemConsumer[] countOneOutput(BlockMenu inv, int[] output, MachineRecipe recipe) {
        return countOneOutput(inv, output, recipe, getpusher);
    }

    public static ItemConsumer[] countOneOutput(
            BlockMenu inv, int[] output, MachineRecipe recipe, ItemPusherProvider pusher) {
        int len2 = output.length;
        ItemStack[] recipeInput = recipe.getOutput();
        int cnt = recipeInput.length;
        ItemConsumer[] result = new ItemConsumer[cnt];
        LazyArray<ItemPusher> slotCounters =
                new LazyArray<>(ItemPusher[]::new, len2, pusher.getMenuInstance(Flags.OUTPUT, inv, output));
        for (int i = 0; i < cnt; ++i) {
            result[i] = getConsumer(recipeInput[i]);
            for (int j = 0; j < len2; ++j) {
                if (result[i].getAmount() <= 0) break;
                ItemPusher itemCounter2 = slotCounters.get(j);
                if (itemCounter2.getItem() == null) {
                    itemCounter2.setFrom(result[i]);
                    itemCounter2.grab(result[i]);
                    result[i].addRelate(itemCounter2);

                } else if (itemCounter2.isDirty()) {
                    continue;
                } else if (CraftUtils.matchItemCounter(itemCounter2, result[i], false)) {
                    itemCounter2.grab(result[i]);
                    result[i].addRelate(itemCounter2);
                }
            }
            if (result[i].getAmount() > 0) return null;
        }
        return result;
    }

    /**
     * match if one time of this recipe can be crafted
     * @param inv
     * @param input
     * @param output
     * @param recipe
     * @return
     */
    public static Pair<ItemConsumer[], ItemConsumer[]> countOneRecipe(
            BlockMenu inv, int[] input, int[] output, MachineRecipe recipe) {
        return countOneRecipe(inv, input, output, recipe, getpusher);
    }

    public static Pair<ItemConsumer[], ItemConsumer[]> countOneRecipe(
            BlockMenu inv, int[] input, int[] output, MachineRecipe recipe, ItemPusherProvider pusher) {
        int len = input.length;
        ItemStack[] recipeIn = recipe.getInput();
        int cnt = recipeIn.length;
        LazyArray<ItemPusher> inputs =
                new LazyArray<>(ItemPusher[]::new, len, pusher.getMenuInstance(Flags.INPUT, inv, input));

        ItemConsumer[] inputInfo = matchRecipe(inputs, recipe);
        if (inputInfo != null) {
            ItemConsumer[] outputInfo = countOneOutput(inv, output, recipe, pusher);
            if (outputInfo != null) {
                return new Pair<>(inputInfo, outputInfo);
            }
        }
        return null;
    }
    /**
     * match max craft time of recipe ,return null if cannot craft return the recorded information of inputConsumer and outputConsumner
     * @param inv
     * @param input
     * @param output
     * @param recipe
     * @param limit
     * @return
     */
    public static Pair<ItemGreedyConsumer[], ItemGreedyConsumer[]> countMultiRecipe(
            BlockMenu inv, int[] input, int[] output, MachineRecipe recipe, int limit) {
        return countMultiRecipe(inv, input, output, recipe, limit, getpusher);
    }

    public static Pair<ItemGreedyConsumer[], ItemGreedyConsumer[]> countMultiRecipe(
            BlockMenu inv, int[] input, int[] output, MachineRecipe recipe, int limit, ItemPusherProvider pusher) {
        int len = input.length;
        ItemStack[] recipeInput = recipe.getInput();
        LazyArray<ItemPusher> inputCounters =
                new LazyArray<>(ItemPusher[]::new, len, pusher.getMenuInstance(Flags.INPUT, inv, input));
        int cnt = recipeInput.length;

        ItemGreedyConsumer[] recipeCounter = new ItemGreedyConsumer[cnt];
        int maxAmount = limit;
        final boolean[] visited = new boolean[len];
        for (int i = 0; i < cnt; ++i) {
            recipeCounter[i] = getGreedyConsumer(recipeInput[i]);
            for (int j = 0; j < len; ++j) {
                ItemPusher itemCounter2 = inputCounters.get(j);
                if (itemCounter2 == null) continue;
                if (!visited[j]) {
                    itemCounter2.syncData();
                    visited[j] = true;
                } else if (itemCounter2.isDirty()) {
                    // 如果该counter已经被人绑定了 就跳过
                    continue;
                }
                if (CraftUtils.matchItemCounter(itemCounter2, recipeCounter[i], false)) {
                    // 如果匹配 将其加入...list,并算入matchCnt
                    recipeCounter[i].addRelate(itemCounter2);
                    recipeCounter[i].addMatchAmount(itemCounter2.getAmount());
                    if (recipeCounter[i].getStackNum() >= maxAmount) break;
                }
            }
            int stackAmount = recipeCounter[i].getStackNum();
            if (stackAmount >= maxAmount) continue;
            maxAmount = Math.min(maxAmount, stackAmount);
            if (maxAmount <= 0) return null;
        }
        // Debug.logger("see match input amount "+maxAmount);
        ItemGreedyConsumer[] recipeCounter2 = countMultiOutput(recipeCounter, inv, output, recipe, maxAmount, pusher);

        return recipeCounter2 != null ? new Pair<>(recipeCounter, recipeCounter2) : null;
    }

    /**
     * return null if no match ,return ItemGreedyConsumer with modification of inputInfo and outputConsumer with written matchAMount
     * @param inputInfo
     * @param inv
     * @param output
     * @param recipe
     * @param limit
     * @return
     */
    public static ItemGreedyConsumer[] countMultiOutput(
            ItemGreedyConsumer[] inputInfo, BlockMenu inv, int[] output, MachineRecipe recipe, int limit) {
        return countMultiOutput(inputInfo, inv, output, recipe, limit, getpusher);
    }

    /**
     * this method has a step reading optimize
     * @param inputInfo
     * @param inv
     * @param output
     * @param recipe
     * @param limit
     * @param pusher
     * @return
     */
    public static ItemGreedyConsumer[] countMultiOutput(
            ItemGreedyConsumer[] inputInfo,
            BlockMenu inv,
            int[] output,
            MachineRecipe recipe,
            int limit,
            ItemPusherProvider pusher) {

        int len2 = output.length;
        LazyArray<ItemPusher> outputCounters =
                new LazyArray<>(ItemPusher[]::new, len2, pusher.getMenuInstance(Flags.OUTPUT, inv, output));
        ItemStack[] recipeOutput = recipe.getOutput();
        int cnt2 = recipeOutput.length;
        ItemGreedyConsumer[] recipeCounter2 = new ItemGreedyConsumer[cnt2];
        int maxAmount2 = limit;
        if (cnt2 <= 0 || maxAmount2 <= 0) {
            return null;
        } // 优化 当一个输出的时候 直接输出 匹配最大的匹配数
        // 99%的情况都是这样的
        // 应该不会有很多2b作者给这么高效的机器设置两个输出
        else if (cnt2 == 1) {
            recipeCounter2[0] = getGreedyConsumer(recipeOutput[0]);
            for (int i = 0; i < len2; ++i) {
                ItemPusher itemCounter = outputCounters.get(i);
                if (itemCounter.getItem() == null) {
                    itemCounter.setFrom(recipeCounter2[0]);
                    recipeCounter2[0].addRelate(itemCounter);
                    recipeCounter2[0].addMatchAmount(recipeCounter2[0].getMaxStackCnt());
                } else if (itemCounter.getMaxStackCnt() <= itemCounter.getAmount()) {
                    continue;
                } else if (CraftUtils.matchItemCounter(recipeCounter2[0], itemCounter, false)) {
                    recipeCounter2[0].addRelate(itemCounter);
                    recipeCounter2[0].addMatchAmount(itemCounter.getMaxStackCnt() - itemCounter.getAmount());
                }
                if (recipeCounter2[0].getStackNum() >= maxAmount2) {
                    break;
                }
            }
            maxAmount2 = Math.min(recipeCounter2[0].getStackNum(), maxAmount2);
            if (maxAmount2 <= 0) {
                return null;
            }
        }
        // 如果真的有,你喜欢就好
        // 有可能是桶或者什么
        else {
            // 维护一下当前matchAmount最小值
            PriorityQueue<ItemGreedyConsumer> priorityRecipeOutput = new PriorityQueue<>(cnt2 + 1);
            for (int i = 0; i < cnt2; ++i) {
                recipeCounter2[i] = getGreedyConsumer(recipeOutput[i]);
                priorityRecipeOutput.add(recipeCounter2[i]);
            }
            while (true) {
                ItemGreedyConsumer itemCounter2 = priorityRecipeOutput.poll();
                if (itemCounter2 == null) {
                    break;
                }
                boolean hasNextPushSlot = false;
                for (int j = 0; j < len2; ++j) {
                    ItemPusher itemCounter = outputCounters.get(j);
                    if (itemCounter.getItem() == null) {
                        itemCounter.setFrom(itemCounter2);
                        itemCounter2.addRelate(itemCounter);
                        // can output maxCnt amount
                        itemCounter2.addMatchAmount(itemCounter2.getMaxStackCnt());
                        hasNextPushSlot = true;
                        break;
                    } else if (itemCounter.isDirty() || itemCounter.getMaxStackCnt() <= itemCounter.getAmount()) {
                        continue;
                    } else if (CraftUtils.matchItemCounter(itemCounter2, itemCounter, false)) {
                        // what the fuck????
                        // 为什么他妈的会覆盖啊 谁写的答辩玩意啊
                        // itemCounter.setFrom(itemCounter2);
                        itemCounter2.addRelate(itemCounter);
                        // must use itemCounter.getMaxStackCnt because ItemStorage
                        itemCounter2.addMatchAmount(itemCounter.getMaxStackCnt() - itemCounter.getAmount());
                        hasNextPushSlot = true;
                        break;
                    }
                }
                // ?
                // 这是什么鬼玩意
                // 哦
                // 要判断是否有nextSlot
                if (hasNextPushSlot && itemCounter2.getStackNum() <= maxAmount2) {
                    priorityRecipeOutput.add(itemCounter2);
                } else {
                    // 这是不会再增加了 作为一个上线
                    maxAmount2 = Math.min(maxAmount2, itemCounter2.getStackNum());
                    if (maxAmount2 <= 0) return null;
                }
            }
        }
        for (int i = 0; i < cnt2; ++i) {
            recipeCounter2[i].setStackNum(maxAmount2);
        }
        int cnt = inputInfo.length;
        for (int i = 0; i < cnt; ++i) {
            inputInfo[i].setStackNum(maxAmount2);
        }
        return recipeCounter2;
    }

    /**
     * this method do not have step reading optimiz,should provide pre-get ItemPushers,used in Places where outputslot are frequently visited
     * we abandon this method because we have better access to dynamicly load ItemPushers
     * @param inputInfo
     * @param output
     * @param recipe
     * @param limit
     * @return
     */
    @Deprecated
    public static ItemGreedyConsumer[] countMultiOutput(
            ItemGreedyConsumer[] inputInfo, ItemPusher[] output, MachineRecipe recipe, int limit) {

        int len2 = output.length;
        int outputSlotpointer = 0;
        ItemStack[] recipeOutput = recipe.getOutput();
        int cnt2 = recipeOutput.length;
        ItemGreedyConsumer[] recipeCounter2 = new ItemGreedyConsumer[cnt2];
        int maxAmount2 = limit;
        if (cnt2 <= 0) {
            return recipeCounter2;
        } // 优化 当一个输出的时候 直接输出 匹配最大的匹配数
        // 99%的情况都是这样的
        // 应该不会有很多2b作者给这么高效的机器设置两个输出
        else if (cnt2 == 1) {
            recipeCounter2[0] = getGreedyConsumer(recipeOutput[0]);

            for (; outputSlotpointer < len2; ) {
                ItemPusher itemCounter = output[outputSlotpointer];
                ++outputSlotpointer;
                if (itemCounter.getItem() == null) {
                    itemCounter.setFrom(recipeCounter2[0]);
                    recipeCounter2[0].addRelate(itemCounter);
                    recipeCounter2[0].addMatchAmount(recipeCounter2[0].getMaxStackCnt());
                } else if (itemCounter.getMaxStackCnt() <= itemCounter.getAmount()) {
                    continue;
                } else if (CraftUtils.matchItemCounter(recipeCounter2[0], itemCounter, false)) {
                    recipeCounter2[0].addRelate(itemCounter);
                    recipeCounter2[0].addMatchAmount(recipeCounter2[0].getMaxStackCnt() - itemCounter.getAmount());
                }
                if (recipeCounter2[0].getStackNum() >= maxAmount2) {
                    break;
                }
            }
            maxAmount2 = Math.min(recipeCounter2[0].getStackNum(), maxAmount2);

            if (maxAmount2 <= 0) {
                return null;
            }
        }
        // 如果真的有,你喜欢就好
        // 有可能是桶或者什么
        else {
            // 维护一下当前matchAmount最小值
            PriorityQueue<ItemGreedyConsumer> priorityRecipeOutput = new PriorityQueue<>(cnt2 + 1);
            for (int i = 0; i < cnt2; ++i) {
                recipeCounter2[i] = getGreedyConsumer(recipeOutput[i]);
                priorityRecipeOutput.add(recipeCounter2[i]);
            }
            while (true) {
                ItemGreedyConsumer itemCounter2 = priorityRecipeOutput.poll();
                if (itemCounter2 == null) {
                    break;
                }
                boolean hasNextPushSlot = false;
                for (int j = 0; j < len2; ++j) {
                    ItemPusher itemCounter = output[j];
                    if (itemCounter.isDirty() || itemCounter.getMaxStackCnt() <= itemCounter.getAmount()) {
                        continue;
                    } else if (CraftUtils.matchItemCounter(itemCounter2, itemCounter, false)) {
                        itemCounter2.addRelate(itemCounter);
                        itemCounter2.addMatchAmount(itemCounter2.getMaxStackCnt() - itemCounter.getAmount());
                        hasNextPushSlot = true;
                        break;
                    }
                }
                if (hasNextPushSlot && itemCounter2.getStackNum() <= maxAmount2) {
                    priorityRecipeOutput.add(itemCounter2);
                } else {
                    // 这是不会再增加了 作为一个上线
                    maxAmount2 = Math.min(maxAmount2, itemCounter2.getStackNum());
                    if (maxAmount2 <= 0) return null;
                }
            }
        }
        for (int i = 0; i < cnt2; ++i) {
            recipeCounter2[i].setStackNum(maxAmount2);
        }
        int cnt = inputInfo.length;
        for (int i = 0; i < cnt; ++i) {
            inputInfo[i].setStackNum(maxAmount2);
        }
        return recipeCounter2;
    }

    /**
     * public static Pair<ItemGreedyConsumer[],ItemGreedyConsumer[]> countMultiRecipe(List<ItemPusher> inputCounters, ItemSlotPusher[] outputCounters, MachineRecipe recipe, int limit){
     * int len=inputCounters.size();
     * ItemStack[] recipeInput = recipe.getInput();
     * int cnt = recipeInput.length;
     * ItemGreedyConsumer[] recipeCounter=new ItemGreedyConsumer[cnt];
     * int maxAmount=limit;
     * for(int i=0;i<cnt;++i) {
     * recipeCounter[i]=getGreedyConsumer(recipeInput[i]);
     * for(int j=0;j<len;++j) {
     * ItemPusher  itemCounter2=inputCounters.get(j);
     * if(i==0){
     * itemCounter2.syncData();
     * }
     * if(itemCounter2.isDirty()){
     * //如果该counter已经被人绑定了 就跳过
     * continue;
     * }
     * if(CraftUtils.matchItemCounter(itemCounter2,recipeCounter[i],false)){
     * //如果匹配 将其加入...list,并算入matchCnt
     * recipeCounter[i].addRelate(itemCounter2);
     * recipeCounter[i].addMatchAmount(itemCounter2.getAmount());
     *
     * if(recipeCounter[i].getStackNum()>=limit)break;
     * }
     * }
     * maxAmount=Math.min(maxAmount,recipeCounter[i].getStackNum());
     * }
     * if (maxAmount==0)return null;
     * int len2=outputCounters.length;
     * ItemStack[] recipeOutput = recipe.getOutput();
     * int cnt2=recipeOutput.length;
     * ItemGreedyConsumer[] recipeCounter2= countMultiOutput(recipeCounter,outputCounters,recipe,maxAmount);
     * return recipeCounter2!=null?new Pair<>(recipeCounter,recipeCounter2):null;
     * }
     **/

    /**
     * force push recipe outputs to outputslot
     * @param
     * @param
     */
    public static boolean forcePush(ItemConsumer[] slotCounters, BlockMenu inv, int[] slots) {
        return forcePush(slotCounters, inv, slots, getpusher);
    }

    public static boolean forcePush(
            ItemConsumer[] slotCounters, BlockMenu inv, int[] slots, ItemPusherProvider pusher) {
        // ItemPusher[] slotCounters2=new ItemPusher[slots.length];
        LazyArray<ItemPusher> slotCounters2 =
                new LazyArray<>(ItemPusher[]::new, slots.length, pusher.getMenuInstance(Flags.OUTPUT, inv, slots));
        ItemConsumer outputItem;
        ItemPusher itemCounter;
        boolean hasChanged = false;
        int len = slotCounters.length;
        ItemStack previewItem;
        for (int i = 0; i < len; ++i) {
            outputItem = slotCounters[i];
            // consume mode
            outputItem.syncData();
            int maxAmount = outputItem.getMaxStackCnt();
            for (int j = 0; j < slots.length; ++j) {
                previewItem = inv.getItemInSlot(slots[j]);
                if (previewItem == null) {
                    // 是空槽 绝对不可能是存储 直接上 不要创建缓存
                    int amount = Math.min(outputItem.getAmount(), maxAmount);
                    inv.replaceExistingItem(slots[j], outputItem.getItem(), false);
                    previewItem = inv.getItemInSlot(slots[j]);
                    if (previewItem != null) {
                        // 防止某些脑瘫push一些null
                        previewItem.setAmount(amount);
                    }
                    outputItem.addAmount(-amount);
                    hasChanged = true;
                    // 满槽 绝对不可能是存储，直接跳
                    // 至于为什么不用maxAmount 因为outputItem可以是不可堆叠物品，但是存储暂时还没有不可堆叠物品
                    // 如果以后有 在此处加上>1判断
                } else if (previewItem.getAmount() >= previewItem.getMaxStackSize()) {
                    continue;
                } else {
                    itemCounter = slotCounters2.get(j);
                    if (!itemCounter.isDirty()) {
                        // 可能是存储里的 或者是被覆写的maxCNT
                        // FIXME 检测存储中是否可能存在空物品
                        if (itemCounter.getItem() == null) {
                            // 我们决定将这个分类留在这,为了安全性 毕竟谁也不知到之后会开发啥，不过显然 这玩意大概率是不会被调用的.
                            itemCounter.setFrom(outputItem);
                            itemCounter.grab(outputItem);
                            itemCounter.updateMenu(inv);
                            itemCounter.setDirty(true);
                            hasChanged = true;
                        } else if (itemCounter.getAmount() >= itemCounter.getMaxStackCnt()) {
                            continue;
                        } else if (matchItemCounter(outputItem, itemCounter, false)) {
                            itemCounter.grab(outputItem);
                            itemCounter.updateMenu(inv);
                            itemCounter.setDirty(true);
                            hasChanged = true;
                        }
                    }
                }
                if (outputItem.getAmount() <= 0) break;
            }
        }
        return hasChanged;
    }

    /**
     * remake version of pushItems
     * @return
     */
    public static boolean pushItems(ItemStack[] items, BlockMenu inv, int[] slots) {
        return pushItems(items, inv, slots, getpusher);
    }

    public static boolean pushItems(ItemStack[] items, BlockMenu inv, int[] slots, ItemPusherProvider pusher) {
        ItemConsumer[] consumers = new ItemConsumer[items.length];
        for (int i = 0; i < items.length; ++i) {
            consumers[i] = getConsumer(items[i]);
        }
        return forcePush(consumers, inv, slots, pusher);
    }

    public static boolean multiPushItems(ItemStack[] items, BlockMenu inv, int[] slots, int multiple) {
        return multiPushItems(items, inv, slots, multiple, getpusher);
    }

    public static boolean multiPushItems(
            ItemStack[] items, BlockMenu inv, int[] slots, int multiple, ItemPusherProvider pusher) {
        if (multiple == 1) {
            return pushItems(items, inv, slots, pusher);
        }
        ItemGreedyConsumer[] slotCounters = new ItemGreedyConsumer[items.length];
        for (int i = 0; i < items.length; ++i) {
            slotCounters[i] = getGreedyConsumer(items[i]);
            slotCounters[i].setMatchAmount(slotCounters[i].getAmount() * multiple);
        }
        return multiForcePush(slotCounters, inv, slots, pusher);
    }

    public static boolean multiForcePush(ItemGreedyConsumer[] slotCounters, BlockMenu inv, int[] slots) {
        return multiForcePush(slotCounters, inv, slots, getpusher);
    }

    public static boolean multiForcePush(
            ItemGreedyConsumer[] slotCounters, BlockMenu inv, int[] slots, ItemPusherProvider pusher) {
        LazyArray<ItemPusher> slotCounters2 =
                new LazyArray<>(ItemPusher[]::new, slots.length, pusher.getMenuInstance(Flags.OUTPUT, inv, slots));
        int len = slotCounters.length;
        ItemPusher itp = null;
        ItemGreedyConsumer outputItem;
        boolean hasChanged = false;
        ItemStack previewItem;
        for (int i = 0; i < len; ++i) {
            outputItem = slotCounters[i];
            // consume mode
            for (int j = 0; j < slots.length; ++j) {
                previewItem = inv.getItemInSlot(slots[j]);
                int maxAmount = outputItem.getMaxStackCnt();
                if (previewItem == null) {
                    // 多倍匹配用的是MatchAmount
                    int amount = Math.min(outputItem.getMatchAmount(), maxAmount);
                    inv.replaceExistingItem(slots[j], outputItem.getItem(), false);
                    previewItem = inv.getItemInSlot(slots[j]);
                    if (previewItem != null) {
                        // 防止某些脑瘫push一些null
                        previewItem.setAmount(amount);
                    } // 多倍匹配用的是MatchAmount
                    outputItem.addMatchAmount(-amount);
                    hasChanged = true;
                    // 同上 不解释
                } else if (previewItem.getAmount() >= previewItem.getMaxStackSize()) {
                    continue;
                } else {
                    itp = slotCounters2.get(j);
                    if (!itp.isDirty()) {
                        // FIXME 检查cachemap中的空存储是不是可能被读取进入?
                        if (itp.getItem() == null) {
                            itp.setFrom(outputItem);
                            // needs this push ,because the source of outputItem
                            outputItem.push(itp);
                            itp.updateMenu(inv);
                            itp.setDirty(true);
                            hasChanged = true;

                        } else if (itp.getAmount() >= itp.getMaxStackCnt()) {
                            continue;
                        } else if (matchItemCounter(outputItem, itp, false)) {
                            outputItem.push(itp);
                            itp.updateMenu(inv);
                            itp.setDirty(true);
                            hasChanged = true;
                        }
                    }
                }
                if (outputItem.getMatchAmount() <= 0) break;
            }
        }
        return hasChanged;
    }
    /**
     * simply update consumes
     * @param itemCounters
     */
    public static void updateInputMenu(ItemConsumer[] itemCounters, BlockMenu inv) {
        for (int i = 0; i < itemCounters.length; ++i) {
            itemCounters[i].updateItems(inv, Flags.GRAB);
        }
    }

    public static void updateOutputMenu(ItemConsumer[] itemCounters, BlockMenu inv) {
        for (int i = 0; i < itemCounters.length; ++i) {
            itemCounters[i].updateItems(inv, Flags.PUSH);
        }
    }

    public static void multiUpdateInputMenu(ItemGreedyConsumer[] recipeGreedyCounters, BlockMenu inv) {
        for (int i = 0; i < recipeGreedyCounters.length; ++i) {
            recipeGreedyCounters[i].updateItemsPlus(inv, Flags.GRAB);
        }
    }

    public static void multiUpdateOutputMenu(ItemGreedyConsumer[] recipeGreedyCounters, BlockMenu inv) {
        for (int i = 0; i < recipeGreedyCounters.length; ++i) {
            recipeGreedyCounters[i].updateItemsPlus(inv, Flags.PUSH);
        }
    }

    /**
     * make pushItem
     * make sure itemCounters.size>=out
     * @param itemCounters
     * @param out
     * @param inv
     */
    public static void updateOutputMenu(ItemPusher[] itemCounters, int[] out, BlockMenu inv) {
        int len = out.length;
        for (int i = 0; i < len; ++i) {
            ItemPusher a = itemCounters[i];
            if (a == null) {
                continue;
            }
            if (a.isDirty()) {
                a.updateMenu(inv);
            }
        }
    }
    /**
     * general findNextRecipe but modified by meeeeeee to adapt ItemCounter
     * @param inv
     * @param slots
     * @param recipes
     * @param useHistory
     * @return
     */
    public static Pair<MachineRecipe, ItemConsumer[]> findNextRecipe(
            BlockMenu inv, int[] slots, int[] outs, List<MachineRecipe> recipes, boolean useHistory) {
        return findNextRecipe(inv, slots, outs, recipes, useHistory, Flags.SEQUENTIAL);
    }

    public static Pair<MachineRecipe, ItemConsumer[]> findNextRecipe(
            BlockMenu inv, int[] slots, int[] outs, List<MachineRecipe> recipes, boolean useHistory, Flags order) {
        return findNextRecipe(inv, slots, outs, recipes, useHistory, Flags.SEQUENTIAL, getpusher);
    }
    /**
     * general findNextRecipe but modified by meeeeeee to adapt ItemCounter
     * @param inv
     * @param slots
     * @param outs
     * @param recipes
     * @param useHistory
     * @param order
     * @return
     */
    public static Pair<MachineRecipe, ItemConsumer[]> findNextRecipe(
            BlockMenu inv,
            int[] slots,
            int[] outs,
            List<MachineRecipe> recipes,
            boolean useHistory,
            Flags order,
            ItemPusherProvider pusher) {
        int delta;
        switch (order) {
            case REVERSE:
                delta = -1;
                break;
            case SEQUENTIAL:
            default:
                delta = 1;
                break;
        }
        int len = slots.length;
        final LazyArray<ItemPusher> slotCounter =
                new LazyArray<>(ItemPusher[]::new, len, pusher.getMenuInstance(Flags.INPUT, inv, slots));
        int recipeAmount = recipes.size();
        if (recipeAmount <= 0) {
            return null;
        }
        int __index = 0;
        // if usehistory ,will suggest a better place to start
        if (useHistory) {
            __index = BlockDataCache.getManager().getLastRecipe(inv.getLocation());
            __index = (__index < 0) ? 0 : __index;
            __index = (__index >= recipeAmount) ? (recipeAmount - 1) : __index;
        }
        int __iter = __index;
        MachineRecipe checkRecipe = recipes.get(__iter);
        ItemConsumer[] inputInfo = matchRecipe(slotCounter, checkRecipe);
        if (inputInfo != null) {
            if (useHistory) {
                BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
            }
            ItemConsumer[] outputInfo = countOneOutput(inv, outs, checkRecipe, getpusher);
            if (outputInfo != null) {
                updateInputMenu(inputInfo, inv);
                return new Pair<>(checkRecipe, outputInfo);
            } else return null; // for better performance in processors
        }
        __iter += delta;
        for (; __iter < recipeAmount && __iter >= 0; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            inputInfo = matchRecipe(slotCounter, checkRecipe);
            if (inputInfo != null) {
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                ItemConsumer[] outputInfo = countOneOutput(inv, outs, checkRecipe, getpusher);
                if (outputInfo != null) {
                    updateInputMenu(inputInfo, inv);
                    return new Pair<>(checkRecipe, outputInfo);
                } else return null; // for better performance in processors
            }
        }
        if (__iter < 0) {
            __iter = recipeAmount - 1;
        } else {
            __iter = 0;
        }
        for (; __iter != __index; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            inputInfo = matchRecipe(slotCounter, checkRecipe);
            if (inputInfo != null) {
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                ItemConsumer[] outputInfo = countOneOutput(inv, outs, checkRecipe, getpusher);
                if (outputInfo != null) {
                    updateInputMenu(inputInfo, inv);
                    return new Pair<>(checkRecipe, outputInfo);
                } else return null; // for better performance in processors
            }
        }
        return null;
    }

    /**
     * general only find next recipe for input slots ,No check for output slots, no consume for inputslots ,only fetch NEXT matching recipe.
     * used in manuals and MaterialGenerators
     * @param inv
     * @param slots
     * @param recipes
     * @param useHistory
     * @param order
     * @return
     *
     */
    public static MachineRecipe matchNextRecipe(
            BlockMenu inv, int[] slots, List<MachineRecipe> recipes, boolean useHistory, Flags order) {
        return matchNextRecipe(inv, slots, recipes, useHistory, order, getpusher);
    }

    public static MachineRecipe matchNextRecipe(
            BlockMenu inv,
            int[] slots,
            List<MachineRecipe> recipes,
            boolean useHistory,
            Flags order,
            ItemPusherProvider pusher) {
        int delta;
        switch (order) {
            case REVERSE:
                delta = -1;
                break;
            case SEQUENTIAL:
            default:
                delta = 1;
                break;
        }
        int len = slots.length;
        // final ArrayList<ItemPusher> slotCounter=new ArrayList<>(len);
        final LazyArray<ItemPusher> slotCounter =
                new LazyArray<>(ItemPusher[]::new, len, pusher.getMenuInstance(Flags.INPUT, inv, slots));
        int recipeAmount = recipes.size();
        if (recipeAmount <= 0) {
            return null;
        }
        int __index = 0;
        // if usehistory ,will suggest a better place to start
        if (useHistory) {
            __index = BlockDataCache.getManager().getLastRecipe(inv.getLocation());
            __index = (__index < 0) ? 0 : __index;
            __index = (__index >= recipeAmount) ? (recipeAmount - 1) : __index;
        }
        int __iter = __index;
        MachineRecipe checkRecipe = recipes.get(__iter);
        if (matchRecipe(slotCounter, checkRecipe) != null) {
            if (useHistory) {
                BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
            }
            return checkRecipe;
        }
        __iter += delta;
        for (; __iter < recipeAmount && __iter >= 0; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            if (matchRecipe(slotCounter, checkRecipe) != null) {

                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return checkRecipe;
            }
        }
        if (__iter < 0) {
            __iter = recipeAmount - 1;
        } else {
            __iter = 0;
        }
        for (; __iter != __index; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            if (matchRecipe(slotCounter, checkRecipe) != null) {
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return checkRecipe;
            }
        }
        return null;
    }

    public static List<Integer> matchAllRecipe(
            BlockMenu inv, int[] slots, List<MachineRecipe> recipes, ItemPusherProvider pusher, int matchAmount) {
        int delta = 1;
        int len = slots.length;
        List<Integer> result = new ArrayList<>(matchAmount + 1);
        // final ArrayList<ItemPusher> slotCounter=new ArrayList<>(len);
        final LazyArray<ItemPusher> slotCounter =
                new LazyArray<>(ItemPusher[]::new, len, pusher.getMenuInstance(Flags.INPUT, inv, slots));
        int recipeAmount = recipes.size();
        if (recipeAmount <= 0) {
            return result;
        }
        // if usehistory ,will suggest a better place to start
        MachineRecipe checkRecipe;
        for (int __iter = 0; __iter < recipeAmount; __iter++) {
            checkRecipe = recipes.get(__iter);
            if (matchRecipe(slotCounter, checkRecipe) != null) {
                result.add(__iter);
                if (result.size() >= matchAmount) {
                    return result;
                }
            }
        }
        return result;
    }

    //    public static Pair<MachineRecipe,ItemGreedyConsumer[]> matchNextMultiRecipe(BlockMenu inv ,int[]
    // slots,List<MachineRecipe> recipes,boolean useHistory,int limit,Settings order){
    //        return matchNextMultiRecipe(inv,slots,recipes,useHistory,limit,order,getpusher);
    //    }
    /**
     * a better version of matchNextRecipe for multiCraft,will remember the related slots of inputItems
     * will not calculate maxCraftTime !,you have to calculate by yourself!!!!
     * @param inv
     * @param slots
     * @param recipes
     * @param useHistory
     * @param order
     * @return
     */
    public static Pair<MachineRecipe, ItemGreedyConsumer[]> matchNextMultiRecipe(
            BlockMenu inv,
            int[] slots,
            List<MachineRecipe> recipes,
            boolean useHistory,
            int limit,
            Flags order,
            ItemPusherProvider pusher) {

        int delta;
        switch (order) {
            case REVERSE:
                delta = -1;
                break;
            case SEQUENTIAL:
            default:
                delta = 1;
                break;
        }
        int len = slots.length;
        ArrayList<ItemStack> slotNotNull = new ArrayList<>(len);
        ItemStack it;
        for (int i = 0; i < len; ++i) {
            it = inv.getItemInSlot(slots[i]);
            if (it != null) {
                slotNotNull.add(it);
            }
        }
        LazyArray<ItemPusher> slotCounter = new LazyArray<>(
                ItemPusher[]::new, slotNotNull.size(), pusher.getMenuInstance(Flags.INPUT, inv, slotNotNull));
        int recipeAmount = recipes.size();
        if (recipeAmount <= 0) {
            return null;
        }
        int __index = 0;
        // if usehistory ,will suggest a better place to start
        if (useHistory) {
            __index = BlockDataCache.getManager().getLastRecipe(inv.getLocation());
            __index = (__index < 0) ? 0 : __index;
            __index = (__index >= recipeAmount) ? (recipeAmount - 1) : __index;
        }
        int __iter = __index;
        MachineRecipe checkRecipe = recipes.get(__iter);
        ItemGreedyConsumer[] result = matchMultiRecipe(slotCounter, checkRecipe, limit);
        if (result != null) {
            if (useHistory) {
                BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
            }
            return new Pair<>(checkRecipe, result);
        }
        __iter += delta;
        for (; __iter < recipeAmount && __iter >= 0; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            result = matchMultiRecipe(slotCounter, checkRecipe, limit);
            if (result != null) {
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return new Pair<>(checkRecipe, result);
            }
        }
        if (__iter < 0) {
            __iter = recipeAmount - 1;
        } else {
            __iter = 0;
        }
        for (; __iter != __index; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            result = matchMultiRecipe(slotCounter, checkRecipe, limit);
            if (result != null) {
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return new Pair<>(checkRecipe, result);
            }
        }
        return null;
    }

    public static int calMaxCraftTime(ItemGreedyConsumer[] recipes, int limit) {
        int len = recipes.length;
        if (len != 0) {
            for (int i = 0; i < len; ++i) {
                limit = Math.min(limit, recipes[i].getStackNum());
            }
            return limit;
        } else return limit;
    }

    public static int calMaxCraftAfterAccelerate(int maxCraftTime, int tick) {
        return tick == 0 ? maxCraftTime : ((maxCraftTime <= tick) ? 1 : (maxCraftTime / (tick + 1)));
    }
    /**
     *it will continue search and at the same time CACULATE the max craftTime for EVERY recipe .
     * cost TOO MUCH,deprecated! use matchNextMultiRecipe+calmaxamount+updateInputMenu instead
     * @param inv
     * @param inputs
     * @param outputs
     * @param recipes
     * @param limit
     * @param useHistory
     * @return
     */
    /**
     * public static Pair<MachineRecipe,ItemGreedyConsumer[]> findNextMultiRecipe(BlockMenu inv,int[] inputs,int[] outputs,
     * List<MachineRecipe> recipes,int limit,boolean useHistory){
     * return findNextMultiRecipe(inv,inputs,outputs,recipes,limit,useHistory,Settings.SEQUENTIAL);
     * }
     * public static Pair<MachineRecipe,ItemGreedyConsumer[]> findNextMultiRecipe(BlockMenu inv,int[] inputs,int[] outputs,
     * List<MachineRecipe> recipes,int limit,boolean useHistory,Settings order){
     * return findNextMultiRecipe(inv,inputs,outputs,recipes,limit,useHistory,Settings.SEQUENTIAL,getpusher);
     * }
     * public static Pair<MachineRecipe,ItemGreedyConsumer[]> findNextMultiRecipe(BlockMenu inv,int[] inputs,int[] outputs,
     * List<MachineRecipe> recipes,int limit,boolean useHistory,Settings order,ItemPusherProvider pusher){
     * int delta;
     * switch(order){
     * case REVERSE:delta=-1;break;
     * case SEQUENTIAL:
     * default: delta=1;break;
     * }
     * int len = inputs.length;
     * final ArrayList<ItemPusher> slotCounter=new ArrayList<>(len);
     * for(int i = 0; i < len; ++i) {
     * ItemPusher input=pusher.get(Settings.INPUT,inv,inputs[i]);
     * if(input!=null){
     * slotCounter.add(input);
     * }
     * }
     * //end before anything
     * int outlen=outputs.length;
     * int recipeAmount=recipes.size();
     * int __index=0;
     * //if usehistory ,will suggest a better place to start
     * if(useHistory) {
     * __index= AbstractMachines.getLastRecipe(inv.getLocation());
     * __index=(__index<0)?0:__index;
     * __index=(__index>=recipeAmount)?(recipeAmount-1):__index;
     * }
     * int __iter=__index;
     * MachineRecipe checkRecipe=recipes.get(__iter);
     * final ItemSlotPusher[] outPushers=new ItemSlotPusher[outlen];
     * for (int i=0;i<outlen;++i){
     * outPushers[i]=ItemSlotPusher.get(inv.getItemInSlot(outputs[i]),outputs[i]);
     * }
     * Pair<ItemGreedyConsumer[],ItemGreedyConsumer[]> tmp=countMultiRecipe(slotCounter,outPushers,checkRecipe,limit);
     * if(tmp!=null) {
     * multiUpdateInputMenu(tmp.getA(),inv);
     * if(useHistory) {
     * AbstractMachines.setLastRecipe(inv.getLocation(),__iter);
     * }
     * return new Pair<>(checkRecipe,tmp.getB());
     * }
     * __iter+=delta;
     * for(;__iter<recipeAmount&&__iter>=0;__iter+=delta){
     * checkRecipe=recipes.get(__iter);
     * tmp=countMultiRecipe(slotCounter,outPushers,checkRecipe,limit);
     * if(tmp!=null) {
     * multiUpdateInputMenu(tmp.getA(),inv);
     * if(useHistory) {
     * AbstractMachines.setLastRecipe(inv.getLocation(),__iter);
     * }
     * return new Pair<>(checkRecipe,tmp.getB());
     * }
     * }
     * if(__iter<0){
     * __iter=recipeAmount-1;
     * }else{
     * __iter=0;
     * }
     * for(;__iter!=__index;__iter+=delta) {
     * checkRecipe=recipes.get(__iter);
     * tmp=countMultiRecipe(slotCounter,outPushers,checkRecipe,limit);
     * if(tmp!=null) {
     * multiUpdateInputMenu(tmp.getA(),inv);
     * if(useHistory) {
     * AbstractMachines.setLastRecipe(inv.getLocation(),__iter);
     * }
     * return new Pair<>(checkRecipe,tmp.getB());
     * }
     * }
     * return null;
     * }
     **/
    public static int matchShapedRecipe(ItemPusher[] input, MachineRecipe recipe, int limit) {
        ItemStack[] recipeInput = recipe.getInput();
        int len = input.length;
        int len2 = recipeInput.length;
        if (len < len2) return 0;
        int max = limit;
        for (int i = 0; i < len2; ++i) {
            if (input[i] == null) {
                if (recipeInput[i] == null) {
                    continue;
                } else return 0;
            } else if (!matchItemStack(recipeInput[i], input[i], false)) return 0;
            else {
                max = Math.min(max, input[i].getAmount() / recipeInput[i].getAmount());
            }
        }
        return max;
    }
    //    public static Pair<MachineRecipe,ItemGreedyConsumer[]> findNextShapedRecipe(BlockMenu inv,int[] inputs,int[]
    // outputs,
    //                                                                                List<MachineRecipe> recipes,int
    // limit,boolean useHistory){
    //        return findNextShapedRecipe(inv,inputs,outputs,recipes,limit,useHistory,Settings.SEQUENTIAL);
    //    }

    public static Pair<MachineRecipe, ItemGreedyConsumer[]> findNextShapedRecipe(
            BlockMenu inv,
            int[] inputs,
            int[] outputs,
            List<MachineRecipe> recipes,
            int limit,
            boolean useHistory,
            Flags order,
            ItemPusherProvider pusher) {
        int delta;
        switch (order) {
            case REVERSE:
                delta = -1;
                break;
            case SEQUENTIAL:
            default:
                delta = 1;
                break;
        }
        int len = inputs.length;
        ItemPusher[] inputItem = new ItemPusher[len];
        IntFunction<ItemPusher> inputSlotInstance = pusher.getMenuInstance(Flags.INPUT, inv, inputs);
        for (int i = 0; i < len; ++i) {
            inputItem[i] = inputSlotInstance.apply(i);
        }
        // end before anything
        if (len == 0) return null;
        int outlen = outputs.length;
        int recipeAmount = recipes.size();
        int __index = 0;
        // if usehistory ,will suggest a better place to start
        if (useHistory) {
            __index = BlockDataCache.getManager().getLastRecipe(inv.getLocation());
            __index = (__index < 0) ? 0 : __index;
            __index = (__index >= recipeAmount) ? (recipeAmount - 1) : __index;
        }

        int __iter = __index;
        MachineRecipe checkRecipe = recipes.get(__iter);
        int craftAmount = matchShapedRecipe(inputItem, checkRecipe, limit);
        if (craftAmount > 0) {
            int finalAmount = craftAmount;
            ItemGreedyConsumer[] outputCounters = null;
            if (outputs.length != 0) {
                outputCounters =
                        countMultiOutput(new ItemGreedyConsumer[] {}, inv, outputs, checkRecipe, craftAmount, pusher);
                if (outputCounters != null) finalAmount = outputCounters[0].getStackNum();
                else return null;
            }
            ItemStack[] recipeInput = checkRecipe.getInput();
            int len2 = recipeInput.length;
            for (int i = 0; i < len2; ++i) {
                if (inputItem[i] != null) {
                    inputItem[i].setAmount(inputItem[i].getAmount() - finalAmount * recipeInput[i].getAmount());
                    inputItem[i].updateMenu(inv);
                }
            }
            if (useHistory) {
                BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
            }

            return new Pair<>(checkRecipe, outputCounters);
        }
        __iter += delta;
        for (; __iter < recipeAmount && __iter >= 0; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            craftAmount = matchShapedRecipe(inputItem, checkRecipe, limit);
            if (craftAmount > 0) {
                int finalAmount = craftAmount;
                ItemGreedyConsumer[] outputCounters = null;
                if (outputs.length != 0) {
                    outputCounters = countMultiOutput(
                            new ItemGreedyConsumer[] {}, inv, outputs, checkRecipe, craftAmount, pusher);
                    if (outputCounters != null) finalAmount = outputCounters[0].getStackNum();
                    else return null;
                }
                ItemStack[] recipeInput = checkRecipe.getInput();
                int len2 = recipeInput.length;
                for (int i = 0; i < len2; ++i) {
                    if (inputItem[i] != null) {
                        inputItem[i].setAmount(inputItem[i].getAmount() - finalAmount * recipeInput[i].getAmount());
                        inputItem[i].updateMenu(inv);
                    }
                }
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return new Pair<>(checkRecipe, outputCounters);
            }
        }
        if (__iter < 0) {
            __iter = recipeAmount - 1;
        } else {
            __iter = 0;
        }
        for (; __iter != __index; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            craftAmount = matchShapedRecipe(inputItem, checkRecipe, limit);
            if (craftAmount > 0) {
                int finalAmount = craftAmount;
                ItemGreedyConsumer[] outputCounters = null;
                if (outputs.length != 0) {
                    outputCounters = countMultiOutput(
                            new ItemGreedyConsumer[] {}, inv, outputs, checkRecipe, craftAmount, pusher);
                    if (outputCounters != null) finalAmount = outputCounters[0].getStackNum();
                    else return null;
                }
                ItemStack[] recipeInput = checkRecipe.getInput();
                int len2 = recipeInput.length;
                for (int i = 0; i < len2; ++i) {
                    if (inputItem[i] != null) {
                        inputItem[i].setAmount(inputItem[i].getAmount() - finalAmount * recipeInput[i].getAmount());
                        inputItem[i].updateMenu(inv);
                    }
                }
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return new Pair<>(checkRecipe, outputCounters);
            }
        }
        return null;
    }

    public static boolean matchSequenceRecipeTarget(ItemPusher[] inPushers, ItemConsumer target) {
        boolean hasChange = false;
        int len = inPushers.length;
        if (target.getAmount() <= 0) return false;
        for (int i = 0; i < len; ++i) {
            if (inPushers[i] == null || inPushers[i].getAmount() == 0) {
                continue;
            } else if (matchItemCounter(target, inPushers[i], false)) {
                hasChange = true;
                target.consume(inPushers[i]);
                if (target.getAmount() <= 0) {
                    break;
                }
            }
        }
        return hasChange;
    }
    /**
     * sequence CRAFT, match itemstack with the first
     */
    public static Pair<MachineRecipe, ItemConsumer> findNextSequenceRecipe(
            BlockMenu inv,
            int[] inputs,
            List<MachineRecipe> recipes,
            boolean useHistory,
            Flags order,
            ItemPusherProvider pusher,
            boolean clearInput) {
        int delta;
        switch (order) {
            case REVERSE:
                delta = -1;
                break;
            case SEQUENTIAL:
            default:
                delta = 1;
                break;
        }
        int len = inputs.length;
        ItemPusher[] inputCounters = new ItemPusher[len];
        IntFunction<ItemPusher> pusherFunc = pusher.getMenuInstance(Flags.INPUT, inv, inputs);
        for (int i = 0; i < len; ++i) {
            inputCounters[i] = pusherFunc.apply(i);
        }
        if (len == 0) return null;
        int recipeAmount = recipes.size();
        int __index = 0;
        // if usehistory ,will suggest a better place to start
        if (useHistory) {
            __index = BlockDataCache.getManager().getLastRecipe(inv.getLocation());
            __index = (__index < 0) ? 0 : __index;
            __index = (__index >= recipeAmount) ? (recipeAmount - 1) : __index;
        }
        int __iter = __index;
        MachineRecipe checkRecipe = recipes.get(__iter);
        ItemConsumer result = getConsumer(checkRecipe.getInput()[0]);
        if (matchSequenceRecipeTarget(inputCounters, result)) {
            if (useHistory) {
                BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
            }
            return new Pair<>(checkRecipe, result);
        }
        __iter += delta;
        for (; __iter < recipeAmount && __iter >= 0; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            result = getConsumer(checkRecipe.getInput()[0]);
            if (matchSequenceRecipeTarget(inputCounters, result)) {
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return new Pair<>(checkRecipe, result);
            }
        }
        if (__iter < 0) {
            __iter = recipeAmount - 1;
        } else {
            __iter = 0;
        }
        for (; __iter != __index; __iter += delta) {
            checkRecipe = recipes.get(__iter);
            result = getConsumer(checkRecipe.getInput()[0]);
            if (matchSequenceRecipeTarget(inputCounters, result)) {
                if (useHistory) {
                    BlockDataCache.getManager().setLastRecipe(inv.getLocation(), __iter);
                }
                return new Pair<>(checkRecipe, result);
            }
        }
        if (clearInput) {
            clearAmount(inv, inputCounters);
        }
        return null;
    }
}
