package me.matl114.matlib.utils.command.commandGroup;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import lombok.Getter;
import me.matl114.matlib.utils.AddUtils;
import me.matl114.matlib.utils.command.interruption.*;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Logger;

/**
 * Abstract base class for main commands that can contain multiple sub-commands.
 * This class provides a complete command framework with sub-command management,
 * permission handling, argument parsing, and error handling capabilities.
 * 
 * <p>AbstractMainCommand implements both ComplexCommandExecutor and InterruptionHandler,
 * providing a comprehensive command system that can handle complex command structures
 * with proper error handling and user feedback.</p>
 * 
 * <p>Key features include:</p>
 * <ul>
 *   <li>Sub-command registration and management</li>
 *   <li>Permission checking at both main and sub-command levels</li>
 *   <li>Automatic help generation</li>
 *   <li>Tab completion for sub-commands and arguments</li>
 *   <li>Error handling with user-friendly messages</li>
 *   <li>Plugin registration and unregistration</li>
 * </ul>
 * 
 * <p>To use this class, extend it and implement the abstract methods.
 * The main command should be defined as a field named "mainCommand" in the subclass.</p>
 */
public abstract class AbstractMainCommand implements ComplexCommandExecutor, InterruptionHandler {
    
    /** Collection of registered sub-commands, maintaining insertion order */
    @Getter
    private LinkedHashSet<SubCommand> subCommands = new LinkedHashSet<>();
    
    /** Internal reference to the main command */
    private SubCommand mainInternal;
    
    /** Whether this command has been registered with the plugin */
    private boolean registered = false;
    
    /** Logger for debug information */
    private Logger Debug;
    
    /** The plugin that owns this command */
    @Getter
    private Plugin plugin;
    
