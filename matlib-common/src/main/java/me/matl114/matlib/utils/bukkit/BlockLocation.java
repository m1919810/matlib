package me.matl114.matlib.utils.bukkit;

import org.bukkit.World;

public record BlockLocation(World world, int x, int y, int z) {

    public String toString() {
        return new StringBuilder()
                .append(world.getName())
                .append(',')
                .append(x)
                .append(',')
                .append(y)
                .append(',')
                .append(z)
                .toString();
    }
}
