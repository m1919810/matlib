package me.matl114.matlib.utils.command.interruption;

import org.bukkit.command.CommandSender;

public class ValueOutOfRangeError extends ArgumentException {
    String name;
    String from;
    String to;
    String value;
    TypeError.BaseArgumentType type;

    public ValueOutOfRangeError(String name, int from, int to, int input) {
        this.name = name;
        this.from = String.valueOf(from);
        this.to = String.valueOf(to);
        this.value = String.valueOf(input);
        this.type = TypeError.BaseArgumentType.INT;
    }

    public ValueOutOfRangeError(String name, float from, float to, float input) {
        this.name = name;
        this.from = String.valueOf(from);
        this.to = String.valueOf(to);
        this.value = String.valueOf(input);

        this.type = TypeError.BaseArgumentType.FLOAT;
    }

    public ValueOutOfRangeError(String name, String from, String to, String input, TypeError.BaseArgumentType type) {
        this.name = name;
        this.from = from;
        this.to = to;
        this.value = input;
        this.type = type;
    }

    @Override
    public void handleAbort(CommandSender sender, InterruptionHandler command) {
        command.handleValueOutOfRange(sender, this.name, this.type, this.from, this.to, this.value);
    }
}
