package me.matl114.matlib.nmsMirror.level;

import me.matl114.matlib.utils.reflect.descriptor.DescriptorImplBuilder;

class EnvInternel {
    public static final StateDefinitionHelper STATE_DEFINITION;

    static {
        STATE_DEFINITION = DescriptorImplBuilder.createHelperImpl(StateDefinitionHelper.class);
    }
}
