package me.matl114.matlib.utils.command.params.impl;

import lombok.Getter;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.api.ArgumentType;
import me.matl114.matlib.utils.command.params.api.InputArgument;

public abstract class AbstractArgumentResult<T> implements InputArgument<T> {
    public final ArgumentType<T> type;
    @Getter
    public final ArgumentReader reader;
    @Getter
    int startIndex;
    @Getter
    int endIndex;
    boolean isDefault;
    final T result;
    public boolean parseSuccess = true;
    // the parsed range is from startIndex  to endIndex
    public AbstractArgumentResult(T result, ArgumentType<T> type, ArgumentReader reader, int startIndex) {
        this.type = type;
        this.reader = reader;
        this.startIndex = startIndex;
        this.endIndex = reader.cursor();
        this.isDefault = this.startIndex == this.endIndex;
        this.result = result;
    }

    @Override
    public ArgumentType<T> getType() {
        return type;
    }

    @Override
    public T result() {
        return result;
    }

    public String[] getParsedArgument() {
        return this.reader.getArgsInRange(this.startIndex, this.endIndex);
    }

    public final String tabbingString() {
        if (this.startIndex < this.endIndex) {
            return this.reader.getArgsAt(this.endIndex - 1);
        } else if(this.startIndex == this.endIndex){
            return this.reader.getLength() > this.endIndex ? this.reader.getArgsAt(this.endIndex) : null;
        }else{
            return null;
        }
    }

    @Override
    public boolean isParseSuccess() {
        return parseSuccess;
    }
}
