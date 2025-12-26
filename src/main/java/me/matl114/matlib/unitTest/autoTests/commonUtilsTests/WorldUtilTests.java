package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import me.matl114.matlib.implement.bukkit.schedule.ScheduleManager;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.version.VersionedWorld;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;

public class WorldUtilTests implements TestCase {
    @OnlineTest(name = "test copy blockState")
    public void test_copyBlockstate() throws Throwable {
        Location location = new Location(testWorld(), 128, 128, 128);
        Location targetLocation = location.clone().add(0, 3, 0);
        Assert(
                ScheduleManager.getManager()
                                .getScheduledFuture(
                                        () -> {
                                            location.getBlock().setType(Material.CHEST);
                                            VersionedWorld.getInstance()
                                                    .copyBlockStateTo(
                                                            location.getBlock().getState(false),
                                                            targetLocation.getBlock());
                                            return targetLocation.getBlock().getState(false);
                                        },
                                        0,
                                        true)
                                .get()
                        instanceof Chest);
    }
}
