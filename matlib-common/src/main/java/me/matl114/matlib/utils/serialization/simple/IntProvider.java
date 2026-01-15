package me.matl114.matlib.utils.serialization.simple;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.*;
import java.util.function.Function;
import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.RandomUtils;

public interface IntProvider {
    int sample();

    long nSample(long num);

    int getMin();

    int getMax();

    String getType();

    public static MapCodec<? extends IntProvider> dispatchCodec(String str) {
        return CODEC_MAP.get(str);
    }

    public static final Codec<Either<Integer, IntProvider>> CONSTANT_OR_DISPATCH_CODEC =
            Codec.either(Codec.INT, Codec.STRING.dispatch("type", IntProvider::getType, IntProvider::dispatchCodec));

    // either INT or 按字段dispatch 到MapCodec

    // Either 和 IntProvider转换
    public static final Codec<IntProvider> CODEC = CONSTANT_OR_DISPATCH_CODEC.xmap(
            either -> either.map(Const::new, Function.identity()),
            intProvider -> Objects.equals(intProvider.getType(), "const")
                    ? Either.left(((Const) intProvider).value())
                    : Either.right(intProvider));

    static final Map<String, MapCodec<? extends IntProvider>> CODEC_MAP = Map.of(
            "const", Codec.INT.fieldOf("value").xmap(Const::new, Const::value),
            "uniform",
                    RecordCodecBuilder.<Uniform>mapCodec(instance -> instance.group(
                                    Codec.INT.fieldOf("min").forGetter(Uniform::getMin),
                                    Codec.INT.fieldOf("max").forGetter(Uniform::getMax))
                            .apply(instance, Uniform::new)),
            "choice",
                    RecordCodecBuilder.<Choice>mapCodec(instance -> instance.group(
                                    Codec.INT.fieldOf("min").forGetter(Choice::getMin),
                                    Codec.INT.fieldOf("max").forGetter(Choice::getMax),
                                    Codec.DOUBLE.fieldOf("p").forGetter(Choice::prob))
                            .apply(instance, Choice::new)),
            "combine",
                    RecordCodecBuilder.<IntProvider>mapCodec(instance -> instance.group(IntProvider.CODEC
                                    .sizeLimitedListOf(256)
                                    .fieldOf("providers")
                                    .forGetter(i -> ((Combine) i).getProviders()))
                            .apply(instance, IntProvider::combine)));

    static IntProvider constInt(int value) {
        return new Const(value);
    }

    static int _recursiveCollect(List<IntProvider> list, List<IntProvider> list2) {
        int constValue = 0;
        for (IntProvider provider : list2) {
            if (provider instanceof Const con) {
                constValue += con.value();
            } else if (provider instanceof Combine comb) {
                constValue += _recursiveCollect(list, comb.getProviders());
            } else {
                list.add(provider);
            }
        }
        return constValue;
    }

    static IntProvider combine(List<IntProvider> providers) {
        List<IntProvider> combinedProviders = new ArrayList<>();
        int constValue = _recursiveCollect(combinedProviders, providers);
        if (combinedProviders.isEmpty()) {
            return new Const(constValue);
        } else if (combinedProviders.size() == 1 && constValue == 0) {
            return combinedProviders.get(0);
        } else {
            combinedProviders.add(new Const(constValue));
            return new Combine(combinedProviders);
        }
    }

    public static record Const(int value) implements IntProvider {

        @Override
        public int sample() {
            return value;
        }

        @Override
        public long nSample(long num) {
            return num * value;
        }

        @Override
        public int getMin() {
            return value;
        }

        @Override
        public int getMax() {
            return value;
        }

        @Override
        public String getType() {
            return "const";
        }
    }

    Random INT_RANDOM = new Random();

    public static record Uniform(int min, int max) implements IntProvider {

        @Override
        public int sample() {
            return min + INT_RANDOM.nextInt(max - min + 1);
        }

        @Override
        public long nSample(long num) {
            return num * min + (long) (INT_RANDOM.nextDouble(0, max - min) * (double) num);
        }

        @Override
        public int getMin() {
            return min;
        }

        @Override
        public int getMax() {
            return max;
        }

        @Override
        public String getType() {
            return "uniform";
        }
    }

    public static record Choice(int min, int max, double prob) implements IntProvider {
        @Override
        public int sample() {
            return (INT_RANDOM.nextDouble(0, 1) < prob) ? min : max;
        }

        @Override
        public long nSample(long num) {
            return num * max - (max - min) * RandomUtils.binomialNSample(INT_RANDOM, prob, num);
        }

        @Override
        public int getMin() {
            return min;
        }

        @Override
        public int getMax() {
            return max;
        }

        @Override
        public String getType() {
            return "choice";
        }
    }

    public static class Combine implements IntProvider {
        @Getter
        List<IntProvider> providers;

        int min;
        int max;

        public Combine(List<IntProvider> providers) {
            this.providers = providers;
            for (var provide : providers) {
                min += provide.getMin();
                max += provide.getMax();
            }
        }

        @Override
        public int sample() {
            int sum = 0;
            for (var provide : providers) {
                sum += provide.sample();
            }
            return sum;
        }

        @Override
        public long nSample(long num) {
            long sum = 0;
            for (var provide : providers) {
                sum += provide.nSample(num);
            }
            return sum;
        }

        @Override
        public int getMin() {
            return min;
        }

        @Override
        public int getMax() {
            return max;
        }

        @Override
        public String getType() {
            return "combine";
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder("Combine[");
            for (var provide : providers) {
                builder.append(provide.toString()).append(", ");
            }
            return builder.append("]").toString();
        }
    }
}
