package me.matl114.matlib.utils.command.commandGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.command.interruption.ArgumentException;
import me.matl114.matlib.utils.command.interruption.ArgumentFormatError;
import me.matl114.matlib.utils.command.interruption.PermissionDenyError;
import me.matl114.matlib.utils.command.interruption.ValueOutOfRangeError;
import me.matl114.matlib.utils.command.params.SimpleCommandInputStream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.objectweb.asm.Opcodes;

public interface SubCommandDispatcher extends CustomTabExecutor, SubCommand.SubCommandCaller {

    default List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
        var re = parseInput(var4);
        if (re.getB().length == 0) {
            List<String> provider = re.getA().getTabComplete();
            return provider == null ? new ArrayList<>() : provider;
        } else {
            SubCommand subCommand = getSubCommand(re.getA().nextArg());
            if (subCommand != null) {
                String[] elseArg = re.getB();
                List<String> tab = subCommand.parseInput(elseArg).getA().getTabComplete();
                if (tab != null) {
                    return tab;
                }
            }
        }
        return new ArrayList<>();
    }


    @Override
    default boolean onCommand(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
        if(var4.length > 0) {
            SubCommand command = getSubCommand(var4[0]);
            if (command != null) {
                // add permission check
                if (command.hasPermission(var1)) {
                    String[] elseArg = Arrays.copyOfRange(var4, 1, var4.length);
                    return command.getExecutor().onCommand(var1, var2, var3 + " " + command.getName(), elseArg);
                } else {
                    throw new PermissionDenyError(permissionRequired(), Optional.of(var4[0]));
                }
            }
        }
        throw new ArgumentFormatError(var3, var4);
    }

    default Stream<String> getHelp(String prefix){
        String prefix2 = prefix + getName() + " ";
        return Streams.concat(getSubCommands().stream().map(cmd -> cmd.getHelp(prefix2)).toArray(Stream[]::new));
    }
}
