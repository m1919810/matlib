package me.matl114.matlib.utils.command.params.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.stream.Stream;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.ArgumentType;
import me.matl114.matlib.utils.command.params.api.CommandExecution;
import me.matl114.matlib.utils.command.params.api.InputArgument;
import org.jetbrains.annotations.Nullable;

public class DispatchArgumentType<T> implements ArgumentType<T> {
    String name;

    public DispatchArgumentType(String name) {
        this.name = name;
    }

    List<Pair<BiPredicate<CommandExecution, List<InputArgument<?>>>, ArgumentType<? extends T>>> dispatchMap =
            new ArrayList<>();

    public DispatchArgumentType<T> registerDispatcher(
            BiPredicate<CommandExecution, List<InputArgument<?>>> predicate, ArgumentType<? extends T> type) {
        dispatchMap.add(Pair.of(predicate, type));
        return this;
    }

    @Override
    public String getArgsName() {
        return this.name;
    }

    @Override
    public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
        for (var re : dispatchMap) {
            if (re.getA().test(sender, args)) {
                return re.getB().getTab(sender, args);
            }
        }
        return Stream.empty();
    }

    @Nullable @Override
    public InputArgument<T> consume(CommandExecution sender, List<InputArgument<?>> args, ArgumentReader reader) {
        for (var re : dispatchMap) {
            if (re.getA().test(sender, args)) {
                return (InputArgument<T>) re.getB().consume(sender, args, reader);
            }
        }
        return new EmptyArgumentResult<>(this, reader);
    }
}
