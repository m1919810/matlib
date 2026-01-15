package me.matl114.matlib.utils.bukkit;

public record BlockPos(int x, int y, int z) {
    public BlockPos offset(int x1, int y1, int z1) {
        return new BlockPos(x + x1, y + y1, z + z1);
    }
}
