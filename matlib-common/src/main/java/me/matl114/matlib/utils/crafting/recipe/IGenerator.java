package me.matl114.matlib.utils.crafting.recipe;

import java.util.stream.Stream;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;

public interface IGenerator extends IRecipeInfo {

    /**
     * describe stacking slot rules,
     * can set rules like : do not stack on other items or only stack on empty items
     * mostly null -> true
     * you can not invoke a IRandGenerator's canStack, you should ask the source generator
     * @param generated
     * @param slotItem
     * @return
     */
    boolean canStack(StackBuffer generated, StackBuffer slotItem);

    @Note("return the generated buffer and the source IGenerator for canStack()")
    Stream<Pair<StackBuffer, IGenerator>> generateOutput();

    public boolean equalsGenerator(IGenerator consumer);

    public IGenerator copy();

    public boolean similarGenerator(IGenerator consumer);

    @Note("must be similar to this")
    public IGenerator combine(IGenerator consumer);

    public class Empty implements IGenerator {

        @Override
        public boolean canStack(StackBuffer generated, StackBuffer slotItem) {
            return slotItem.isNull();
        }

        @Override
        public Stream<Pair<StackBuffer, IGenerator>> generateOutput() {
            return Stream.empty();
        }

        @Override
        public boolean equalsGenerator(IGenerator consumer) {
            return consumer == this;
        }

        @Override
        public IGenerator copy() {
            return this;
        }

        @Override
        public boolean similarGenerator(IGenerator consumer) {
            return consumer == this;
        }

        @Override
        public IGenerator combine(IGenerator consumer) {
            return this;
        }

        @Override
        public Stream<ItemStack> getDisplays() {
            return Stream.of((ItemStack) null);
        }
    }

    IGenerator EMPTY = new Empty();
}
