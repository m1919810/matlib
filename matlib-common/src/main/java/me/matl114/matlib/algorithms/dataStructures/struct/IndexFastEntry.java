package me.matl114.matlib.algorithms.dataStructures.struct;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class IndexFastEntry<T> implements IndexEntry<T> {
    public void addIndex() {
        ++index;
    }

    protected int index;

    private T value;
}
