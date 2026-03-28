package me.matl114.matlib.utils.command.params.impl;

import java.util.List;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.ArgumentType;
import me.matl114.matlib.utils.command.params.api.CommandExecution;
import me.matl114.matlib.utils.command.params.api.InputArgument;
import org.jetbrains.annotations.Nullable;

public class OptionalArgumentType<T> extends DelegateArgumentType<T> implements ArgumentType<T> {
    public OptionalArgumentType(ArgumentType<T> delegate) {
        super(delegate);
    }

    @Nullable @Override
    public InputArgument<T> consume(CommandExecution sender, List<InputArgument<?>> args, ArgumentReader reader) {
        int cursor = reader.cursor();
        var result = super.consume(sender, args, reader);
        if (reader.cursor() == cursor) {
            // did not consume anything
            // we have to check if it is the last argument
        }
        return result;
    }
}
