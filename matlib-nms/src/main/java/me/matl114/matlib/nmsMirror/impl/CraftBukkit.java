package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.craftbukkit.adventure.PaperAdventureAPI;
import me.matl114.matlib.nmsMirror.craftbukkit.configuration.SpigotWorldConfigHelper;
import me.matl114.matlib.nmsMirror.craftbukkit.core.CraftRegistryHelper;
import me.matl114.matlib.nmsMirror.craftbukkit.entity.CraftEntityHelper;
import me.matl114.matlib.nmsMirror.craftbukkit.inventory.CraftItemStackHelper;
import me.matl114.matlib.nmsMirror.craftbukkit.inventory.InventoryAPI;
import me.matl114.matlib.nmsMirror.craftbukkit.inventory.ItemMetaAPI;
import me.matl114.matlib.nmsMirror.craftbukkit.persistence.CraftPersistentDataContainerHelper;
import me.matl114.matlib.nmsMirror.craftbukkit.utils.MagicNumberAPI;
import me.matl114.matlib.nmsMirror.craftbukkit.world.CraftBlockHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;

public class CraftBukkit {

    public static final MagicNumberAPI MAGIC_NUMBERS;
    public static final CraftItemStackHelper ITEMSTACK;
    public static final CraftBlockHelper BLOCK;
    public static final InventoryAPI INVENTORYS;
    public static final SpigotWorldConfigHelper SPIGOT_CONFIG;
    public static final CraftEntityHelper ENTITY;
    public static final PaperAdventureAPI ADVENTURE;
    public static final CraftRegistryHelper REGISTRY;
    public static final ItemMetaAPI META;
    // should have a persistentDataContainerHelper
    public static final CraftPersistentDataContainerHelper PERSISTENT_DATACONTAINER;

    static {
        MAGIC_NUMBERS = DescriptorImplBuilder.createMultiHelper(MagicNumberAPI.class);
        ITEMSTACK = DescriptorImplBuilder.createHelperImpl(CraftItemStackHelper.class);
        BLOCK = DescriptorImplBuilder.createHelperImpl(CraftBlockHelper.class);
        PERSISTENT_DATACONTAINER = DescriptorImplBuilder.createMultiHelper(CraftPersistentDataContainerHelper.class);
        INVENTORYS = DescriptorImplBuilder.createMultiHelper(InventoryAPI.class);
        SPIGOT_CONFIG = DescriptorImplBuilder.createHelperImpl(SpigotWorldConfigHelper.class);
        ENTITY = DescriptorImplBuilder.createHelperImpl(CraftEntityHelper.class);
        ADVENTURE = DescriptorImplBuilder.createMultiHelper(PaperAdventureAPI.class);
        REGISTRY = DescriptorImplBuilder.createMultiHelper(CraftRegistryHelper.class);
        META = DescriptorImplBuilder.createMultiHelper(ItemMetaAPI.class);
    }
}
