package me.matl114.matlib.utils.command.params;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface TabProvider {
    public List<String> getTab(CommandSender sender);
}
