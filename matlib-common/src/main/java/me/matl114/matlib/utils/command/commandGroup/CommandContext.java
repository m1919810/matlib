package me.matl114.matlib.utils.command.commandGroup;

import java.util.List;
import java.util.function.*;
import me.matl114.matlib.utils.command.params.ArgumentInputStream;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.CommandExecution;
import org.apache.commons.lang3.function.TriFunction;
import org.bukkit.command.CommandSender;

public interface CommandContext {
    public boolean execute(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader);

    default List<String> supplyTab(CommandExecution var1, ArgumentInputStream streamArgs, ArgumentReader argsReader) {
        return List.of();
    }

    public static CommandContext run(
            TriFunction<CommandSender, ArgumentInputStream, ArgumentReader, Boolean> delegate) {
        return ((var1, streamArgs, argsReader) -> {
            if (var1.getExecutor() != null) {
                return delegate.apply(var1.getExecutor(), streamArgs, argsReader);
            }
            return false;
        });
    }

    public static CommandContext run(Runnable task) {
        return (var1, streamArgs, argsReader) -> {
            task.run();
            return true;
        };
    }

    public static CommandContext run(BooleanSupplier task) {
        return ((var1, streamArgs, argsReader) -> {
            return task.getAsBoolean();
        });
    }

    public static CommandContext run(Consumer<ArgumentInputStream> var) {
        return ((var1, streamArgs, argsReader) -> {
            var.accept(streamArgs);
            return true;
        });
    }

    public static CommandContext run(Predicate<ArgumentInputStream> var) {
        return (var1, streamArgs, argsReader) -> {
            return var.test(streamArgs);
        };
    }

    public static CommandContext run(BiConsumer<CommandSender, ArgumentInputStream> var) {
        return ((var1, streamArgs, argsReader) -> {
            if (var1.getExecutor() != null) {
                var.accept(var1.getExecutor(), streamArgs);
                return true;
            }
            return false;
        });
    }

    public static CommandContext run(BiPredicate<CommandSender, ArgumentInputStream> var) {
        return (var1, streamArgs, argsReader) -> {
            if (var1.getExecutor() != null) {
                return var.test(var1.getExecutor(), streamArgs);
            }
            return false;
        };
    }
}
