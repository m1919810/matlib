package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.level.*;
import me.matl114.matlib.nmsMirror.level.v1_20_R4.BlockEntityHelper_1_20_R4;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.version.Version;

public class NMSLevel {
    public static final BlockHelper BLOCK;
    public static final BlockStateHelper BLOCK_STATE;
    public static final LevelHelper LEVEL;
    public static final LevelChunkHelper LEVEL_CHUNK;
    public static final ServerChunkCacheHelper CHUNK_CACHE_SYSTEM;
    public static final BlockEntityHelper BLOCK_ENTITY;
    public static final BlockEntityAPI TILE_ENTITIES;
    public static final ServerPlayerHelper PLAYER;
    public static final ChunkMapHelper CHUNK_MAP;

    static {
        Version version = Version.getVersionInstance();
        BLOCK = DescriptorImplBuilder.createHelperImpl(BlockHelper.class);
        BLOCK_STATE = DescriptorImplBuilder.createHelperImpl(BlockStateHelper.class);
        LEVEL = DescriptorImplBuilder.createHelperImpl(LevelHelper.class);
        LEVEL_CHUNK = DescriptorImplBuilder.createHelperImpl(LevelChunkHelper.class);
        CHUNK_CACHE_SYSTEM = DescriptorImplBuilder.createHelperImpl(ServerChunkCacheHelper.class);
        if (version.isAtLeast(Version.v1_20_R4)) {
            BLOCK_ENTITY = DescriptorImplBuilder.createHelperImpl(BlockEntityHelper_1_20_R4.class);
        } else {
            BLOCK_ENTITY = DescriptorImplBuilder.createHelperImpl(BlockEntityHelper.class);
        }
        TILE_ENTITIES = DescriptorImplBuilder.createMultiHelper(BlockEntityAPI.class);
        PLAYER = DescriptorImplBuilder.createHelperImpl(ServerPlayerHelper.class);
        CHUNK_MAP = DescriptorImplBuilder.createMultiHelper(ChunkMapHelper.class);
    }
}
