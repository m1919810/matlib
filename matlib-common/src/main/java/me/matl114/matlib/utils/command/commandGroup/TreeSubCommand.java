package me.matl114.matlib.utils.command.commandGroup;

import me.matl114.matlib.utils.command.params.SimpleCommandArgs;

import java.util.*;
import java.util.stream.Stream;

public class TreeSubCommand extends SubCommand implements SubCommandDispatcher , SubCommand.SubCommandCaller {

    private final Map<String, SubCommand> subCommands;
    public TreeSubCommand(String name, String... helpContent) {
        super(name, new SimpleCommandArgs("dispatch_" + name), helpContent);
        this.subCommands = new LinkedHashMap<String, SubCommand>();
        setTabCompletor("dispatch_", ()-> subCommands.keySet().stream().toList());
    }
    public Stream<String> getHelp(String prefix){
        //the name should be included in the help
        return Stream.concat(Stream.of(prefix + help[0]) , SubCommandDispatcher.super.getHelp(prefix));
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

    public <W extends SubCommand> TreeSubBuilder<W> subBuilder(Builder<W> builder){
        return new TreeSubBuilder<>(this, builder);
    }


    public static class TreeSubBuilder<W extends SubCommand> extends Builder<W>{
        TreeSubCommand treeSubCommand;
        protected TreeSubBuilder(TreeSubCommand root, Builder<W> builder) {
            super(builder);
            this.treeSubCommand = root;
        }

        public TreeSubCommand complete(){
            W sb = build();
            treeSubCommand.registerSub(sb);
            return treeSubCommand;
        }
    }
}
