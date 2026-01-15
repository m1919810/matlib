package me.matl114.matlib.nmsUtils.inventory;

public class ItemHashCacheFactories {
    public static final ItemHashCacheFactory INSTANCE = new ItemHashCacheFactory();

    public static final ItemHashCacheFactory WITH_HASH = new ItemHashCacheFactory.PrecomputeHash();
    public static final ItemHashCacheFactory WITH_CUSTOM_HASH = new ItemHashCacheFactory.PrecomputeHashLore();

    public static final ItemHashCacheFactory WITH_ALL_HASH = new ItemHashCacheFactory.PrecomputeHashAll();
}
