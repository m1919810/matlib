package me.matl114.matlib.utils.command.commandGroup;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import org.bukkit.command.CommandSender;

public class TreeSubCommand extends SubCommand implements SubCommandDispatcher, SubCommand.SubCommandCaller {
    private SubCommand fallBackCommand = null;
    private Supplier<Stream<String>> fallbackTabSuggestor = Stream::of;
    private final Map<String, SubCommand> subCommands;

    public TreeSubCommand(String name, String... helpContent) {
        super(name, new SimpleCommandArgs("dispatch_" + name), helpContent);
        this.subCommands = new LinkedHashMap<String, SubCommand>();
        setTabCompletor("dispatch_" + name, this::onSubCommandSuggest);
    }

    public List<String> onSubCommandSuggest() {
        return Stream.concat(subCommands.keySet().stream(), fallbackTabSuggestor.get())
                .toList();
    }

    public Stream<String> getHelp(String prefix) {
        // the name should be included in the help
        return Stream.concat(Stream.of(help).map(s -> prefix + s), SubCommandDispatcher.super.getHelp(prefix));
    }

    @Override
    public void registerSub(SubCommand command) {
        this.subCommands.put(command.getName(), command);
    }

    public void setFallbackCommand(SubCommand fallbackCommand, Supplier<Stream<String>> fallbackTabSuggestor) {
        this.fallBackCommand = fallbackCommand;
        this.fallbackTabSuggestor = fallbackTabSuggestor;
    }

    @Override
    public SubCommand getSubCommand(String name) {
        return subCommands.get(name);
    }

    @Override
    public Collection<SubCommand> getSubCommands() {
        return subCommands.values();
    }

    @Override
    public SubCommand getFallbackCommand() {
        return this.fallBackCommand;
    }

    public Stream<String> onCustomHelp(CommandSender sender, ArgumentReader arguments) {
        return SubCommandDispatcher.super.onCustomHelp(sender, arguments);
    }
}
