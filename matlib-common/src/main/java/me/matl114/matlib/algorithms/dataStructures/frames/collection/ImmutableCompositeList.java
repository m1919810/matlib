package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImmutableCompositeList<W> extends AbstractList<W> {
    private final List<List<W>> components;
    private final int[] offsets; // 每个组件在总列表中的起始偏移
    private final int totalSize;

    public ImmutableCompositeList(List<W>... components) {
        this(Arrays.asList(components));
    }

    public ImmutableCompositeList(List<List<W>> components) {
        this.components = new ArrayList<>(components);
        this.offsets = new int[components.size()];

        // 计算偏移量和总大小
        int offset = 0;
        for (int i = 0; i < components.size(); i++) {
            offsets[i] = offset;
            offset += components.get(i).size();
        }
        this.totalSize = offset;
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
        int left = 0;
        int right = offsets.length - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            if (globalIndex >= offsets[mid]) {
                if (mid == offsets.length - 1 || globalIndex < offsets[mid + 1]) {
                    return mid;
                }
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        return -1; // 不应该发生
    }

    @Override
    public int size() {
        return totalSize;
    }
}
