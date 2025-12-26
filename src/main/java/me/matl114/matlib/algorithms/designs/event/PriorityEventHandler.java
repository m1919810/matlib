package me.matl114.matlib.algorithms.designs.event;

import java.util.function.Consumer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import me.matl114.matlib.common.lang.annotations.Note;
import org.jetbrains.annotations.NotNull;

@AllArgsConstructor
@ToString
public class PriorityEventHandler<W, T extends Event> implements Comparable<PriorityEventHandler<?, T>> {
    @Getter
    final W owner;

    @Note("Priority 从低到高依次执行,同Priority,按注册顺序执行")
    final int priority;

    @Getter
    final boolean ignoreIfCancel;

    final Consumer<T> task;

    public void task(T va) {
        task.accept(va);
    }

    @Override
    public int compareTo(@NotNull PriorityEventHandler<?, T> o) {
        return Integer.compare(this.priority, o.priority);
    }
}
