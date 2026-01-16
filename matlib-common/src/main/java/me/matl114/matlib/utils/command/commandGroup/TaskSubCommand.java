package me.matl114.matlib.utils.command.commandGroup;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import me.matl114.matlib.utils.command.params.SimpleCommandInputStream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Setter
@Getter
@Accessors(fluent = true, chain = true)
public class TaskSubCommand extends SubCommand{
    StreamCommandExecutor executor;
    public TaskSubCommand(String name, SimpleCommandArgs argsTemplate, String... help) {
        super(name, argsTemplate, help);
    }


    @Override
    public List<String> onCustomTabComplete(CommandSender sender, @Nullable Command command, ArgumentReader arguments) {
        var re =  this.parseInput(arguments);
        if(arguments.hasNext()){
            //already filled all the arguments so use executor to supply the extra args
            return executor == null ? List.of() : executor.supplyTab(sender, re, arguments);
        }else{
            return re.getTabComplete(sender);
        }
    }

    @Override
    public boolean onCustomCommand(CommandSender sender, Command command, ArgumentReader arguments) {
        return executor != null && executor.execute(sender, parseInput(arguments), arguments);
    }

    public static interface StreamCommandExecutor {
        public boolean execute(CommandSender var1, SimpleCommandInputStream streamArgs, ArgumentReader argsReader);

        default List<String> supplyTab(CommandSender var1, SimpleCommandInputStream streamArgs, ArgumentReader argsReader) {
            return List.of();
        }
    }

}
