package me.matl114.matlib.utils.command.params;

import java.util.*;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;

public class SimpleCommandArgs {
    public static class Argument implements TabProvider {
        @Getter
        private final String argsName;

        public HashSet<String> argsAlias;

        @Getter
        @Setter
        private String defaultValue = null;

        public Supplier<List<String>> tabCompletor = List::of;

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

        public List<String> getTab() {
            return tabCompletor.get();
        }
    }

    @Getter
    Argument[] args;

    public SimpleCommandArgs(String... args) {
        this.args = Arrays.stream(args).map(Argument::new).toArray(Argument[]::new);
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
                a.tabCompletor = tabCompletor;
            }
        }
    }

    public Pair<SimpleCommandInputStream, String[]> parseInputStream(String[] input) {
        List<String> commonArgs = new ArrayList<>();
        final HashMap<Argument, String> argsMap = new HashMap<>();
        Iterator<String> iter = Arrays.stream(input).iterator();
        while (iter.hasNext()) {
            String arg = iter.next();
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
                    if (arg.startsWith("--")) {
                        // --args inputValue
                        if (iter.hasNext()) {
                            String arg2 = iter.next();

                            argsMap.put(selected, arg2);
                        }
                    } else {
                        // -f -v means boolean
                        argsMap.put(selected, "true");
                    }

                } else {
                    // 输入了一个无效参数 加入commonArgs
                    commonArgs.add(arg);
                }
            } else {
                commonArgs.add(arg);
            }
        }
        for (Argument a : args) {
            if (!argsMap.containsKey(a)) {
                if (!commonArgs.isEmpty()) {
                    argsMap.put(a, commonArgs.remove(0));
                }
            }
        }
        return new Pair<>(new SimpleCommandInputStream(args, argsMap), commonArgs.toArray(String[]::new));
    }
}
