package me.matl114.matlib.utils.command.commandGroup;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.command.CommandUtils;
import me.matl114.matlib.utils.command.params.CommandArgumentMap;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import me.matl114.matlib.utils.command.params.SimpleCommandInputStream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

/**
 * Represents a sub-command within a command group system.
 * This class provides functionality for creating, configuring, and executing sub-commands
 * with argument parsing, tab completion, and permission checking capabilities.
 *
 * <p>SubCommand instances can be configured with various argument types (int, float, enum)
 * and can be hidden from help displays. They support both custom executors and
 * built-in argument parsing functionality.</p>
 *
 * <p>Each SubCommand has a name, help text, argument template, and optional executor.
 * The class implements TabExecutor to provide tab completion functionality.</p>
 */
public class SubCommand implements CustomTabExecutor {

    /**
     * Handles tab completion for this sub-command.
     * If a custom executor is set, delegates to that executor. Otherwise,
     * uses the argument template to provide tab completion suggestions.
     *
     * @param commandSender The sender requesting tab completion
     * @param command The command being executed
     * @param s The command alias
     * @param elseArg The arguments provided so far
     * @return A list of tab completion suggestions, or empty list if none available
     */
    @Nullable @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] elseArg) {
        if (executor != this) {
            return executor.onTabComplete(commandSender, command, s, elseArg);
        } else {
            var tab = this.parseInput(elseArg).getA();
            if (tab != null) {
                return tab.getTabComplete();
            } else {
                return List.of();
            }
        }
    }

    /**
     * Interface for registering sub-commands with a parent command.
     * Implemented by classes that can contain and manage sub-commands.
     */
    public interface SubCommandCaller {
        /**
         * Registers a sub-command with this caller.
         *
         * @param command The sub-command to register
         */
        public void registerSub(SubCommand command);
    }

    /** Help text lines for this sub-command */
    @Getter
    String[] help;

    /** Argument template defining the expected parameters */
    SimpleCommandArgs template;

    /** Name of this sub-command */
    @Getter
    String name;

    /** Executor responsible for handling command execution */
    @Getter
    TabExecutor executor = this;

    /** Whether this sub-command should be hidden from help displays */
    boolean hide = false;

    /**
     * Checks if the sender has permission to use this sub-command.
     * By default, returns true (no permission required).
     * Override this method to implement custom permission logic.
     *
     * @param sender The command sender to check
     * @return true if the sender has permission, false otherwise
     */
    public boolean hasPermission(CommandSender sender) {
        return true;
    }

    /**
     * Handles command execution for this sub-command.
     * By default, returns true (success). Override this method to implement
     * custom command logic.
     *
     * @param var1 The command sender
     * @param var2 The command being executed
     * @param var3 The command alias
     * @param var4 The command arguments
     * @return true if the command was executed successfully, false otherwise
     */
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
        return true;
    }

    /**
     * Creates a new SubCommand with the specified name, argument template, and help text.
     *
     * @param name The name of the sub-command
     * @param argsTemplate The argument template defining expected parameters
     * @param help Help text lines for this sub-command
     */
    public SubCommand(String name, SimpleCommandArgs argsTemplate, String... help) {
        this.name = name;
        this.template = argsTemplate;
        this.help = help;
    }

    /**
     * Creates a new SubCommand with the specified name, argument template, and help text.
     *
     * @param name The name of the sub-command
     * @param argsTemplate The argument template defining expected parameters
     * @param help Help text lines for this sub-command
     */
    public SubCommand(String name, SimpleCommandArgs argsTemplate, List<String> help) {
        this(name, argsTemplate, help.toArray(String[]::new));
    }

    /**
     * Hides this sub-command from help displays.
     *
     * @return This SubCommand instance for method chaining
     */
    public SubCommand hide() {
        this.hide = true;
        return this;
    }

    /**
     * Checks if this sub-command is visible in help displays.
     *
     * @return true if the sub-command is visible, false if hidden
     */
    public boolean isVisiable() {
        return !this.hide;
    }

    /**
     * Registers this sub-command with the specified caller.
     *
     * @param caller The SubCommandCaller to register with
     * @return This SubCommand instance for method chaining
     */
    public SubCommand register(SubCommandCaller caller) {
        caller.registerSub(this);
        return this;
    }

    /**
     * Parses the input arguments according to the argument template.
     * Returns a pair containing the parsed input stream and remaining arguments.
     *
     * @param args The arguments to parse
     * @return A pair containing the parsed input stream and remaining arguments
     */
    @Nonnull
    public Pair<SimpleCommandInputStream, String[]> parseInput(String[] args) {
        return template.parseInputStream(args);
    }

    /**
     * Parses arguments and creates a CommandArgumentMap for easy access to argument values.
     *
     * @param args The arguments to parse
     * @return A CommandArgumentMap containing the parsed arguments
     */
    public CommandArgumentMap parseArgument(String[] args) {
        return new CommandArgumentMap(CommandUtils.parseArguments(args, this.template.getArgs()));
    }

    /**
     * Sets a default value for the specified argument.
     *
     * @param arg The argument name
     * @param val The default value
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setDefault(String arg, String val) {
        this.template.setDefault(arg, val);
        return this;
    }

    /**
     * Configures an argument as an integer with default value 0.
     * Also sets up tab completion for integer values.
     *
     * @param arg The argument name
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setInt(String arg) {
        setDefault(arg, "0");
        setTabCompletor(arg, AbstractMainCommand.numberSupplier());
        return this;
    }

    /**
     * Configures an argument as an integer with the specified default value.
     * Also sets up tab completion for integer values.
     *
     * @param arg The argument name
     * @param val The default integer value
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setInt(String arg, int val) {
        setDefault(arg, String.valueOf(val));
        setTabCompletor(arg, AbstractMainCommand.numberSupplier());
        return this;
    }

    /**
     * Configures an argument as a float with default value 0.0.
     * Also sets up tab completion for float values.
     *
     * @param arg The argument name
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setFloat(String arg) {
        setDefault(arg, "0.0");
        setTabCompletor(arg, AbstractMainCommand.floatSupplier());
        return this;
    }

    /**
     * Configures an argument as a float with the specified default value.
     * Also sets up tab completion for float values.
     *
     * @param arg The argument name
     * @param val The default float value
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setFloat(String arg, float val) {
        setDefault(arg, String.valueOf(val));
        setTabCompletor(arg, AbstractMainCommand.floatSupplier());
        return this;
    }

    /**
     * Configures an argument as an enum with the specified values.
     * Sets up tab completion for the enum values.
     *
     * @param arg The argument name
     * @param enumValues The collection of valid enum values
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setEnum(String arg, Collection<String> enumValues) {
        List<String> enums = enumValues.stream().toList();
        setTabCompletor(arg, () -> enums);
        return this;
    }

    /**
     * Configures an argument as an enum with the specified default value and enum values.
     * Sets up tab completion for the enum values.
     *
     * @param arg The argument name
     * @param defaultValue The default enum value
     * @param enumValues The valid enum values
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setEnum(String arg, String defaultValue, String... enumValues) {
        return setEnum(arg, defaultValue, List.of(enumValues));
    }

    /**
     * Configures an argument as an enum with the specified default value and enum values.
     * Sets up tab completion for the enum values.
     *
     * @param arg The argument name
     * @param defaultValue The default enum value
     * @param enumValues The collection of valid enum values
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setEnum(String arg, String defaultValue, Collection<String> enumValues) {
        List<String> enums = enumValues.stream().toList();
        setDefault(arg, defaultValue);
        setTabCompletor(arg, () -> enums);
        return this;
    }

    /**
     * Sets a custom tab completer for the specified argument.
     *
     * @param arg The argument name
     * @param completions A supplier that provides tab completion suggestions
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setTabCompletor(String arg, Supplier<List<String>> completions) {
        this.template.setTabCompletor(arg, completions);
        return this;
    }

    /**
     * Sets a custom executor for this sub-command.
     * The executor will handle both command execution and tab completion.
     *
     * @param executor The TabExecutor to use for this sub-command
     * @return This SubCommand instance for method chaining
     */
    public SubCommand setCommandExecutor(TabExecutor executor) {
        this.executor = executor;
        return this;
    }
}
