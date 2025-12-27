package me.matl114.matlib.nmsMirror.impl;

import me.matl114.matlib.nmsMirror.core.BlockPosHelper;
import me.matl114.matlib.nmsMirror.core.RegistriesHelper;
import me.matl114.matlib.nmsMirror.nbt.CompoundTagHelper;
import me.matl114.matlib.nmsMirror.nbt.ListTagHelper;
import me.matl114.matlib.nmsMirror.nbt.TagAPI;
import me.matl114.matlib.nmsMirror.nbt.v1_21_R4.CompoundTagHelper_v1_21_R4;
import me.matl114.matlib.nmsMirror.nbt.v1_21_R4.TagAPI_v1_21_R4;
import me.matl114.matlib.nmsMirror.resources.ResourceLocationHelper;
import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;
import me.matl114.matlib.utils.version.Version;

public class NMSCore {
    public static final CompoundTagHelper COMPOUND_TAG;
    public static final ListTagHelper LIST_TAG;
    public static final TagAPI TAGS;

    public static final ResourceLocationHelper NAMESPACE_KEY;
    public static final RegistriesHelper REGISTRIES;
    public static final BlockPosHelper BLOCKPOS;

    static {
        // Version version = Version.getVersionInstance();
        if (Version.getVersionInstance().isAtLeast(Version.v1_21_R4)) {
            COMPOUND_TAG = DescriptorImplBuilder.createHelperImpl(CompoundTagHelper_v1_21_R4.class);
            TAGS = DescriptorImplBuilder.createMultiHelper(TagAPI_v1_21_R4.class);

        } else {
            COMPOUND_TAG = DescriptorImplBuilder.createHelperImpl(CompoundTagHelper.class);
            TAGS = DescriptorImplBuilder.createMultiHelper(TagAPI.class);
        }
        LIST_TAG = DescriptorImplBuilder.createHelperImpl(ListTagHelper.class);

        NAMESPACE_KEY = DescriptorImplBuilder.createHelperImpl(ResourceLocationHelper.class);
        REGISTRIES = DescriptorImplBuilder.createMultiHelper(RegistriesHelper.class);
        BLOCKPOS = DescriptorImplBuilder.createHelperImpl(BlockPosHelper.class);
    }
}
