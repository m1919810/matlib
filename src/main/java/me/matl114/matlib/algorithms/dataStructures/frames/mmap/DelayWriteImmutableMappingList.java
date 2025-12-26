package me.matl114.matlib.algorithms.dataStructures.frames.mmap;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import me.matl114.matlib.algorithms.dataStructures.frames.cowCollection.COWImmutableListView;

public class DelayWriteImmutableMappingList<W, T> extends MappingList<W, T> {
    Consumer<List<W>> batchWriteback;
    List<W> originList;

    public DelayWriteImmutableMappingList(
            Function<W, T> reader, Function<T, W> writer, List<W> origin, Consumer<List<W>> batchWriteBack) {
        super(reader, writer, new COWImmutableListView<>(origin, ArrayList::new));
        this.batchWriteback = batchWriteBack;
        this.originList = origin;
    }

    public void batchWriteback() {
        List<W> handle = ((COWImmutableListView<W>) this.origin).getHandle();
        if (handle != this.originList) {
            // write back , yeee
            batchWriteback.accept(handle);
        }
    }

    public boolean isDelayWrite() {
        return true;
    }
}
