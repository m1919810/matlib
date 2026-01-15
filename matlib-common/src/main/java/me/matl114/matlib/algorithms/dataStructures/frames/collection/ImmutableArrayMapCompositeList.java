package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImmutableArrayMapCompositeList<W> extends AbstractList<W> {
    private final List<List<W>> components;
    private final int[] offsets; // 每个组件在总列表中的起始偏移
    private final int totalSize;
    private final int firstSize;
    private int lastListIndex;

    public ImmutableArrayMapCompositeList(List<W>... components) {
        this(Arrays.asList(components));
    }

    public ImmutableArrayMapCompositeList(List<List<W>> components) {
        this.components = new ArrayList<>(components);

        // 计算偏移量和总大小
        int offset = 0;
        for (int i = 0; i < components.size(); i++) {
            offset += components.get(i).size();
        }
        this.totalSize = offset;
        this.firstSize = components.get(0).size();
        this.offsets = new int[totalSize];
        this.lastListIndex = firstSize;
    }

    @Override
    public W get(int index) {
        if (index < 0 || index >= totalSize) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + totalSize);
        }

        // 二分查找找到对应的组件
        int componentIndex = findComponentIndex(index);
        List<W> component = components.get(componentIndex);
        int localIndex = index - offsets[componentIndex];

        return component.get(localIndex);
    }

    @Override
    public W set(int index, W element) {
        int componentIndex = findComponentIndex(index);
        List<W> component = components.get(componentIndex);
        int localIndex = index - offsets[componentIndex];

        return component.set(localIndex, element);
    }

    private int findComponentIndex(int globalIndex) {
        // 二分查找找到对应的组件
        if (globalIndex < firstSize) {
            return 0;
        }
        if (globalIndex < lastListIndex) {
            return offsets[globalIndex];
        }
        int lastList = offsets[lastListIndex - 1];
        do {
            lastList += 1;
            int size = components.get(lastList).size();
            Arrays.fill(offsets, lastListIndex, lastListIndex + size, lastList);
            lastListIndex += size;
        } while (lastListIndex >= globalIndex);
        return offsets[globalIndex];
    }

    @Override
    public int size() {
        return totalSize;
    }
}
