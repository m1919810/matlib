package me.matl114.matlib.algorithms.dataStructures.frames.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;

public class PairList<T, W> extends ArrayList<Pair<T, W>> {
    public PairList(int i) {
        super(i);
    }

    public PairList() {
        super();
    }

    public void put(T key, W value) {
        this.add(Pair.of(key, value));
    }

    public W get(T key) {
        for (var it : this) {
            if (Objects.equals(it.getA(), key)) {
                return it.getB();
            }
        }
        return null;
    }

    public W removeEntry(T key) {
        for (int i = 0; i < this.size(); i++) {
            var getIt = this.get(i);
            if (Objects.equals(getIt.getA(), key)) {
                this.remove(i);
                return getIt.getB();
            }
        }
        return null;
    }

    public List<T> keyList() {
        List<T> result = new ArrayList<>(this.size() + 2);
        for (var it : this) {
            result.add(it.getA());
        }
        return result;
    }

    public List<W> valueList() {
        List<W> result = new ArrayList<>(this.size() + 2);
        for (var it : this) {
            result.add(it.getB());
        }
        return result;
    }
}
