package me.matl114.matlib.common.lang.exceptions;

public class DeprecatedRemoval extends RuntimeException {
    public DeprecatedRemoval() {
        super("This method is deprecated and marked for removal");
    }
}
