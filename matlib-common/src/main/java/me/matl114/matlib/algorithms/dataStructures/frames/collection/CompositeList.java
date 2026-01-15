package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import me.matl114.matlib.algorithms.algorithm.IntUtils;

public class CompositeList<W> extends AbstractList<W> {
    private final List<List<W>> components;

    public CompositeList(List<W>... components) {
        this(Arrays.asList(components));
    }

    public CompositeList(List<List<W>> components) {
        this.components = new ArrayList<>(components);
    }

    @Override
    public W get(int index) {
        long componentIndex = findComponentIndex(index);
        List<W> component = components.get(IntUtils.getFirstInt(componentIndex));
        int localIndex = IntUtils.getSecondInt(componentIndex);

        return component.get(localIndex);
    }

    @Override
    public W set(int index, W element) {
        long componentIndex = findComponentIndex(index);
        List<W> component = components.get(IntUtils.getFirstInt(componentIndex));
        int localIndex = IntUtils.getSecondInt(componentIndex);

        return component.set(localIndex, element);
    }

    @Override
    public void add(int index, W element) {
        long componentIndex = findComponentIndex(index);

        List<W> component = components.get(IntUtils.getFirstInt(componentIndex));
        int localIndex = IntUtils.getSecondInt(componentIndex);

        component.add(localIndex, element);
    }

    private long findComponentIndex(int globalIndex) {
        int len = 0;
        for (int i = 0; i < components.size(); i++) {
            int len_ = len + components.get(i).size();
            if (globalIndex < len_) {
                return IntUtils.makePair(i, globalIndex - len);
            }
            len = len_;
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public int size() {
        int size = 0;
        for (List<W> component : components) {
            size += component.size();
        }
        return size;
    }
}
