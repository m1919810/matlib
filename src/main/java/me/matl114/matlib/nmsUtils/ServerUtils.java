package me.matl114.matlib.nmsUtils;

import static me.matl114.matlib.nmsMirror.impl.NMSServer.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.FutureTask;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.nmsMirror.impl.NMSLevel;
import me.matl114.matlib.utils.WorldUtils;
import org.bukkit.World;

public class ServerUtils {
    private static final Map<World, Executor> cachedWorldExecutor = new HashMap<>();

    public static void executeSync(Runnable task) {
        Env.MAIN_EXECUTOR.execute(task);
    }

    public static <T> FutureTask<T> executeFuture(Callable<T> sup) {
        var future = new FutureTask<T>(sup);
        executeSync(future);
        return future;
    }

    public static void executeAsChunkTask(World world, Runnable task) {
        Executor executor = cachedWorldExecutor.computeIfAbsent(world, w -> {
            Object handled = WorldUtils.getHandledWorld(w);
            var chunkSource = NMSLevel.LEVEL.getChunkSource(handled);
            return NMSLevel.CHUNK_CACHE_SYSTEM.mainThreadProcessorGetter(chunkSource);
        });
        executor.execute(task);
    }

    public static void broadCastMessage(Iterable<?> chatMessages) {
        Object playerlist = SERVER.getPlayerList(Env.SERVER);
        PLAYER_LIST.broadcastSystemMessage(playerlist, chatMessages, false);
    }
}
