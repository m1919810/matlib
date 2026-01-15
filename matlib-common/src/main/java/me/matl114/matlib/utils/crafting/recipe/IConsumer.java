package me.matl114.matlib.utils.crafting.recipe;

import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import me.matl114.matlib.common.lang.annotations.ConstVal;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;

public interface IConsumer extends IRecipeInfo {

    boolean accept(@Nullable StackBuffer cache);

    @ConstVal
    long getRequiringAmount();

    long getTotalConsumeAmount(List<StackBuffer> acceptedItems, long craftTime);

    public static IConsumer EMPTY = new Empty();

    public static class Empty implements IConsumer {

        @Override
        public boolean accept(@Nullable StackBuffer cache) {
            return cache == null || cache.isNull();
        }

        @Override
        public long getRequiringAmount() {
            return 0;
        }

        @Override
        public boolean equalsConsumer(IConsumer consumer) {
            return consumer == this;
        }

        @Override
        public IConsumer copy() {
            return this;
        }

        @Override
        public boolean similarConsumer(IConsumer consumer) {
            return consumer == this;
        }

        @Override
        public IConsumer combine(IConsumer consumer) {
            return this;
        }

        @Override
        public long getTotalConsumeAmount(List acceptedItems, long craftTime) {
            return 0;
        }

        @Override
        public Stream<ItemStack> getDisplays() {
            return Stream.of((ItemStack) null);
        }
    }

    public boolean equalsConsumer(IConsumer consumer);

    public IConsumer copy();

    public boolean similarConsumer(IConsumer consumer);

    @Note("must be similar to this")
    public IConsumer combine(IConsumer consumer);
}
