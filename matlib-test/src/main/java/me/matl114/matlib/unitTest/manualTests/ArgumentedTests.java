package me.matl114.matlib.unitTest.manualTests;

import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import org.bukkit.command.CommandSender;

public class ArgumentedTests implements TestCase {
    @OnlineTest(automatic = false, name = "Argumented Test")
    public void test_argument(CommandSender executor, String[] args) throws Throwable {
        String clazzName = args[0];
        var re = Class.forName(clazzName);
        Debug.logger(re);
        Debug.logger(re.getClassLoader());
    }
}
