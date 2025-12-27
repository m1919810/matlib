package me.matl114.matlib.common.lang.exceptions;

public class CompileError extends RuntimeException {
    public CompileError(CompilePeriod period, int i, String message) {
        super("Compile Error during " + period.name() + " period at index " + i + ":" + message);
    }

    public enum CompilePeriod {
        LEXICAL,
        SYN_ANALYSIS,
        SDT_TRANSLATE,
        IR_BUILDING
    }
}
