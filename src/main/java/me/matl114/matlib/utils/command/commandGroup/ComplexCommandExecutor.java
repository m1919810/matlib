package me.matl114.matlib.utils.command.commandGroup;

import java.util.ArrayList;
import java.util.List;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public interface ComplexCommandExecutor extends CustomTabExecutor, SubCommand.SubCommandCaller {
    public SubCommand getMainCommand();

    public SubCommand getSubCommand(String name);

    default List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
        var re = getMainCommand().parseInput(var4);
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

    @Note("the \"async\" means that it can be called either on main or off main")
    default boolean onCommandAsync(
            @NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4) {
        return onCommand(var1, var2, var3, var4);
    }
}
