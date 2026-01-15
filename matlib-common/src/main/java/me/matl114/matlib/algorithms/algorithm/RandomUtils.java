package me.matl114.matlib.algorithms.algorithm;

import java.util.Random;

public class RandomUtils {
    public static long uniformNSample(Random random, int n, long N) {
        if (N <= 8) {
            int sum = 0;
            for (int i = 0; i < N; i++) {
                sum += random.nextInt(n);
            }
            return sum;
        } else {
            int gaussian = (int) Math.round(random.nextGaussian(N * (n - 1) / 2.0, Math.sqrt(N * (n * n - 1) / 12.0)));
            return MathUtils.clamp(gaussian, 0, N * (n - 1));
        }
    }

    public static long binomialNSample(Random random, double p, long N) {
        if (N <= 8) {
            int sum = 0;
            for (int i = 0; i < N; i++) {
                if (random.nextDouble(0, 1) < p) {
                    sum += 1;
                }
            }
            return sum;
        } else {
            int gaussian = (int) Math.round(random.nextGaussian(N * p, Math.sqrt(N * p * (1 - p))));
            return MathUtils.clamp(gaussian, 0, N);
        }
    }

    public static double[] normalizeWeight(double[] weights) {
        double[] normalizedWeights = new double[weights.length];
        double sum = 0.0d;
        for (int i = 0; i < weights.length; i++) {
            sum += weights[i];
        }
        for (int i = 0; i < weights.length; i++) {
            normalizedWeights[i] = weights[i] / sum;
        }
        return normalizedWeights;
    }
}
