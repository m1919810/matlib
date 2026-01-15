package me.matl114.matlib.utils.crafting;

import com.destroystokyo.paper.MaterialTags;
import java.util.*;
import me.matl114.matlib.implement.custom.crafting.recipe.SimpleRecipe;
import me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer.ItemConsumer;
import me.matl114.matlib.implement.custom.crafting.recipe.itemConsumer.SimpleItemConsumer;
import me.matl114.matlib.implement.custom.crafting.recipe.itemGenerator.ItemGenerator;
import me.matl114.matlib.implement.custom.crafting.recipe.itemGenerator.SimpleItemGenerator;
import me.matl114.matlib.utils.crafting.recipe.IConsumer;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.stackCache.ItemStackCache;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class RecipeUtils {

    public static <G extends IGenerator> List<G> compressOutput(G[] out) {
        List<G> results = new ArrayList<G>();
        loop:
        for (G gen : out) {
            if (gen == IGenerator.EMPTY || gen == null) {
                continue;
            }
            int size = results.size();
            for (int i = 0; i < size; i++) {
                G tmp = results.get(i);
                if (tmp.similarGenerator(gen)) {
                    results.set(i, (G) tmp.combine(gen));
                    continue loop;
                }
            }
            results.add(gen);
        }
        return results;
    }

    public static <G extends IConsumer> List<G> compressInput(G[] in) {
        List<G> results = new ArrayList<G>();
        loop:
        for (G gen : in) {
            if (gen == IConsumer.EMPTY || gen == null) {
                continue;
            }
            int size = results.size();
            for (int i = 0; i < size; i++) {
                G tmp = results.get(i);
                if (tmp.similarConsumer(gen)) {
                    results.set(i, (G) tmp.combine(gen));
                    continue loop;
                }
            }
            results.add(gen);
        }
        return results;
    }

    public static <T extends ItemStackCache<T>> SimpleRecipe<T> compressRecipe(SimpleRecipe<T> recipe) {
        return new SimpleRecipe<>(
                compressInput(recipe.getInputs()).toArray(ItemConsumer[]::new),
                compressOutput(recipe.getOutputs()).toArray(ItemGenerator[]::new),
                recipe.getTotalProgress());
    }

    public static <T extends ItemStackCache<T>> ItemConsumer<T> getConsumer(T ca) {
        return ca.isAir() ? null : new SimpleItemConsumer<>(ca, ca.getAmount());
    }

    public static <T extends ItemStackCache<T>> ItemGenerator<T> getGenerator(T ca) {
        return ca.isAir() ? null : new SimpleItemGenerator<>(ca);
    }

    public static <T extends ItemStackCache<T>> SimpleRecipe<T> createRecipe(T[] input, T[] output, int progress) {
        ItemConsumer<T>[] consumers = new ItemConsumer[input.length];
        for (int i = 0; i < input.length; i++) {
            consumers[i] = getConsumer(input[i]);
        }
        ItemGenerator<T>[] generators = new ItemGenerator[output.length];
        for (int i = 0; i < input.length; i++) {
            generators[i] = getGenerator(output[i]);
        }
        return new SimpleRecipe<>(consumers, generators, progress);
    }

    public static <T extends ItemStackCache<T>> SimpleRecipe<T> createRecipe(
            List<T> input, List<T> output, int progress) {
        ItemConsumer<T>[] consumers = new ItemConsumer[input.size()];
        for (int i = 0; i < input.size(); i++) {
            consumers[i] = getConsumer(input.get(i));
        }
        ItemGenerator<T>[] generators = new ItemGenerator[output.size()];
        for (int i = 0; i < input.size(); i++) {
            generators[i] = getGenerator(output.get(i));
        }
        return new SimpleRecipe<>(consumers, generators, progress);
    }

    public static final ItemStack BOTTLE = new ItemStack(Material.GLASS_BOTTLE);
    public static final ItemStack BUCKET = new ItemStack(Material.BUCKET);
    public static final Set<Material> GLASS_BOTTLE_ITEMS = Set.of(
            Material.HONEY_BOTTLE,
            Material.POTION,
            Material.SPLASH_POTION,
            Material.LINGERING_POTION,
            Material.DRAGON_BREATH);

    public static List<ItemStack> createReturnedItem(ItemStack[] input) {
        int bucket = 0;
        int bottle = 0;
        ItemStack p;
        for (int i = 0; i < input.length; i++) {
            p = input[i];
            if (p != null && !p.hasItemMeta()) {
                Material mat = p.getType();
                if (MaterialTags.BUCKETS.isTagged(mat)) {
                    bucket += p.getAmount();
                } else if (GLASS_BOTTLE_ITEMS.contains(mat)) {
                    bottle += p.getAmount();
                }
            }
        }
        List<ItemStack> items = new ArrayList<>();
        if (bucket > 0) {
            items.add(new ItemStack(Material.BUCKET, bucket));
        }
        if (bottle > 0) {
            items.add(new ItemStack(Material.GLASS_BOTTLE, bottle));
        }
        return items;
    }
}
