package me.matl114.matlib.implement.serialization;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public interface ItemStackSource {

    ItemStack getSample();

    default ItemStack getWithAmount(int amount) {
        ItemStack stack = getSample().clone();
        stack.setAmount(amount);
        return stack;
    }

    public static ItemStackSource of(ItemStack stack) {
        return new Custom(stack);
    }

    public static ItemStackSource of(Material material) {
        return new Vanilla(material);
    }

    public record Vanilla(Material material) implements ItemStackSource {
        public ItemStack getSample() {
            return new ItemStack(material);
        }

        public ItemStack getWithAmount(int amount) {
            return new ItemStack(material, amount);
        }
    }

    public record Custom(ItemStack itemStack) implements ItemStackSource {

        @Override
        public ItemStack getSample() {
            return itemStack;
        }
    }
}
