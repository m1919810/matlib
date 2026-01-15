package me.matl114.matlib.utils.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import me.matl114.matlib.algorithms.algorithm.ArrayUtils;
import me.matl114.matlib.algorithms.algorithm.FuncUtils;
import me.matl114.matlib.algorithms.dataStructures.frames.bits.BitList;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.MappingList;
import me.matl114.matlib.utils.crafting.agents.CraftingOperation;
import me.matl114.matlib.utils.crafting.agents.GeneratorAgent;
import me.matl114.matlib.utils.crafting.agents.StackAgent;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.stackCache.ItemCacheFactory;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import me.matl114.matlib.utils.stackCache.ItemWithSlot;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.InventoryHolder;

public class CraftingUtils {
    public static <W extends StackAgent> void resetMatchingInfo(W[] agents) {
        for (W agent : agents) {
            agent.resetMatchingInfo();
        }
    }

    public static List<GeneratorAgent> generateCraftingOutput(CraftingOperation operation) {
        IGenerator[] recipeOutput = operation.getRecipe().getOutputs();
        List<GeneratorAgent> list = new ArrayList<>();
        for (var generator : recipeOutput) {
            generator.generateOutput().forEach(i -> list.add(GeneratorAgent.get(i.getA(), i.getB())));
        }
        operation.setGeneratorAgent(list.toArray(GeneratorAgent[]::new));
        return list;
    }

