package me.matl114.matlib.implement.custom.inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.SimpleLinkList;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.Stack;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import org.bukkit.entity.Player;

public class ScreenHistoryStackImpl implements ScreenHistoryStack {

    Map<UUID, Stack<Pair<Screen, Integer>>> historyStack;

    public ScreenHistoryStackImpl() {
        this.historyStack = new ConcurrentHashMap<>();
    }

    public void cleanPlayerHistory(Player player) {
        historyStack.remove(player.getUniqueId());
    }

    @NonNull public Stack<Pair<Screen, Integer>> getPlayerHistory(Player player) {
        return this.historyStack.computeIfAbsent(player.getUniqueId(), (i) -> new SimpleLinkList<>());
    }

    /**
     * return whether there are histories
     * @param player
     * @return
     */
    public void popLast(Player player) {
        var stack = getPlayerHistory(player);
        stack.poll();
    }

    public void pushNew(Screen screen, Player player, int page) {
        var stack = getPlayerHistory(player);
        stack.push(Pair.of(screen, page));
    }

    public void switchTopPage(Screen screen, Player player, int page) {
        var stack = getPlayerHistory(player);
        var pair = stack.peek();
        if (pair != null && pair.getA() == screen) {
            pair.setB(page);
        }
    }

    public boolean openLast(InventoryBuilder.InventoryFactory screenType, Player player) {
        var stack = getPlayerHistory(player);
        var history = stack.peek();
        if (history != null) {
            history.getA().openPage(screenType, player, history.getB());
            return true;
        } else return false;
    }
}
