package me.matl114.matlib.utils.command.commandGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import me.matl114.matlib.utils.command.interruption.ArgumentException;
import me.matl114.matlib.utils.command.interruption.ValueUnexpectedError;
import me.matl114.matlib.utils.command.interruption.PermissionDenyError;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SubCommandDispatcher extends CustomTabExecutor, SubCommand.SubCommandCaller {



    default List<String> onCustomTabComplete(CommandSender sender, @Nullable Command apiUsage, ArgumentReader arguments){
        if(hasPermission(sender)){
            var re = parseInput(arguments);
            if (!arguments.hasNext()) {
                List<String> provider = re.getTabComplete(sender);
                return provider == null ? new ArrayList<>() : provider;
            } else {
                SubCommand subCommand = getSubCommand(re.nextArg());
                if (subCommand != null) {
                    List<String> tab = subCommand.onCustomTabComplete(sender, apiUsage, arguments);  //parseInput(elseArg).getTabComplete();
                    if (tab != null) {
                        return tab;
                    }
                }
            }
        }

        return new ArrayList<>();
    }

    @Override
    default boolean onCustomCommand(@NotNull CommandSender var1, @Nullable Command apiUsage, ArgumentReader reader) throws ArgumentException {
        if(hasPermission(var1)){
            if(reader.hasNext()) {
                String next = reader.next();
                SubCommand command = getSubCommand(next);
                if (command != null) {
                    // add permission check
                    if (command.hasPermission(var1)) {
                        return command.onCustomCommand(var1, apiUsage, reader);
                    } else {
                        throw new PermissionDenyError(permissionRequired(), reader);
                    }
                }
            }
            // not consume
            throw new ValueUnexpectedError(reader.stepBack());
        }else{
            throw new PermissionDenyError(permissionRequired(), reader);
        }

    }

    default Stream<String> getHelp(String prefix){
        String prefix2 = prefix + getName() + " ";
        return Streams.concat(getSubCommands().stream().map(cmd -> cmd.getHelp(prefix2)).toArray(Stream[]::new));
    }
}
