package me.matl114.matlib.utils.command.params;

import java.util.List;
import java.util.stream.Stream;
import org.bukkit.command.CommandSender;

public interface TabProvider {
    public Stream<String> getTab(CommandSender sender, List<InputArgument> arguments);
}
