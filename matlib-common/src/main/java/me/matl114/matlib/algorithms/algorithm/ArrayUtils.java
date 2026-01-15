package me.matl114.matlib.algorithms.algorithm;

import java.util.List;
import java.util.stream.IntStream;

public class ArrayUtils {
    private static final List<Integer> INTEGER_TO_100 =
            IntStream.range(0, 255).boxed().toList();

    public static List<Integer> getArangeList(int dx) {
        if (dx > 255) {
            return IntStream.range(0, dx).boxed().toList();
        }
        return INTEGER_TO_100.subList(0, dx);
    }
}
