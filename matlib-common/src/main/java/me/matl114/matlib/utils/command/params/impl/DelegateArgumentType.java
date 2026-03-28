package me.matl114.matlib.utils.command.params.impl;

import java.util.List;
import java.util.stream.Stream;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.ArgumentType;
import me.matl114.matlib.utils.command.params.api.CommandExecution;
import me.matl114.matlib.utils.command.params.api.InputArgument;
import org.jetbrains.annotations.Nullable;

public class DelegateArgumentType<T> implements ArgumentType<T> {
    public ArgumentType<T> delegate;

    public DelegateArgumentType(ArgumentType<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getArgsName() {
        return this.delegate.getArgsName();
    }

    @Override
    public Stream<String> getTab(CommandExecution sender, List<InputArgument<?>> args) {
        return this.delegate.getTab(sender, args);
    }

    @Nullable @Override
    public InputArgument<T> consume(CommandExecution sender, List<InputArgument<?>> args, ArgumentReader reader) {
        return this.delegate.consume(sender, args, reader);
    }
}
