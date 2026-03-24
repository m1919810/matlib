package me.matl114.matlib.utils.command.interruption;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.CommandExecution;

public interface InterruptionHandler {
    public void handleTypeError(
            CommandExecution sender,
            @Nullable ArgumentReader reader,
            @Nullable String argument,
            TypeError.BaseArgumentType type,
            String input);

    public void handleValueAbsent(CommandExecution sender, @Nullable ArgumentReader reader, @Nonnull String argument);

    public void handleValueParseFailure(
            CommandExecution sender, @Nullable ArgumentReader reader, @Nonnull String argument);

    public void handleValueOutOfRange(
            CommandExecution sender,
            @Nullable ArgumentReader reader,
            @Nullable String argument,
            TypeError.BaseArgumentType type,
            String range,
            @Nonnull String input);

    public void handleExecutorInvalid(CommandExecution sender, boolean shouldConsole);

    public void handlePermissionDenied(CommandExecution sender, String permission, ArgumentReader reader);

    public void handleLogicalError(CommandExecution sender, String fullMessage);

    public void handleDispatchFailure(CommandExecution sender, ArgumentReader reader);
}
