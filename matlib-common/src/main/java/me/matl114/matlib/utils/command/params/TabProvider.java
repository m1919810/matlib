package me.matl114.matlib.utils.command.params;

import org.bukkit.command.CommandSender;

import java.util.List;

public interface TabProvider {
    public List<String> getTab(CommandSender sender);
}
