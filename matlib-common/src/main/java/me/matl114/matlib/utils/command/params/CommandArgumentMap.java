package me.matl114.matlib.utils.command.params;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.matl114.matlib.utils.command.CommandUtils;
import me.matl114.matlib.utils.command.interruption.ValueAbsentError;

@AllArgsConstructor
public class CommandArgumentMap {
    @Getter
    Map<String, String> argsMap;

    public String getArg(String val) {
        return argsMap.get(val);
    }

    public String getNonnull(String val) {
        var re = argsMap.get(val);
        if (re == null) {
            throw new ValueAbsentError(val);
        }
        return re;
    }

    public int getInt(String val) {
        var re = getNonnull(val);
        return CommandUtils.gint(re, val);
    }

    public float getFloat(String val) {
        var re = getNonnull(val);
        return CommandUtils.gfloat(re, val);
    }

    public double getDouble(String val) {
        var re = getNonnull(val);
        return CommandUtils.gdouble(re, val);
    }

    public boolean getBoolean(String val) {
        var re = getNonnull(val);
        return CommandUtils.gbool(re, val);
    }
}
