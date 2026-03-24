package me.matl114.matlib.utils.command.interruption;

import me.matl114.matlib.utils.command.params.api.CommandExecution;

public class LogicalError extends ArgumentException {
    String message;

    public LogicalError(String fullMessage) {
        this.message = fullMessage;
    }

    @Override
    public void handleAbort(CommandExecution sender, InterruptionHandler command) {
        command.handleLogicalError(sender, message);
    }
}
