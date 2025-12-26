package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.server.MinecraftServerHelper;
import me.matl114.matlib.nmsMirror.server.PlayerListHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;

public class NMSServer {
    public static final MinecraftServerHelper SERVER =
            DescriptorImplBuilder.createHelperImpl(MinecraftServerHelper.class);
    public static final PlayerListHelper PLAYER_LIST = DescriptorImplBuilder.createHelperImpl(PlayerListHelper.class);
}
