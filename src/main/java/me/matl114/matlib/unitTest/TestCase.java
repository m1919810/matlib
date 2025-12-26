package me.matl114.matlib.unitTest;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.World;

public interface TestCase extends TestResources {
    default void Assert(boolean expression) {
        Preconditions.checkArgument(expression, "Assertion failed!");
    }

    default void AssertEq(Object val1, Object val2) {
        Preconditions.checkArgument(Objects.equals(val1, val2), "Assertion failed! not equal:" + val1 + " " + val2);
    }

    default void AssertNEq(Object val1, Object val2) {
        Preconditions.checkArgument(!Objects.equals(val1, val2), "Assertion failed! equal:" + val1 + " AND " + val2);
    }

    default void AssertNN(Object expression) {
        Preconditions.checkNotNull(expression, "Assertion failed!");
    }

    default World testWorld() {
        return Bukkit.getWorlds().get(0);
    }
}
