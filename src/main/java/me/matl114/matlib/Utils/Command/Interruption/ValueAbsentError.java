package me.matl114.matlib.Utils.Command.Interruption;

import lombok.AllArgsConstructor;
import me.matl114.matlib.Common.Lang.Annotations.Note;
import me.matl114.matlib.Utils.Command.CommandGroup.AbstractMainCommand;
import me.matl114.matlib.Utils.Command.Params.SimpleCommandArgs;
import org.bukkit.command.CommandSender;

@Note("interrupt when no value present at this argument")
@AllArgsConstructor
public class ValueAbsentError extends ArgumentException {
    String argument;
    public ValueAbsentError(SimpleCommandArgs.Argument argument){
        this(argument.getArgsName());
    }
    @Override
    public void handleAbort(CommandSender sender, InterruptionHandler command) {
        command.handleValueAbsent(sender, argument);
    }
}
