package me.matl114.matlib.algorithms.designs.rand;

import java.util.*;

public interface RandomSelector {
    public int getChoices();

    public int sample();

    public static RandomSelector generate(double[] doubles) {
        boolean isSame = true;
        double a1 = doubles[0];
        for (int i = 1; i < doubles.length; i++) {
            if (doubles[i] != a1) {
                isSame = false;
                break;
            }
        }
        if (isSame) {
            return new Uniform(doubles.length);
        } else {
            return new Weighted(doubles);
        }
    }

    public static class Uniform implements RandomSelector {
        Random rand = new Random();
        int n;

        public Uniform(int n) {
            this.n = n;
        }

        @Override
        public int getChoices() {
            return n;
        }

        @Override
        public int sample() {
            return rand.nextInt(n);
        }
    }

    public static class Weighted implements RandomSelector {
        int[] aliasTable;
        double[] aliasProbability;
        int N;
        Random rand = new Random();

        public Weighted(double[] weight) {
            double weightSum = 0.0;
            for (int i = 0; i < weight.length; i++) {
                weightSum += weight[i];
            }
            double[] weightCopy = new double[weight.length];
            N = weight.length;
            aliasTable = new int[weightCopy.length];
            aliasProbability = new double[weightCopy.length];
            // 归一化

            Deque<Integer> small = new ArrayDeque<Integer>(weightCopy.length);
            Deque<Integer> last = new ArrayDeque<>(weightCopy.length);
            for (int i = 0; i < weightCopy.length; i++) {
                weightCopy[i] = weight[i] * N / weightSum;
                if (weightCopy[i] > 1 + 1e-6) {
                    small.addLast(i);
                } else if (weightCopy[i] < 1 - 1e-6) {
                    last.addLast(i);
                } else {
                    // write to table
                    aliasTable[i] = i;
                    aliasProbability[i] = 1.0D;
                }
            }
            // 初始化table
            while (!small.isEmpty() && !last.isEmpty()) {
                int s = small.removeFirst();
                int l = last.removeFirst();
                aliasProbability[s] = weightCopy[s];
                aliasTable[s] = l;
                weightCopy[l] = weightCopy[l] - 1.0 + weightCopy[s];
                if (weightCopy[l] < 1 - 1e-6) {
                    small.addLast(l);
                } else if (weightCopy[l] > 1 + 1e-6) {
                    last.addLast(l);
                } else {
                    aliasTable[l] = l;
                    aliasProbability[l] = 1.0D;
                }
            }
            // 此时的状态必然是全1.0 否则存在一个小的一个大的 不可能离开上方的while循环
            while (!last.isEmpty()) {
                int l = last.removeFirst();
                aliasProbability[l] = 1.0;
                aliasTable[l] = l;
            }

            while (!small.isEmpty()) {
                int s = small.removeFirst();
                aliasProbability[s] = 1.0;
                aliasTable[s] = s;
            }
        }

        @Override
        public int getChoices() {
            return N;
        }

        @Override
        public int sample() {
            int col = rand.nextInt(N);
            double probability = aliasProbability[col];
            if (probability > 1.0D - 1e-6) {
                return col;
            } else {
                return (rand.nextDouble() < probability) ? col : aliasTable[col];
            }
        }
    }
}
