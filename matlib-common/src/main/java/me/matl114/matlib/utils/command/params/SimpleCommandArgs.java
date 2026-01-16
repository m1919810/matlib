package me.matl114.matlib.utils.command.params;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.Streams;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.matl114.matlib.algorithms.algorithm.FuncUtils;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import org.bukkit.command.CommandSender;

public class SimpleCommandArgs {
    //todo: add Argument type,  consume more args
    //todo: use StringReader
    public static class Argument implements TabProvider {
        @Getter
        private final String argsName;

        public HashSet<String> argsAlias;

        @Getter
        @Setter
        private String defaultValue = null;

        public Function<CommandSender, List<String>> tabCompletor = (p)->List.of();

        public Argument(String argsName) {
            this.argsName = argsName;
            this.argsAlias = new HashSet<>();
            argsAlias.add(argsName);
            argsAlias.add(argsName.toLowerCase());
            argsAlias.add(argsName.toUpperCase());
            argsAlias.add(argsName.substring(0, 1));
            argsAlias.add(argsName.substring(0, 1).toLowerCase());
            argsAlias.add(argsName.substring(0, 1).toUpperCase());
        }

        public boolean isAlias(String arg) {
            return argsAlias.contains(arg);
        }

        public List<String> getTab(CommandSender sender) {
            return tabCompletor.apply(sender);
        }

    }
    @Accessors(fluent = true, chain = true)
    @Getter
    @Setter
    public static class ArgumentBuilder {
        String name;
        String defaultValue;
        List<Function<CommandSender, Stream<String>>> tabCompletor = new ArrayList<>();

        public ArgumentBuilder tabSupplier(Supplier<Stream<String>> list){
            tabCompletor.add((p)-> list.get());
            return this;
        }

        public ArgumentBuilder tabCompletor(Function<CommandSender, Stream<String>> list){
            tabCompletor.add(list);
            return this;
        }

        Set<String> alias = new HashSet<>();

        public ArgumentBuilder alias(String alias) {
            this.alias.add(alias);
            return this;
        }

        public Argument build(){
            var arg = new Argument(name);
            arg.setDefaultValue(defaultValue);
            arg.tabCompletor = (cmd)->{
                List<String> strings = new ArrayList<>();
                for (var en : tabCompletor){
                    en.apply(cmd).forEach(strings::add);
                }
                return strings;
            };
            arg.argsAlias.addAll(alias);
            return arg;
        }
    }


    @Getter
    Argument[] args;

    public SimpleCommandArgs(String... args) {
        this.args = Arrays.stream(args).map(Argument::new).toArray(Argument[]::new);
    }

    public SimpleCommandArgs(Argument... args) {
        this.args = args;
    }

    public void setDefault(String arg, String defaultValue) {
        for (Argument a : args) {
            if (a.argsName.equals(arg)) {
                a.defaultValue = defaultValue;
            }
        }
    }

    public void setTabCompletor(String arg, Supplier<List<String>> tabCompletor) {
        for (Argument a : args) {
            if (a.argsName.equals(arg)) {
                a.tabCompletor =  (o)-> tabCompletor.get();
            }
        }
    }

    public void setTabCompletor(String arg, Function<CommandSender, List<String>> tabCompletor) {
        for (Argument a : args) {
            if (a.argsName.equals(arg)) {
                a.tabCompletor = tabCompletor;
            }
        }
    }

    public SimpleCommandInputStream parseInputStream(ArgumentReader reader) {
        final HashMap<Argument, String> argsMap = new HashMap<>();
//        Iterator<String> iter = Arrays.stream(input).iterator();
        List<Argument> argSet = Arrays.stream(args).collect(Collectors.toCollection(ArrayList::new));
        while (reader.hasNext() && !argSet.isEmpty()) {
            String arg = reader.next();
            if (arg.startsWith("-")) {
                Argument selected = null;
                String trueName = arg.replaceFirst("^-+", "");
                for (Argument a : args) {
                    if (a.isAlias(trueName)) {
                        selected = a;
                        break;
                    }
                }
                if (selected != null) {
                    argSet.remove(selected);
                    if (arg.startsWith("--")) {
                        // --args inputValue
                        if (reader.hasNext()) {
                            String arg2 = reader.next();

                            argsMap.put(selected, arg2);
                        }
                    } else {
                        // -f -v means boolean
                        argsMap.put(selected, "true");
                    }

                } else {
                    //the argument is broken, expect a argument but no argument is here
                    break;
                }
            } else {
                Argument selected = argSet.remove(0);
                argsMap.put(selected, arg);
            }
        }
        return new SimpleCommandInputStream(args, argsMap);
    }
}
