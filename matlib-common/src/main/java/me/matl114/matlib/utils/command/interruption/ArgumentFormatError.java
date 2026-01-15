package me.matl114.matlib.utils.command.interruption;

import lombok.AllArgsConstructor;
import org.bukkit.command.CommandSender;

@AllArgsConstructor
public class ArgumentFormatError extends ArgumentException {
    String currentCommand;
    String[] currentArgs;
    @Override
    public void handleAbort(CommandSender sender, InterruptionHandler command) {
        command.handleWrongArgumentFormat(sender, currentCommand, currentArgs);
    }
}
