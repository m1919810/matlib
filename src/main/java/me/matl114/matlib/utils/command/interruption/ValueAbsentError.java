package me.matl114.matlib.utils.command.interruption;

import lombok.AllArgsConstructor;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import org.bukkit.command.CommandSender;

@Note("interrupt when no value present at this argument")
@AllArgsConstructor
public class ValueAbsentError extends ArgumentException {
    String argument;

    public ValueAbsentError(SimpleCommandArgs.Argument argument) {
        this(argument.getArgsName());
    }

    @Override
    public void handleAbort(CommandSender sender, InterruptionHandler command) {
        command.handleValueAbsent(sender, argument);
    }
}
