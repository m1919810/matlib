package me.matl114.matlib.unitTest;

import com.google.common.base.Preconditions;
import java.util.Objects;
import me.matl114.matlib.common.lang.annotations.Internal;
import me.matl114.matlib.common.lang.exceptions.RuntimeAbort;
import org.bukkit.Bukkit;
import org.bukkit.World;

public interface TestCase extends TestResources {
    @Internal
    default void onEnable() {}

    @Internal
    default void onDisable() {}

    default void Assert(boolean expression) {
        Preconditions.checkArgument(expression, "Assertion failed!");
    }

    default void Assert(boolean expression, String message) {
        Preconditions.checkArgument(expression, "Assertion failed! " + message);
    }

    default void AssertEq(Object val1, Object val2) {
        Preconditions.checkArgument(Objects.equals(val1, val2), "Assertion failed! not equal:" + val1 + " " + val2);
    }

    default void AssertEq(Object val1, Object val2, String message) {
        Preconditions.checkArgument(Objects.equals(val1, val2), "Assertion failed! " + message);
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

    default void disableTest() {
        throw new RuntimeAbort();
    }
}
