package me.matl114.matlib.unitTest.autoTests;

import me.matl114.matlib.algorithms.algorithm.ExecutorUtils;
import me.matl114.matlib.core.bukkit.schedule.ScheduleManager;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.entity.entityRecords.EntityRecord;
import me.matl114.matlib.utils.entity.entityRecords.FixedEntityRecord;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Vector;

public class EntityTests implements TestCase {
    @OnlineTest(name = "Entity Validation Test")
    public void test_EntityRecord() throws Throwable {
        World world = testWorld();
        Location loc = new Location(world, 100_000, 127, 100_000);
        EntityRecord record = ScheduleManager.getManager()
                .getScheduledFuture(
                        () -> {
                            loc.getChunk();
                            Entity entity = world.spawnEntity(loc, EntityType.MARKER);
                            Assert(entity.isValid());
                            Debug.logger("Get Entity ", entity.getUniqueId(), entity.getEntityId());
                            return FixedEntityRecord.ofFixedEntity(entity);
                        },
                        0,
                        true)
                .get();

        Debug.logger("Sleep 1_000 ms");
        ExecutorUtils.sleep(1_000);
        Assert(!record.stillValid());
        ScheduleManager.getManager()
                .getScheduledFuture(
                        () -> {
                            Assert(record.loadEntity());
                            Assert(record.getLocation()
                                    .equals(record.getEntity().getLocation()));
                            Assert(record.killEntity());
                            Assert(record.getEntity().isDead());
                            Assert(!record.isPresent());
                            return null;
                        },
                        0,
                        true)
                .get();
        Debug.logger("Entity Record test pass");
    }

    @OnlineTest(name = "test Method Safety")
    public void test_EntityMethodThreadSafety() throws Throwable {
        Location loc = new Location(testWorld(), 0, 128, 0);

        Entity marker = ScheduleManager.getManager()
                .getScheduledFuture(
                        () -> {
                            loc.getChunk().setForceLoaded(true);
                            ;
                            return loc.getWorld().spawnEntity(loc, EntityType.MARKER);
                        },
                        0,
                        true)
                .get();
        Assert(marker.isValid());
        marker.getPersistentDataContainer()
                .set(new NamespacedKey("minecraft", "newwbee"), PersistentDataType.STRING, "newbee Entity");
        Debug.logger(marker.getPersistentDataContainer());
        marker.setCustomName("1a1a");
        marker.setCustomNameVisible(true);
        marker.setVelocity(new Vector(0, 1, 0));

        ScheduleManager.getManager().execute(() -> {
            marker.teleport(loc.clone().add(0, 114, 0));
            marker.remove();
            loc.getChunk().setForceLoaded(false);
        });
    }
}
