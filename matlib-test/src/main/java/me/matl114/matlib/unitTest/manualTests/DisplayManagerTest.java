package me.matl114.matlib.unitTest.manualTests;

import me.matl114.matlib.unitTest.TestCase;

public class DisplayManagerTest implements TestCase {
    //    @OnlineTest(automatic = false, name = "Display Manager Terminal Test")
    //    public void test_displayManagerGen(CommandSender executor) throws Throwable {
    //        if (!(executor instanceof Player p)) {
    //            Debug.logger("Not a player,stopping test");
    //            return;
    //        }
    //        DisplayManager manager = new SimpleDisplayManager();
    //        EntityBuilder<Display> commonAttribute = EntityBuilder.createVirtual(Display.class)
    //                .withGlow(true)
    //                .with(display -> display.setGlowColorOverride(Color.PURPLE));
    //        manager.addDisplayPart(BluePrinted.DisplayPart.builder()
    //                        .transformation(TransformationUtils.builder()
    //                                .scale(0.2f, 2.0f, 0.2f)
    //                                .translate(2.0f, 0.0f, 2.0f)
    //                                .build())
    //                        .context(commonAttribute.copyTo(DisplayEntityBuilder.ofBlock(Material.GOLD_BLOCK)))
    //                        .partIdentifier("part1")
    //                        .build())
    //                .addDisplayPart(BluePrinted.DisplayPart.builder()
    //                        .transformation(TransformationUtils.builder()
    //                                .scale(0.2f, 2.0f, 0.2f)
    //                                .postRotation(0, 1, 0, 180)
    //                                .translate(-2.0f, 0.0f, -2.0f)
    //                                .build())
    //                        .context(commonAttribute.copyTo(DisplayEntityBuilder.ofBlock(Material.DIAMOND_BLOCK)))
    //                        .partIdentifier("part2")
    //                        .build());
    //        createTerminal(p, manager);
    //    }
    //
    //    @OnlineTest(automatic = false, name = "Complex Display Part Test")
    //    public void test_displayGearWheel(CommandSender executor) throws Throwable {
    //        if (!(executor instanceof Player p)) {
    //            Debug.logger("Not a player,stopping test");
    //            return;
    //        }
    //        DisplayManager manager = new SimpleDisplayManager();
    //        EntityBuilder<Display> commonAttribute = EntityBuilder.createVirtual(Display.class)
    //                .withGlow(true)
    //                .with(display -> display.setGlowColorOverride(Color.PURPLE));
    //        var p1 = TransformationUtils.buildCubeAtCenter(0.4f, 1.0f, 0.4f);
    //        manager.addDisplayPart(BluePrinted.DisplayPart.builder()
    //                .context(commonAttribute.copyTo(DisplayEntityBuilder.ofBlock(Material.STONE)))
    //                .transformation(p1.build())
    //                .partIdentifier("part1")
    //                .build());
    //        var p2 = TransformationUtils.buildCubeAtCenter(0.6f, 0.4f, 1.0f).build();
    //        Debug.logger(p2);
    //        var rotate90 = TransformationUtils.linearBuilder().A(0, 1, 0, 45).build();
    //        Debug.logger(rotate90);
    //        var p3 = rotate90.transformOrigin(p2);
    //        Debug.logger(p3);
    //        var p4 = rotate90.transformOrigin(p3);
    //        var p5 = rotate90.transformOrigin(p4);
    //        manager.addDisplayPart(BluePrinted.DisplayPart.builder()
    //
    // .context(commonAttribute.copyTo(DisplayEntityBuilder.ofBlock(Material.STRIPPED_SPRUCE_LOG)))
    //                        .transformation(p2)
    //                        .partIdentifier("sub1")
    //                        .build())
    //                .addDisplayPart(BluePrinted.DisplayPart.builder()
    //
    // .context(commonAttribute.copyTo(DisplayEntityBuilder.ofBlock(Material.STRIPPED_SPRUCE_LOG)))
    //                        .transformation(p3)
    //                        .partIdentifier("sub2")
    //                        .build())
    //                .addDisplayPart(BluePrinted.DisplayPart.builder()
    //
    // .context(commonAttribute.copyTo(DisplayEntityBuilder.ofBlock(Material.STRIPPED_SPRUCE_LOG)))
    //                        .transformation(p4)
    //                        .partIdentifier("sub3")
    //                        .build())
    //                .addDisplayPart(BluePrinted.DisplayPart.builder()
    //
    // .context(commonAttribute.copyTo(DisplayEntityBuilder.ofBlock(Material.STRIPPED_SPRUCE_LOG)))
    //                        .transformation(p5)
    //                        .partIdentifier("sub4")
    //                        .build());
    //        createTerminal(p, manager);
    //    }
    //
    //    @OnlineTest(automatic = false, name = "Display Arrow Test")
    //    public void test_genArrow(CommandSender executor, String[] args) throws Throwable {
    //        if (!(executor instanceof Player p)) {
    //            Debug.logger("Not a player,stopping test");
    //            return;
    //        }
    //        float dx = Float.parseFloat(args[0]);
    //        float dy = Float.parseFloat(args[1]);
    //        float dz = Float.parseFloat(args[2]);
    //        DisplayManager manager = new SimpleDisplayManager();
    //        manager.addDisplayPart(BluePrinted.DisplayPart.builder()
    //                .partIdentifier("nu")
    //                .context(DisplayEntityBuilder.ofBlock(Material.GOLD_BLOCK)
    //                        //                        .with(display -> display.setInterpolationDuration(200))
    //                        //                        .with(display -> display.setInterpolationDelay(10))
    //                        .cast())
    //                .build());
    //        Debug.logger("Points to", dx, dy, dz);
    //        ScheduleManager.getManager()
    //                .getScheduledFuture(
    //                        () -> {
    //                            manager.buildDisplay(p.getLocation(), FixedEntityGroup.builder("testcase", null,
    // true));
    //                            return null;
    //                        },
    //                        0,
    //                        true)
    //                .get();
    //        ExecutorUtils.sleep(2_000);
    //        var vec = new Vector3f(dx, dy, dz);
    //        Quaternionf q = TransformationUtils.rotateOriginTo(vec, true);
    //
    //        float len = vec.length();
    //        manager.reshapeBase(1.0f, len, 1.0f, true);
    //        ExecutorUtils.sleep(2_000);
    //        manager.appendTransformation(TransformationUtils.rotationAsLinear(q), true);
    //    }
    //
    //    @OnlineTest(automatic = false, name = "Display Robot Test")
    //    public void test_robot(CommandSender executor, String[] args) throws Throwable {
    //        if (!(executor instanceof Player p)) {
    //            Debug.logger("Not a player,stopping test");
    //            return;
    //        }
    //        DisplayModelBuilder rootBuilder = DisplayModelBuilder.builder()
    //                .addDisplayPart(BluePrinted.DisplayPart.builder()
    //                        .partIdentifier("root-body-1")
    //                        .transformation(TransformationUtils.buildCubeAtCenter(1, 2, 1)
    //                                .addTranslate(0, 1, 0)
    //                                .build())
    //                        .context(DisplayEntityBuilder.ofBlock(Material.IRON_BLOCK))
    //                        .build());
    //        DisplayModelBuilder arm = DisplayModelBuilder.builder()
    //                .addDisplayPart(BluePrinted.DisplayPart.builder()
    //                        .partIdentifier("arm-body-1")
    //                        .transformation(TransformationUtils.buildCubeAtCenter(0.2f, 0.5f, 0.2f)
    //                                .addTranslate(0, -0.25f, 0)
    //                                .build())
    //                        .context(DisplayEntityBuilder.ofBlock(Material.COAL_BLOCK))
    //                        .build());
    //        /**
    //         * 丸辣,mc是左手系
    //         */
    //        // todo add more joint
    //        // todo use json to configure build and action
    //        Joint joint_root_left_arm =
    //                new RevolutePolarJoint("joint1", new Vector3f(0, 1.8f, 0.65f), Vectors.AXIS_XN, Vectors.AXIS_Y);
    //        Joint joint_root_right_arm =
    //                new RevolutePolarJoint("joint2", new Vector3f(0, 1.8f, -0.65f), Vectors.AXIS_X, Vectors.AXIS_YN);
    //        Joint joint_left_arm_2_arm = new RevoluteAxisJoint("joint3", new Vector3f(0f, -0.5f, 0f), Vectors.AXIS_Z);
    //        Joint joint_right_arm_2_arm = new RevoluteAxisJoint("joint4", new Vector3f(0, -0.5f, 0f), Vectors.AXIS_Z);
    //        ScheduleManager.getManager().execute(() -> {
    //            Location loc = p.getEyeLocation();
    //            loc.getChunk();
    //            // simplify this shit, please
    //            // can we make parent null?
    //            // shit, I cant
    //            Marker rootp = loc.getChunk().getWorld().spawn(loc, Marker.class);
    //            Marker larm1p = loc.getChunk().getWorld().spawn(loc, Marker.class);
    //            Marker larm2p = loc.getChunk().getWorld().spawn(loc, Marker.class);
    //            Marker rarm1p = loc.getChunk().getWorld().spawn(loc, Marker.class);
    //            Marker rarm2p = loc.getChunk().getWorld().spawn(loc, Marker.class);
    //            RobotRoot root = new RobotRoot("matlibtest", rootp, "root");
    //            root.copyFrom(rootBuilder);
    //            root.buildAt(loc, (str, sup) -> root.addMemberSync(str, sup.get()));
    //            LinkDisplayGroup larm1 = new LinkDisplayGroup("matlibtest", larm1p, "larm1");
    //            larm1.copyFrom(arm);
    //            larm1.buildAt(loc, (str, sup) -> larm1.addMemberSync(str, sup.get()));
    //            LinkDisplayGroup larm2 = new LinkDisplayGroup("matlibtest", larm2p, "larm2");
    //            larm2.copyFrom(arm);
    //            larm2.buildAt(loc, (str, sup) -> larm2.addMemberSync(str, sup.get()));
    //            LinkDisplayGroup rarm1 = new LinkDisplayGroup("matlibtest", rarm1p, "rarm1");
    //            rarm1.copyFrom(arm);
    //            rarm1.buildAt(loc, (str, sup) -> rarm1.addMemberSync(str, sup.get()));
    //            LinkDisplayGroup rarm2 = new LinkDisplayGroup("matlibtest", rarm2p, "rarm2");
    //            rarm2.copyFrom(arm);
    //            rarm2.buildAt(loc, (str, sup) -> rarm2.addMemberSync(str, sup.get()));
    //            root.addChildLink(joint_root_left_arm, larm1);
    //            root.addChildLink(joint_root_right_arm, rarm1);
    //            larm1.addChildLink(joint_left_arm_2_arm, larm2);
    //            rarm1.addChildLink(joint_right_arm_2_arm, rarm2);
    //            root.updateConfiguration();
    //            AbstractMainCommand robotController = new DisplayRobotTerminalCommand(root);
    //            AsyncCommandTerminal terminal = new AsyncCommandTerminal(p, robotController);
    //            terminal.setOnEnd(root::killGroupAsync);
    //            Debug.logger("Starting Command Terminal...");
    //            terminal.onEnable(ChatInputManager.getManager());
    //        });
    //    }
    //
    //    private static void createTerminal(Player p, DisplayManager manager) throws Throwable {
    //        ScheduleManager.getManager()
    //                .getScheduledFuture(
    //                        () -> {
    //                            Location loc = p.getLocation().add(0, 1, 0);
    //                            manager.buildDisplay(
    //                                    loc,
    //                                    FixedEntityGroup.builder(
    //                                            MatlibTest.getInstance(),
    //                                            loc.getChunk().getWorld().spawn(loc, Interaction.class),
    //                                            true));
    //                            AbstractMainCommand managerController = new DisplayManagerTerminalCommand(manager);
    //                            AsyncCommandTerminal terminal = new AsyncCommandTerminal(p, managerController);
    //                            terminal.setOnEnd(manager::deconstruct);
    //                            Debug.logger("Starting Command Terminal...");
    //                            terminal.onEnable(ChatInputManager.getManager());
    //                            return null;
    //                        },
    //                        0,
    //                        true)
    //                .get();
    //    }
    //
    //    public static class DisplayManagerTerminalCommand extends AbstractMainCommand {
    //        public DisplayManagerTerminalCommand(DisplayManager manager) {
    //            this.handle = manager;
    //        }
    //
    //        DisplayManager handle;
    //
    //        @Override
    //        public String permissionRequired() {
    //            return null;
    //        }
    //
    //        SubCommand mainCommand = genMainCommand("terminal");
    //        SubCommand translationCommand = new SubCommand(
    //                "trans", new SimpleCommandArgs("axisX", "axisY", "axisZ", "theta", "scale", "dx", "dy", "dz"),
    // "...") {
    //            @Override
    //            public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    //                var re = parseInput(var4).getA();
    //
    //                float axisX = re.nextFloat();
    //                float axisY = re.nextFloat();
    //                float axisZ = re.nextFloat();
    //                float theta = re.nextFloat();
    //                float scale = re.nextFloat();
    //                float dx = re.nextFloat();
    //                float dy = re.nextFloat();
    //                float dz = re.nextFloat();
    //                var axis = new Vector3f(axisX, axisY, axisZ);
    //                var q = TransformationUtils.fromAxisAngle(axis, theta);
    //                var d = new Vector3f(dx, dy, dz);
    //                Debug.logger("Doing Transformation:", axis, theta, "(q:", q, "),", scale, d);
    //                handle.appendTransformation(q, scale, d, true);
    //                return true;
    //            }
    //        }.setFloat("axisX")
    //                .setFloat("axisY")
    //                .setFloat("axisZ")
    //                .setFloat("theta")
    //                .setFloat("scale", 1.0f)
    //                .setFloat("dx")
    //                .setFloat("dy")
    //                .setFloat("dz")
    //                .register(this);
    //        SubCommand undoCommand = new SubCommand("undo", new SimpleCommandArgs("all"), "...") {
    //            @Override
    //            public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    //                if (var4.length == 0) {
    //                    var1.sendMessage("undo");
    //                    handle.undo();
    //                } else {
    //                    var1.sendMessage("undo All");
    //                    handle.undoAll();
    //                }
    //                return true;
    //            }
    //        }.register(this);
    //        private HashMap<String, BukkitRunnable> task = new HashMap<>();
    //
    //        SubCommand taskCommand = new SubCommand("task", new SimpleCommandArgs("id"), "...") {
    //            @Override
    //            public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    //                var input = parseInput(var4).getA();
    //                String id = input.nextArg();
    //                BukkitRunnable bkt = task.remove(id);
    //                if (bkt != null && !bkt.isCancelled()) {
    //                    bkt.cancel();
    //                    var1.sendMessage("cancel previous tasks");
    //                }
    //                Runnable t;
    //                switch (id) {
    //                    case "task1":
    //                        t = DisplayManagerTerminalCommand.this::task1;
    //                        break;
    //                    default:
    //                        var1.sendMessage("task ID不匹配");
    //                        return false;
    //                }
    //                BukkitRunnable taskR = ExecutorUtils.getRunnable(t);
    //                taskR.runTaskTimer(ThreadUtils.getMockPlugin(), 0, 1);
    //                task.put(id, taskR);
    //                return true;
    //            }
    //        }.setTabCompletor("id", () -> List.of("task1")).register(this);
    //        SubCommand cancelCommand = new SubCommand("canc", new SimpleCommandArgs("id"), "...") {
    //            @Override
    //            public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    //                var input = parseInput(var4).getA();
    //                String id = input.nextArg();
    //                BukkitRunnable bkt = task.remove(id);
    //                if (bkt != null && !bkt.isCancelled()) {
    //                    bkt.cancel();
    //                    var1.sendMessage("cancel previous tasks");
    //                    return true;
    //                } else {
    //                    if ("all".equals(id)) {
    //                        for (BukkitRunnable task : task.values()) {
    //                            if (!task.isCancelled()) {
    //                                task.cancel();
    //                            }
    //                        }
    //                        task.clear();
    //                        var1.sendMessage("cancel all tasks");
    //                        return true;
    //                    }
    //                    var1.sendMessage("no valid tasks found");
    //                    return false;
    //                }
    //            }
    //        }.setTabCompletor("id", () -> task.keySet().stream().toList()).register(this);
    //        SubCommand scaleCommand = new SubCommand("scale", AbstractMainCommand.genArgument("sx", "sy", "sz"),
    // "...") {
    //            @Override
    //            public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    //                var re = parseInput(var4).getA();
    //                try {
    //                    float scaleX = Float.parseFloat(re.nextArg());
    //                    float scaleY = Float.parseFloat(re.nextArg());
    //                    float scaleZ = Float.parseFloat(re.nextArg());
    //                    handle.reshapeBase(scaleX, scaleY, scaleZ, true);
    //                } catch (Throwable e) {
    //                    var1.sendMessage("参数有误");
    //                    Debug.logger(e);
    //                }
    //                return true;
    //            }
    //        }.setFloat("sx", 1.0f).setFloat("sy", 1.0f).setFloat("sz", 1.0f).register(this);
    //
    //        private void task1() {
    //            handle.appendTransformation(new Vector3f(0.0f, 0.0f, 1.0f), 0.1f, true);
    //        }
    //    }
    //
    //    public static class DisplayRobotTerminalCommand extends AbstractMainCommand {
    //        public DisplayRobotTerminalCommand(RobotConfigure root) {
    //            this.root = root;
    //        }
    //
    //        RobotConfigure root;
    //
    //        @Override
    //        public String permissionRequired() {
    //            return null;
    //        }
    //
    //        SubCommand mainCommand = genMainCommand("terminal");
    //        SubCommand configCommand = new SubCommand("config", AbstractMainCommand.genArgument(), "...") {
    //            @Override
    //            public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    //                var re = parseArgument(var4);
    //                Object2DoubleOpenHashMap<String> config =
    //                        new Object2DoubleOpenHashMap<>(re.getArgsMap().entrySet().stream()
    //                                .collect(Collectors.toMap(
    //                                        Map.Entry::getKey,
    //                                        e -> CommandUtils.parseDoubleOrDefault(e.getValue(), 0.0d))));
    //                Debug.logger("adding configure", re);
    //                root.appendAngleConfiguration(config);
    //                return true;
    //            }
    //        }.register(this);
    //        SubCommand tpCommand = new SubCommand("tp", AbstractMainCommand.genArgument("x", "y", "z"), "...") {
    //            @Override
    //            public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
    //                var re = parseInput(var4).getA();
    //                double x = re.nextDouble();
    //                double y = re.nextDouble();
    //                double z = re.nextDouble();
    //                Location now = root.getRootLocation().clone().add(x, y, z);
    //                root.setRootLocation(now);
    //                return true;
    //            }
    //        }.setFloat("x").setFloat("y").setFloat("z").register(this);
    //
    //        @Override
    //        public boolean onCommandAsync(
    //                @NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4)
    // {
    //            try {
    //                return ScheduleManager.getManager()
    //                        .getScheduledFuture(
    //                                () -> {
    //                                    return onCommand(var1, var2, var3, var4);
    //                                },
    //                                0,
    //                                true)
    //                        .get();
    //            } catch (ExecutionException | InterruptedException ie) {
    //                throw new RuntimeException(ie);
    //            }
    //        }
    //    }
}
