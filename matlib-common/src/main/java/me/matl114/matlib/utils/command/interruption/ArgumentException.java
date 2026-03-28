package me.matl114.matlib.utils.command.interruption;

import me.matl114.matlib.common.lang.exceptions.RuntimeAbort;
import me.matl114.matlib.utils.command.params.api.CommandExecution;

public abstract class ArgumentException extends RuntimeAbort {
    public ArgumentException() {
        super();
    }

    public abstract void handleAbort(CommandExecution sender, InterruptionHandler command);

    // if return true , this exception is thrown when condition check not pass
    public boolean isConditionError() {
        return false;
    }
}
