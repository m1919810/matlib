package me.matl114.matlib.utils.command.commandGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
public class TaskSubCommand extends SubCommand{
    CommandExecutor executor;
    public TaskSubCommand(String name, SimpleCommandArgs argsTemplate, String... help) {
        super(name, argsTemplate, help);
    }

    @Override
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
        return executor != null && executor.onCommand(var1, var2, var3, var4);
    }
}