    public static void prepareCraftingOutput(CraftingOperation operation, List<StackBuffer> outputSlots) {
        GeneratorAgent[] list = operation.getGeneratorAgent();
        long maxAmount2 = operation.getCraftLimit();
        if (maxAmount2 <= 0) {
            return;
        }
        // 优化 当一个输出的时候 直接输出 匹配最大的匹配数
        // 99%的情况都是这样的
        // 应该不会有很多2b作者给这么高效的机器设置两个输出

        int cnt = list.length;
        int len2 = outputSlots.size();
        BitList visited = new BitList(len2);
        if (cnt > 0) {
            PriorityQueue<GeneratorAgent> priorityRecipeOutput = new PriorityQueue<>(cnt + 1);
            for (var agent : list) {
                priorityRecipeOutput.add(agent);
            }
            if (cnt == 1) {
                GeneratorAgent agent = priorityRecipeOutput.poll(); //   getGreedyConsumer(recipeOutput[0]);
                if (agent != null) {
                    for (int i = 0; i < len2; ++i) {
                        StackBuffer itemCounter = outputSlots.get(i);
                        // 增加过滤器 过滤已经被绑定的槽位
                        // 我们在addRelated中增加绑定
                        if (!visited.get(i)) {
                            itemCounter.syncData();
                            visited.setTrue(i);
                        } else if (itemCounter.isDirty()) {
                            continue;
                        }
                        if (itemCounter.isNull()) {
                            if (agent.canStackOn(itemCounter)) {
                                if (itemCounter.setFrom(agent.getOutputSample())) {
                                    long maxTransferCount = Math.min(
                                            itemCounter.getMaxStackCnt(),
                                            agent.getOutputSample().getMaxStackCnt());
                                    agent.pushSlot(itemCounter, maxTransferCount);
                                } else {
                                    continue;
                                }
                                // todo: here should we use itemCounter.getMaxStackCnt?
                                // todo: no, here outSample means how can one stack at
                                // todo: use this

                            } else {
                                continue;
                            }

                            //                        recipeCounter2[0].addRelate(itemCounter);
                            //
                            // recipeCounter2[0].addMatchAmount(recipeCounter2[0].getMaxStackCnt());
                        } else if (itemCounter.getMaxStackCnt() <= itemCounter.getAmount()) {
                            continue;
                        } else if (agent.canStackOn(itemCounter)) {
                            agent.pushSlot(itemCounter, itemCounter.getMaxStackCnt() - itemCounter.getAmount());
                            //                        recipeCounter2[0].addRelate(itemCounter);
                            //                        recipeCounter2[0].addMatchAmount(itemCounter.getMaxStackCnt() -
                            // itemCounter.getAmount());
                        }
                        if (agent.getMatchStackAmount() >= maxAmount2) {
                            break;
                        }
                    }
                    maxAmount2 = Math.min(agent.getMatchStackAmount(), maxAmount2);
                    operation.setCraftLimit(maxAmount2);
                    if (maxAmount2 <= 0) {
                        return;
                    }
                }

            }
            // 如果真的有,你喜欢就好
            // 有可能是桶或者什么
            else {
                // 维护一下当前matchAmount最小值
                while (true) {
                    GeneratorAgent agent = priorityRecipeOutput.poll();
                    if (agent == null) {
                        break;
                    }
                    boolean hasNextPushSlot = false;
                    for (int j = 0; j < len2; ++j) {
                        StackBuffer itemCounter = outputSlots.get(j);
                        // 增加过滤器 过滤已经被绑定的槽位
                        // 我们在addRelated中增加绑定
                        if (!visited.get(j)) {
                            itemCounter.syncData();
                            visited.setTrue(j);
                        } else if (itemCounter.isDirty()) {
                            continue;
                        }
                        if (itemCounter.isNull()) {
                            if (agent.canStackOn(itemCounter)) {
                                if (itemCounter.setFrom(agent.getOutputSample())) {
                                    long maxTransferCount = Math.min(
                                            itemCounter.getMaxStackCnt(),
                                            agent.getOutputSample().getMaxStackCnt());
                                    agent.pushSlot(itemCounter, maxTransferCount);
                                    hasNextPushSlot = true;
                                    break;

                                } else {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                            //                        itemCounter.setFrom(itemCounter2);
                            //                        itemCounter2.addRelate(itemCounter);
                            //                        // can output maxCnt amount
                            //                        itemCounter2.addMatchAmount(itemCounter2.getMaxStackCnt());
                        } else if (itemCounter.isDirty() || itemCounter.getMaxStackCnt() <= itemCounter.getAmount()) {
                            continue;
                        } else if (agent.canStackOn(itemCounter)) {

                            // what the fuck????
                            // 为什么他妈的会覆盖啊 谁写的答辩玩意啊
                            // itemCounter.setFrom(itemCounter2);
                            //                            itemCounter2.addRelate(itemCounter);
                            //                            // must use itemCounter.getMaxStackCnt because ItemStorage
                            //                            itemCounter2.addMatchAmount(itemCounter.getMaxStackCnt() -
                            // itemCounter.getAmount());
                            agent.pushSlot(itemCounter, itemCounter.getMaxStackCnt() - itemCounter.getAmount());
                            hasNextPushSlot = true;
                            break;
                        }
                    }
                    // ?
                    // 这是什么鬼玩意
                    // 哦
                    // 要判断是否有nextSlot
                    if (hasNextPushSlot && agent.getMatchStackAmount() <= maxAmount2) {
                        priorityRecipeOutput.add(agent);
                    } else {
                        // 这是不会再增加了 作为一个上线
                        maxAmount2 = Math.min(maxAmount2, agent.getMatchStackAmount());
                        operation.setCraftLimit(maxAmount2);
                        if (maxAmount2 <= 0) return;
                    }
                }
            }
        }

        // the craftLimit are already set in the operation

    }

    public static <W extends ItemStackCache<W>> List<ItemWithSlot<W>> getInputSlotMapping(
            InventoryHolder holder, int[] slots, ItemCacheFactory<W> cacheFactory) {
        return new MappingList<>(
                FuncUtils.box(cacheFactory.getInputIndex(holder, slots)),
                MappingList::readOnly,
                ArrayUtils.getArangeList(slots.length));
    }

    public static <W extends ItemStackCache<W>> List<ItemWithSlot<W>> getOutputSlotMapping(
            InventoryHolder holder, int[] slots, ItemCacheFactory<W> cacheFactory) {
        return new MappingList<>(
                FuncUtils.box(cacheFactory.getOutputIndex(holder, slots)),
                MappingList::readOnly,
                ArrayUtils.getArangeList(slots.length));
    }
}
