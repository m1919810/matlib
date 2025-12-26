package me.matl114.matlib.algorithms.dataStructures.struct;

import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

public final class Pair<A extends Object, B extends Object> implements Cloneable {
    @Getter
    @Setter
    private A a;

    @Setter
    @Getter
    private B b;

    private static final Pair instance = new Pair(null, null);

    public static <T extends Object, W extends Object> Pair<T, W> of(T w1, W w2) {
        Pair newInstance = instance.clone();
        newInstance.a = w1;
        newInstance.b = w2;
        return (Pair<T, W>) newInstance;
    }

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public Pair clone() {
        try {
            return (Pair) super.clone();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Pair<?, ?> pair) {
            return Objects.equals(this.a, pair.a) && Objects.equals(this.b, pair.b);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = 1;
        result = result * 61 + (this.a == null ? 43 : this.a.hashCode());
        result = result * 61 + (this.b == null ? 43 : this.b.hashCode());
        return result;
    }

    public io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair<A, B> tosfPair() {
        return new io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair<A, B>(this.a, this.b);
    }

    @Nonnull
    public String toString() {
        return "Pair(a=" + this.a + ", b=" + this.b + ")";
    }
}
