package me.matl114.matlib.utils.command.interruption;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import org.bukkit.command.CommandSender;

@Getter
@AllArgsConstructor
@Note("interrupt when input type not match")
public class TypeError extends ArgumentException {
    String argument;
    BaseArgumentType typeName;
    String input;

    public TypeError(SimpleCommandArgs.Argument arg, BaseArgumentType typeName, String input) {
        this(arg == null ? null : arg.getArgsName(), typeName, input);
    }

    @Override
    public void handleAbort(CommandSender sender, InterruptionHandler command) {
        command.handleTypeError(sender, argument, typeName, input);
    }

    @Getter
    public static enum BaseArgumentType {
        INT("整形", "Integer"),
        FLOAT("浮点型", "Float"),
        BOOLEAN("布尔型", "Boolean"),
        STRING("字符串", "String"),
        ENUM("枚举型", "Enum");
        String displayNameZHCN;
        String displayNameENUS;

        BaseArgumentType(String display, String display2) {
            this.displayNameZHCN = display;
            this.displayNameENUS = display2;
        }
    }
}