    /**
     * Sends a message to the command sender with color code translation.
     * 
     * @param sender The command sender to send the message to
     * @param message The message to send (supports & color codes)
     */
    protected void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }
    
    /**
     * Registers this command with the specified plugin.
     * This method sets up the command executor and tab completer with Bukkit.
     * 
     * @param <T> The type of this command (for method chaining)
     * @param plugin The plugin to register this command with
     * @return This command instance for method chaining
     * @throws IllegalArgumentException if the command has already been registered
     */
    public <T extends AbstractMainCommand> T registerCommand(Plugin plugin){
        Preconditions.checkArgument(!registered, "Command have already been registered!");
        this.plugin = plugin;
        this.Debug = plugin.getLogger();
        plugin.getServer().getPluginCommand(getMainName()).setExecutor(this);
        plugin.getServer().getPluginCommand(getMainName()).setTabCompleter(this);
        this.registered=true;
        return (T)this;
    }
    
    /**
     * Unregisters this command from the plugin.
     * This method removes the command executor and tab completer from Bukkit.
     * 
     * @param <T> The type of this command (for method chaining)
     * @return This command instance for method chaining
     * @throws IllegalArgumentException if the command hasn't been registered
     */
    public <T extends AbstractMainCommand> T unregisterCommand(){
        Preconditions.checkArgument(registered, "Command functional havem't been unregistered!");
        plugin.getServer().getPluginCommand(getMainName()).setExecutor(null);
        plugin.getServer().getPluginCommand(getMainName()).setTabCompleter(null);
        this.registered=false;
        return (T)this;
    }
    
    /**
     * Gets a sub-command by name (case-insensitive).
     * 
     * @param name The name of the sub-command to find
     * @return The sub-command with the specified name, or null if not found
     */
    public SubCommand getSubCommand(String name) {
        for(SubCommand command:subCommands){
            if(command.getName().equalsIgnoreCase(name)){
                return command;
            }
        }return null;
    }
    
    /**
     * Gets a list of visible sub-command names for display purposes.
     * Hidden sub-commands are filtered out.
     * 
     * @return A list of visible sub-command names
     */
    public List<String> getDisplayedSubCommand(){
        return this.subCommands.stream().filter(SubCommand::isVisiable).map(SubCommand::getName).toList();
    }
    
    /**
     * Generates a main command with the specified name.
     * The main command uses a special "_operation" argument for sub-command selection.
     * 
     * @param name The name of the main command
     * @return A SubCommand instance configured as the main command
     */
    protected SubCommand genMainCommand(String name){
        return new SubCommand(name,genArgument("_operation"),"")
                .setTabCompletor("_operation",this::getDisplayedSubCommand);
    }
    
    /**
     * Registers a sub-command with this main command.
     * 
     * @param command The sub-command to register
     */
    @Override
    public void registerSub(SubCommand command) {
        this.subCommands.add(command);
    }
    
    /**
     * Gets the internal main command reference.
     * This method uses reflection to find the "mainCommand" field in the subclass.
     * 
     * @return The main command SubCommand instance
     */
    private SubCommand getMainInternal() {
        if(mainInternal == null){
            try{
                Field field=this.getClass().getDeclaredField("mainCommand");
                field.setAccessible(true);
                mainInternal=(SubCommand)field.get(this);
            }catch (Throwable e){
                Debug.info("Error in "+this.getClass().getName()+": main Command Not Found");
                e.printStackTrace();
            }
        }
        return mainInternal;
    }
    
    /**
     * Gets the main command for this command group.
     * 
     * @return The main command SubCommand instance
     */
    public SubCommand getMainCommand() {
        return getMainInternal();
    }
    
    /**
     * Gets the name of the main command.
     * 
     * @return The name of the main command
     */
    public String getMainName(){
        return getMainInternal().getName();
    }
    
    /**
     * Handles command execution for the main command.
     * This method routes commands to appropriate sub-commands based on the first argument.
     * 
     * <p>The execution flow:</p>
     * <ol>
     *   <li>Check main command permission</li>
     *   <li>Parse the first argument as sub-command name</li>
     *   <li>Find and validate the sub-command</li>
     *   <li>Check sub-command permission</li>
     *   <li>Execute the sub-command with remaining arguments</li>
     *   <li>Handle any ArgumentException errors</li>
     *   <li>Show help if no valid sub-command is found</li>
     * </ol>
     * 
     * @param var1 The command sender
     * @param var2 The command being executed
     * @param var3 The command alias
     * @param var4 The command arguments
     * @return true if the command was executed successfully, false otherwise
     */
    public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4){
        if(permissionRequired()==null|| var1.hasPermission(permissionRequired())){
            if(var4.length>=1){
                SubCommand command=getSubCommand(var4[0]);
                if(command != null ){
                    //add permission check
                    if( command.hasPermission(var1)){
                        String[] elseArg= Arrays.copyOfRange(var4,1,var4.length);
                        try{
                            return command.getExecutor().onCommand(var1,var2,var3,elseArg);
                        }catch (ArgumentException e){
                            e.handleAbort(var1, this);
                            return false;
                        }
                    }else {
                        noPermission(var1);
                        return false;
                    }
                }
            }
            showHelpCommand(var1);
        }else{
            noPermission(var1);
        }
        return false;
    }

    /**
     * Handles type errors during argument parsing.
     * Displays a user-friendly error message in Chinese.
     * 
     * @param sender The command sender to send the error to
     * @param argument The argument name that caused the error (may be null)
     * @param type The expected argument type
     * @param input The invalid input that was provided
     */
    public void handleTypeError(CommandSender sender,@Nullable String argument, TypeError.BaseArgumentType type, String input){
        if(argument != null){
            sendMessage(sender, "&c类型错误:参数\""+ argument+"\"需要输入一个"+type.getDisplayNameZHCN()+",但是输入了:" + input);
        }else {
            sendMessage(sender, "&c类型错误: 需要输入一个" + type.getDisplayNameZHCN()+",但是输入了:" + input);
        }
    }
    
    /**
     * Handles missing argument values.
     * Displays a user-friendly error message in Chinese.
     * 
     * @param sender The command sender to send the error to
     * @param argument The argument name that is missing a value
     */
    public void handleValueAbsent(CommandSender sender,@Nonnull String argument){
        sendMessage(sender, "&c值缺失: 并未输入参数\"" + argument + "\"的值");
    }

    /**
     * Handles argument values that are out of the expected range.
     * Displays a user-friendly error message in Chinese.
     * 
     * @param sender The command sender to send the error to
     * @param argument The argument name that caused the error (may be null)
     * @param type The argument type
     * @param from The minimum allowed value
     * @param to The maximum allowed value (exclusive)
     * @param input The invalid input that was provided
     */
    @Override
    public void handleValueOutOfRange(CommandSender sender, @Nullable String argument, TypeError.BaseArgumentType type, String from, String to,@Nonnull String input) {
        if(argument != null){
            sendMessage(sender, "&c值不在范围内: 参数 %s 输入了类型: %s, 需要在范围 %s ~ %s(exclude) 之间, 但是输入了%s".formatted(argument, type.getDisplayNameZHCN(), from, to, input));
        }else {
            sendMessage(sender, "&c值不在范围内: 输入了类型: %s, 需要在范围 %s ~ %s(exclude) 之间, 但是输入了 %s".formatted( type.getDisplayNameZHCN(), from, to, input));
        }
    }
    
    /**
     * Handles invalid executor errors (console vs player).
     * Displays a user-friendly error message in Chinese.
     * 
     * @param sender The command sender to send the error to
     * @param shouldConsole Whether the command should be executed by console
     */
    @Override
    public void handleExecutorInvalid(CommandSender sender, boolean shouldConsole){
        if(shouldConsole){
            sendMessage(sender, "&c错误! 该指令只能在控制台执行");
        }else {
            sendMessage(sender,"&c该指令只能在游戏内执行!");
        }
    }

    public void handlePermissionDenied(CommandSender sender, String permission, @Nullable String commandNodeName){
        if(commandNodeName == null){
            noPermission(sender);
        }else {
            sendMessage(sender, "&c你没有权限使用: " + commandNodeName);
        }
    }

    /**
     * Handles logical errors during command execution.
     * Displays a user-friendly error message in Chinese.
     * 
     * @param sender The command sender to send the error to
     * @param fullMessage The full error message
     */
    public void handleLogicalError(CommandSender sender, String fullMessage){
        sendMessage(sender, "&c执行该指令时出现逻辑错误: "+ fullMessage);
    }

    /**
     * Sends a permission denied message to the command sender.
     * 
     * @param var1 The command sender to send the message to
     */
    public void noPermission(CommandSender var1){
        sendMessage(var1,"&c你没有权限使用该指令!");
    }
    
    /**
     * Returns the permission required to use this main command.
     * Override this method to specify the required permission.
     * Return null for no permission requirement.
     * 
     * @return The permission string, or null if no permission is required
     */
    public abstract String permissionRequired();

    /**
     * Shows the help command with all visible sub-commands.
     * Displays the main command usage and help text for each sub-command.
     * 
     * @param sender The command sender to show help to
     */
    public void showHelpCommand(CommandSender sender){
        sendMessage(sender,"&a/%s 全部指令大全".formatted(getMainName()));
        for(SubCommand cmd:subCommands){
            for (String help:cmd.getHelp()){
                sendMessage(sender,"&a"+help);
            }
        }
    }
    
    /**
     * Handles tab completion for the main command and its sub-commands.
     * This method provides intelligent tab completion based on the current input.
     * 
     * <p>The tab completion flow:</p>
     * <ol>
     *   <li>Check main command permission</li>
     *   <li>Parse input to determine current context</li>
     *   <li>If no sub-command is selected, show available sub-commands</li>
     *   <li>If a sub-command is selected, delegate to that sub-command</li>
     *   <li>Handle any exceptions gracefully</li>
     * </ol>
     * 
     * @param var1 The command sender requesting tab completion
     * @param var2 The command being executed
     * @param var3 The command alias
     * @param var4 The command arguments
     * @return A list of tab completion suggestions
     */
    public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4){
        //add permission check
        try{
            if(permissionRequired()==null|| var1.hasPermission(permissionRequired())){
                var re=getMainCommand().parseInput(var4);
                if(re.getB().length==0){
                    List<String> provider=re.getA().getTabComplete();
                    return provider==null?new ArrayList<>():provider;
                }else{
                    SubCommand subCommand= getSubCommand(re.getA().nextArg());
                    if(subCommand!=null && subCommand.hasPermission(var1)){
                        String[] elseArg=re.getB();
                        return subCommand.onTabComplete(var1,var2,var3,elseArg);
                    }
                }
            }
        }catch (Throwable e){
        }
        return new ArrayList<>();
    }
    
    /**
     * Safely casts a CommandSender to a Player.
     * Throws InvalidExecutorError if the sender is not a Player.
     * 
     * @param sender The command sender to cast
     * @return The Player instance
     * @throws InvalidExecutorError if the sender is not a Player
     */
    @Nonnull
    public Player player(CommandSender sender){
        if(sender instanceof Player player){
            return player;
        }else {
            throw new InvalidExecutorError(false);
        }
    }

    public void permissionDenied(String permission,@Nullable String commandnode){
        throw new PermissionDenyError(permission, Optional.ofNullable(commandnode));
    }


    public void checkPermission(CommandSender sender, String permission){
        if(sender.hasPermission(permission)){
            return;
        }else {
            throw new PermissionDenyError(permission, Optional.empty());
        }
    }

    /**
     * Generates a SimpleCommandArgs instance with the specified argument names.
     * 
     * @param args The argument names
     * @return A SimpleCommandArgs instance configured with the specified arguments
     */
    public static SimpleCommandArgs genArgument(String... args){
        return new SimpleCommandArgs(args);
    }
    
    /**
     * Creates a supplier that provides common number values for tab completion.
     * 
     * @return A supplier that returns a list of common number values
     */
    public static Supplier<List<String>> numberSupplier(){
        return ()->List.of("0","1","16","64","114514","2147483647");
    }
    
    /**
     * Creates a supplier that provides common float values for tab completion.
     * 
     * @return A supplier that returns a list of common float values
     */
    public static Supplier<List<String>> floatSupplier(){
        return ()->List.of("0.0","1.0", "2.0", "3.0" ,"3.14159" ,"1.57079" ,"6.283185");
    }
    
    /**
     * Creates a supplier that provides the list of visible sub-command names.
     * 
     * @return A supplier that returns the list of visible sub-command names
     */
    public Supplier<List<String>> subCommandsSupplier(){
        return this::getDisplayedSubCommand;
    }
    
    /**
     * Creates a supplier that provides online player names for tab completion.
     * 
     * @return A supplier that returns a list of online player names
     */
    public static Supplier<List<String>> playerNameSupplier(){
        return ()-> Bukkit.getOnlinePlayers().stream().map(Player::getName).toList();
    }

    public static void checkArgument(boolean argument, String... msg){
        if(!argument){
            throw new LogicalError(String.join(" ", msg));
        }
    }
    public static void checkNonnull(Object object, String... msg){
        if(object  == null){
            throw new LogicalError(String.join(" ", msg));
        }
    }
}