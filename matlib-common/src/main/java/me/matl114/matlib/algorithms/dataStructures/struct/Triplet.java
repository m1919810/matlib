package me.matl114.matlib.algorithms.dataStructures.struct;

import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import lombok.Setter;

public final class Triplet<A extends Object, B extends Object, C extends Object> implements Cloneable {
    @Getter
    @Setter
    private A a;

    @Setter
    @Getter
    private B b;

    @Setter
    @Getter
    private C c;

    private static final Triplet instance = new Triplet(null, null, null);

    public static <T extends Object, W extends Object, R extends Object> Triplet<T, W, R> of(T w1, W w2, R w3) {
        Triplet newInstance = instance.clone();
        newInstance.a = w1;
        newInstance.b = w2;
        newInstance.c = w3;
        return (Triplet<T, W, R>) newInstance;
    }

    public Triplet(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Triplet clone() {
        try {
            return (Triplet) super.clone();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (o instanceof Triplet<?, ?, ?> pair) {
            return Objects.equals(this.a, pair.a) && Objects.equals(this.b, pair.b) && Objects.equals(this.c, pair.c);
        } else {
            return false;
        }
    }

    public int hashCode() {
        int result = 1;
        result = result * 67 + (this.a == null ? 43 : this.a.hashCode());
        result = result * 67 + (this.b == null ? 43 : this.b.hashCode());
        result = result * 67 + (this.c == null ? 43 : this.c.hashCode());
        return result;
    }

    @Nonnull
    public String toString() {
        return "Triplet(a=" + this.a + ", b=" + this.b + ", c=" + this.c + ")";
    }
}
