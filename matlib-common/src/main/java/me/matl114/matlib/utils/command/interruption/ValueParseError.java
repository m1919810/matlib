package me.matl114.matlib.utils.command.interruption;

import lombok.AllArgsConstructor;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.CommandExecution;

@AllArgsConstructor
public class ValueParseError extends ArgumentException {
    public String argumentName;
    public ArgumentReader argumentReader;

    public ValueParseError(ArgumentReader argumentReader, String argumentName) {
        this.argumentName = argumentName;
        this.argumentReader = argumentReader;
    }

    @Override
    public void handleAbort(CommandExecution sender, InterruptionHandler command) {
        command.handleValueParseFailure(sender, argumentReader, argumentName);
    }
}
