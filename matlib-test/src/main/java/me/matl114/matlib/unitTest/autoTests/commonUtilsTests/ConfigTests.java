package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import java.io.InputStream;
import java.util.Collection;
import me.matl114.matlib.algorithms.algorithm.FileUtils;
import me.matl114.matlib.algorithms.algorithm.IterUtils;
import me.matl114.matlib.unitTest.MatlibTest;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.ResourceUtils;
import me.matl114.matlib.utils.config.Config;
import me.matl114.matlib.utils.config.YamlConfig;

public class ConfigTests implements TestCase {
    @OnlineTest(name = "test new config structure")
    public void test_config() throws Throwable {
        String filePath = "tests/config_system/language";
        Config config;
        io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config config1;
        try (InputStream file = FileUtils.readResource(filePath + ".yml")) {
            config = new YamlConfig(file);
            config1 = new io.github.thebusybiscuit.slimefun4.libraries.dough.config.Config(
                    null, ResourceUtils.loadInternalConfig(MatlibTest.class, filePath));
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        Debug.logger(config);
        Collection<String> keys = config.getPaths();
        Debug.logger("key set size", keys.size());
        for (var keyEntry : IterUtils.fastEnumerate(keys)) {
            try {
                AssertEq(config.getLeaf(keyEntry.getValue()).get(), config1.getValue(keyEntry.getValue()));

            } catch (Throwable e) {
                Debug.logger(
                        "Error:",
                        keyEntry.getIndex(),
                        keyEntry.getValue(),
                        config.getLeaf(keyEntry.getValue()).get(),
                        config1.getValue(keyEntry.getValue()));
                return;
            }
        }
    }
}
