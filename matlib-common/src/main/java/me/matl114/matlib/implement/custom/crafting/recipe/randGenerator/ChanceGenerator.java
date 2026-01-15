package me.matl114.matlib.implement.custom.crafting.recipe.randGenerator;

import java.util.Random;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.implement.custom.crafting.recipe.IRandGenerator;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;

public class ChanceGenerator<G extends IGenerator> implements IRandGenerator<G>, IGenerator {
    G generator;
    double percentage;
    Random rand = new Random();

    public ChanceGenerator(G generator, double percentage) {
        this.generator = generator;
        this.percentage = percentage;
    }

    @Override
    public Stream<ItemStack> getConditionalDisplay(double current) {
        if (generator instanceof IRandGenerator irand) {
            return irand.getConditionalDisplay(current * percentage);
        } else {
            final double p = current * percentage;
            return generator
                    .getDisplays()
                    .map(s -> TextUtils.appendLore(s, "&e随机输出", "&e概率: " + TextUtils.getPercentFormat(p)));
        }
    }

    @Override
    public Stream<Pair<StackBuffer, IGenerator>> generateOutput() {
        if (rand.nextDouble() < percentage) {
            return generator.generateOutput();
        } else {
            return Stream.empty();
        }
    }

    @Override
    public boolean equalsGenerator(IGenerator consumer) {
        return consumer == this
                || (consumer instanceof ChanceGenerator chance
                        && chance.percentage == percentage
                        && chance.generator.equalsGenerator(generator));
    }

    @Override
    public IGenerator copy() {
        return new ChanceGenerator(generator, percentage);
    }

    @Override
    public boolean similarGenerator(IGenerator consumer) {
        return consumer == this
                || (consumer instanceof ChanceGenerator<?> chance
                        && chance.percentage == percentage
                        && chance.generator.similarGenerator(generator));
    }

    @Override
    public IGenerator combine(IGenerator consumer) {
        ChanceGenerator chance = (ChanceGenerator) consumer;
        return new ChanceGenerator(generator.combine(chance.generator), chance.percentage);
    }
}
