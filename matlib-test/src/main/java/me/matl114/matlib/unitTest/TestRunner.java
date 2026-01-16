package me.matl114.matlib.unitTest;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import lombok.Getter;
import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.core.Manager;
import me.matl114.matlib.core.bukkit.schedule.ScheduleManager;
import me.matl114.matlib.utils.AddUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.TextUtils;
import me.matl114.matlib.utils.command.commandGroup.AbstractMainCommand;
import me.matl114.matlib.utils.command.commandGroup.CommandContext;
import me.matl114.matlib.utils.command.commandGroup.SubCommand;
import me.matl114.matlib.utils.command.commandGroup.TreeSubCommand;
import me.matl114.matlib.utils.command.params.ArgumentReader;
import me.matl114.matlib.utils.command.params.ArgumentInputStream;
import me.matl114.matlib.utils.command.params.SimpleCommandArgs;
import me.matl114.matlib.utils.reflect.ReflectUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class TestRunner extends AbstractMainCommand implements Manager {
    private Plugin plugin;

    @Getter
    private TestRunner manager;

    @Override
    public TestRunner init(Plugin pl, String... path) {
        this.plugin = pl;
        ScheduleManager.getManager().launchScheduled(this::runAutomaticTests, 2, false, 0);
        registerFunctional();
        this.addToRegistry();
        return this;
    }

    @Override
    public TestRunner reload() {
        deconstruct();
        return init(plugin);
    }

    @Override
    public boolean isAutoDisable() {
        return true;
    }

    @Override
    public void deconstruct() {
        unregisterFunctional();
        this.removeFromRegistry();
    }

    private void registerFunctional() {
        this.plugin.getServer().getPluginCommand("matlib").setExecutor(this);
        this.plugin.getServer().getPluginCommand("matlib").setTabCompleter(this);
    }

    private void unregisterFunctional() {
        this.plugin.getServer().getPluginCommand("matlib").setExecutor(null);
        this.plugin.getServer().getPluginCommand("matlib").setTabCompleter(null);
    }

    private final HashMap<String, Pair<TestRunnable, TestCase>> testCases = new LinkedHashMap<>();
    private final HashMap<String, Pair<BiConsumer<CommandSender, String[]>, TestCase>> manuallyExecutedCase =
            new LinkedHashMap<>();

    public TestRunner registerTestCase(TestCase testCase) {
        if (testCase instanceof TestSet set) {
            try {
                set.init();
            } catch (Throwable e) {
                Debug.logger(e, "Exception occurred while testset init");
            }
            set.getTests().forEach(this::registerTestCase);
        }
        var methods = ReflectUtils.getAllMethodsRecursively(testCase.getClass());
        for (var method : methods) {
            if (method.isSynthetic() || method.isBridge() || method.getParameterTypes().length >= 3) {
                continue;
            }
            OnlineTest testAnnotation = method.getAnnotation(OnlineTest.class);
            if (testAnnotation == null) {
                continue;
            }

            method.setAccessible(true);
            String testcaseName = testAnnotation.name().toLowerCase(Locale.ROOT).replace(" ", "_");
            if (testAnnotation.automatic() && method.getParameterTypes().length == 0) {
                testCases.put(
                        testcaseName,
                        Pair.of(
                                new TestRunnable() {
                                    @Override
                                    public boolean isAsync() {
                                        return testAnnotation.async();
                                    }

                                    @Override
                                    public void run() {
                                        long start = System.nanoTime();
                                        Debug.logger(
                                                "Start Running test case: ",
                                                testAnnotation.name(),
                                                "in",
                                                isAsync() ? "Async" : "Main",
                                                "Thread");
                                        try {
                                            method.invoke(testCase);
                                        } catch (InvocationTargetException | IllegalAccessException e) {
                                            Debug.logger(
                                                    "Error While Running test case: ",
                                                    testAnnotation.name(),
                                                    ",caused by:");
                                            e.getCause().printStackTrace();
                                        } finally {
                                            long end = System.nanoTime();
                                            Debug.logger(
                                                    "Finish test case:",
                                                    testAnnotation.name(),
                                                    ",Time cost:",
                                                    end - start,
                                                    "ns,(",
                                                    (end - start) / 1_000_000,
                                                    "ms)");
                                        }
                                    }
                                },
                                testCase));
            } else {
                if (!testAnnotation.automatic()
                        && (method.getParameterCount() == 0
                                || (method.getParameterCount() >= 1
                                        && method.getParameterTypes()[0] == CommandSender.class
                                        && (method.getParameterCount() == 1
                                                || method.getParameterTypes()[1] == String[].class)))) {
                    manuallyExecutedCase.put(
                            testcaseName,
                            Pair.of(
                                    ((sender, str) -> new TestRunnable() {
                                        @Override
                                        public boolean isAsync() {
                                            return testAnnotation.async();
                                        }

                                        @Override
                                        public void run() {
                                            long start = System.nanoTime();
                                            Debug.logger(
                                                    "Start Running test case: ",
                                                    testAnnotation.name(),
                                                    ",in",
                                                    isAsync() ? "Async" : "Main",
                                                    "Thread");
                                            try {
                                                switch (method.getParameterCount()) {
                                                    case 0:
                                                        method.invoke(testCase);
                                                        break;
                                                    case 1:
                                                        method.invoke(testCase, sender);
                                                        break;
                                                    default:
                                                        method.invoke(testCase, sender, str);
                                                        break;
                                                }
                                            } catch (InvocationTargetException | IllegalAccessException e) {
                                                throw new RuntimeException(e.getCause());
                                            } finally {
                                                long end = System.nanoTime();
                                                Debug.logger(
                                                        "Finish test case:",
                                                        testAnnotation.name(),
                                                        ",Time cost:",
                                                        end - start,
                                                        "ns,(",
                                                        (end - start) / 1_000_000,
                                                        "ms)");
                                            }
                                        }
                                    }.execute()),
                                    testCase));
                }
            }
        }
        return this;
    }

    public TestRunner unregisterTestCase(TestCase testCase) {
        testCases.entrySet().removeIf(entry -> entry.getValue().getB() == testCase);
        return this;
    }

    @Override
    public String permissionRequired() {
        return "matlib.test.op";
    }

    private TreeSubCommand command;

    {
        command = mainBuilder()
            .name("matlib")
            .build();

        command.subBuilder(
                SubCommand.taskBuilder()
                    .name("runmain")
            )
            .args(
                b -> b.name("test")
                    .defaultValue("null")
                    .tabSupplier(()-> this.testCases.keySet().stream())
            )
            .helper("执行自动测试项")
            .post(run -> run.executor(this::executeMain))
            .complete();

        command.subBuilder(
                SubCommand.taskBuilder()
                    .name("exetest")
            )
            .args(
                b -> b.name("testcase")
                    .defaultValue("null")
                    .tabSupplier(()-> this.manuallyExecutedCase.keySet().stream())
            )
            .helper("执行手动测试项")
            .post(run -> run.executor(this::executeManual))
            .complete();

        command.subBuilder(
            SubCommand.taskBuilder()
                .name("testcommandargs")
        )
            .args(
                b -> b.name("1")
            )
            .args(
                b -> b.name("2")
                    .intValue()
            )
            .args(
                b -> b.name("3")
                    .bool()
            )
            .args(
                b -> b.name("4")
                    .floatValue()
            )
            .args(
                b -> b.name("5")
                    .intValue(IntArrayList.toList(IntStream.range(0, 10)))
            )
            .helper("测试指令参数解析功能")
            .post(run -> run.executor(CommandContext.run(this::testEveryThingInCommandStream)))
            .complete();
        ;
    }
    private boolean executeMain(CommandSender sender, ArgumentInputStream args, ArgumentReader reader){
        String val = args.nextArg();
        var re = testCases.get(val);
        if (re == null) {
            TextUtils.sendMessage(sender, "&cTest case not found, run all");
            ScheduleManager.getManager().launchScheduled(TestRunner.this::runAutomaticTests, 10, false, 0);
        } else {
            ScheduleManager.getManager().launchScheduled(() -> runAutomaticTests(List.of(re.getA())), 10, false, 0);
        }
        return true;
    }

    private boolean executeManual(CommandSender sender, ArgumentInputStream args, ArgumentReader reader){
        String testcase = args.nextArg();
        var re = TestRunner.this.manuallyExecutedCase.get(testcase);
        if (re != null) {
            ScheduleManager.getManager().execute(() -> runManualTests(sender, List.of(re.getA()), reader.getRemainingArgs()));
        } else {
            if ("all".equals(testcase)) {
                ScheduleManager.getManager()
                    .execute(() -> runManualTests(
                        sender,
                        manuallyExecutedCase.values().stream()
                            .map(Pair::getA)
                            .toList(),
                        reader.getRemainingArgs()));
            } else {
                AddUtils.sendMessage(sender, "&cTest case not found");
            }
        }
        return true;
    }

    private void testEveryThingInCommandStream(ArgumentInputStream arg){
        Debug.logger(arg.nextNonnull(), arg.nextInt(), arg.nextBoolean(), arg.nextDouble(), arg.nextClampedInt(0, 10));

    }






    public interface TestRunnable extends Runnable {
        default void execute() {
            ScheduleManager.getManager().execute(this, !isAsync());
        }

        default void executeAwait() {
            Preconditions.checkArgument(!Bukkit.isPrimaryThread());
            var future = ExecutorUtils.getFutureTask(this);
            ScheduleManager.getManager().execute(future, !isAsync());
            ExecutorUtils.awaitFuture(future);
        }

        boolean isAsync();
    }

    @Getter
    private boolean warmup = true;

    public TestRunner setWarmup(boolean a) {
        warmup = a;
        return this;
    }

    public void autoTestsWarmUp(List<TestRunnable> tests) {
        if (warmup) {
            Debug.logger("Starting test warm up for the first time");
            Debug.interceptAllOutputs(() -> {
                for (int i = 0; i < 3; ++i) {
                    List<CompletableFuture> futures = new ArrayList<>();
                    for (var runnable : tests) {
                        futures.add(CompletableFuture.runAsync(runnable::executeAwait));
                    }
                    CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new))
                            .join();
                }
                return null;
            });
            warmup = false;
            Debug.logger("Warm up completed");
            ExecutorUtils.sleep(4000);
        } else {
            Debug.logger("Escape warm up before test!");
        }

        runAutomaticTests0(tests);
    }

    public void runAutomaticTests() {
        autoTestsWarmUp(this.testCases.values().stream().map(Pair::getA).toList());
    }

    public void runAutomaticTests(List<TestRunnable> tests) {
        autoTestsWarmUp(tests);
    }

    private void runAutomaticTests0(List<TestRunnable> tests) {
        Debug.logger("Starting automatic tests");
        Debug.logger("--------------------------------------------------------------------------------------");
        for (var runnable : tests) {
            runnable.executeAwait();
            Debug.logger("--------------------------------------------------------------------------------------");
        }

        Debug.logger("Finished automatic tests");
    }

    public void runManualTests(
            CommandSender player, List<BiConsumer<CommandSender, String[]>> testCases, String[] args) {
        Debug.logger("Starting manually tests");
        Debug.logger("--------------------------------------------------------------------------------------");
        for (var runnable : testCases) {
            runnable.accept(player, args);
            Debug.logger("--------------------------------------------------------------------------------------");
        }
        Debug.logger("Finished manually tests");
    }
}
