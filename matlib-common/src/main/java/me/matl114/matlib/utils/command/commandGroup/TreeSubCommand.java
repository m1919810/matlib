package me.matl114.matlib.utils.command.commandGroup;

import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Stream;

public class TreeSubCommand extends SubCommand implements SubCommandDispatcher , SubCommand.SubCommandCaller {

    private final Map<String, SubCommand> subCommands;
    public TreeSubCommand(String name, String... helpContent) {
        super(name, new SimpleCommandArgs("dispatch_" + name), helpContent);
        this.subCommands = new LinkedHashMap<String, SubCommand>();
        setTabCompletor("dispatch_" + name, ()-> subCommands.keySet().stream().toList());
    }
    public Stream<String> getHelp(String prefix){
        //the name should be included in the help
        return Stream.concat(Stream.of(help).map(s -> prefix + s) , SubCommandDispatcher.super.getHelp(prefix));
    }

    @Override
    public void registerSub(SubCommand command) {
        this.subCommands.put(command.getName(), command);

    }

    @Override
    public SubCommand getSubCommand(String name) {
        return subCommands.get(name);
    }

    @Override
    public Collection<SubCommand> getSubCommands() {
        return subCommands.values();
    }

    public Stream<String> onCustomHelp(CommandSender sender, ArgumentReader arguments){
        return SubCommandDispatcher.super.onCustomHelp(sender, arguments);
    }


}
