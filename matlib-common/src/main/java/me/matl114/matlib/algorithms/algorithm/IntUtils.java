package me.matl114.matlib.algorithms.algorithm;

public class IntUtils {
    private static final long MASK = ((long) 1 << 32) - 1;

    public static long makePair(int a, int b) {
        return ((long) a << 32) | (b & MASK);
    }

    public static int getFirstInt(long pair) {
        return (int) (pair >>> 32);
    }

    /**
     * 从打包的long中提取低位int
     */
    public static int getSecondInt(long pair) {
        return (int) (pair & MASK);
    }

    public static long addPair(long pair, int addX, int addZ) {
        int x = getFirstInt(pair) + addX;
        int z = getSecondInt(pair) + addZ;
        return makePair(x, z);
    }

    public static long addPair(long pair, long pair2) {
        int x = getFirstInt(pair) + getFirstInt(pair2);
        int z = getSecondInt(pair) + getSecondInt(pair2);
        return makePair(x, z);
    }
}
