package me.matl114.matlib.utils.command.interruption;

import lombok.AllArgsConstructor;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.CommandExecution;

@AllArgsConstructor
public class DispatchFailureError extends ArgumentException {
    ArgumentReader argumentReader;

    @Override
    public void handleAbort(CommandExecution sender, InterruptionHandler command) {
        command.handleDispatchFailure(sender, argumentReader);
    }

    @Override
    public boolean isConditionError() {
        return true;
    }
}
