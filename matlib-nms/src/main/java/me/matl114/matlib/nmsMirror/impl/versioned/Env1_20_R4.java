package me.matl114.matlib.nmsMirror.impl.versioned;

import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.CustomDataHelper;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentPatchHelper;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.DataComponentTypeAPI;
import me.matl114.matlib.nmsMirror.inventory.v1_20_R4.ItemDataComponentMapHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.version.Version;
import me.matl114.matlib.utils.version.VersionAtLeast;

@VersionAtLeast(Version.v1_20_R4)
public class Env1_20_R4 {
    public static final CustomDataHelper ICUSTOMDATA = DescriptorImplBuilder.createHelperImpl(CustomDataHelper.class);
    public static final ItemDataComponentMapHelper ICOMPONENT =
            DescriptorImplBuilder.createHelperImpl(ItemDataComponentMapHelper.class);
    public static final DataComponentPatchHelper COMPONENT_PATCH =
            DescriptorImplBuilder.createMultiHelper(DataComponentPatchHelper.class);
    public static final DataComponentTypeAPI DATA_TYPES =
            DescriptorImplBuilder.createMultiHelper(DataComponentTypeAPI.class);
}
