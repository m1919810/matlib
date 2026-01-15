package me.matl114.matlib.implement.custom.crafting.recipe.randGenerator;

import com.google.common.base.Preconditions;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.algorithm.RandomUtils;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.algorithms.designs.rand.RandomSelector;
import me.matl114.matlib.implement.custom.crafting.recipe.IRandGenerator;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.crafting.recipe.IGenerator;
import me.matl114.matlib.utils.stackCache.StackBuffer;
import org.bukkit.inventory.ItemStack;

public class RandSelectGenerator<G extends IGenerator> implements IGenerator, IRandGenerator<G> {
    protected double[] weights;
    protected G[] generators;
    protected RandomSelector selector;

    public RandSelectGenerator(double[] weights, G[] generators) {
        this(weights, generators, RandomSelector.generate(weights));
    }

    public RandSelectGenerator(double[] weights, G[] generators, RandomSelector selector) {
        Preconditions.checkArgument(selector.getChoices() == weights.length);
        Preconditions.checkArgument(weights.length == generators.length);
        this.weights = RandomUtils.normalizeWeight(weights);
        this.generators = generators;
        this.selector = selector;
    }

    @Override
    public Stream<Pair<StackBuffer, IGenerator>> generateOutput() {
        int select = selector.sample();
        return generators[select].generateOutput();
    }

    @Override
    public boolean equalsGenerator(IGenerator consumer) {
        return consumer instanceof RandSelectGenerator selectGenerator
                && Arrays.equals(selectGenerator.weights, weights)
                && equalsGenerators(selectGenerator.generators);
    }

    @Override
    public IGenerator copy() {
        return new RandSelectGenerator(weights, generators, selector);
    }

    @Override
    public boolean similarGenerator(IGenerator consumer) {
        return consumer instanceof RandSelectGenerator selectGenerator
                && Arrays.equals(selectGenerator.weights, weights)
                && similarGenerators(selectGenerator.generators);
    }

    private boolean equalsGenerators(IGenerator[] generator) {
        if (generators.length != generator.length) {
            return false;
        }
        for (int i = 0; i < generator.length; i++) {
            if (!generators[i].equalsGenerator(generator[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean similarGenerators(IGenerator[] generator) {
        if (generators.length != generator.length) {
            return false;
        }
        for (int i = 0; i < generator.length; i++) {
            if (!generators[i].similarGenerator(generator[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IGenerator combine(IGenerator consumer) {
        RandSelectGenerator selectGenerator = (RandSelectGenerator) consumer;
        IGenerator[] generators = new IGenerator[this.generators.length];
        for (int i = 0; i < generators.length; i++) {
            generators[i] = this.generators[i].combine(selectGenerator.generators[i]);
        }
        return new RandSelectGenerator<>(weights, generators, selector);
    }

    @Override
    public Stream<ItemStack> getConditionalDisplay(double current) {
        Stream<ItemStack>[] streams = new Stream[generators.length];
        for (int i = 0; i < generators.length; i++) {
            IGenerator generator = generators[i];
            if (generator instanceof IRandGenerator) {
                streams[i] = ((IRandGenerator) generator).getConditionalDisplay(current * weights[i]);
            } else {
                final double percentage = current * weights[i];
                streams[i] = generator
                        .getDisplays()
                        .map(s -> TextUtils.appendLore(s, "&e随机输出", "&e概率: " + TextUtils.getPercentFormat(percentage)));
            }
        }
        return Streams.concat(streams);
    }
}
