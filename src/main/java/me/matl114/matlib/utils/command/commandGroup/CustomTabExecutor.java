package me.matl114.matlib.utils.command.commandGroup;

import me.matl114.matlib.utils.command.CommandUtils;
import me.matl114.matlib.utils.command.interruption.TypeError;
import org.bukkit.command.TabExecutor;

public interface CustomTabExecutor extends TabExecutor {
    static int gint(String val) {
        return CommandUtils.gint(val, (String) null);
    }

    static float gfloat(String val) {
        return CommandUtils.gfloat(val, (String) null);
    }

    static double gdouble(String val) {
        return CommandUtils.gdouble(val, (String) null);
    }

    static boolean gbool(String val) {
        return CommandUtils.gbool(val, (String) null);
    }

    static void enumError(String input) {
        throw new TypeError((String) null, TypeError.BaseArgumentType.ENUM, input);
    }

    static void checkRange(int val, int from, int to) {
        CommandUtils.range(null, val, from, to);
    }

    static void checkRange(float val, float from, float to) {
        CommandUtils.range(null, val, from, to);
    }

    static void checkRange(double val, double from, double to) {
        CommandUtils.range(null, val, from, to);
    }
}
