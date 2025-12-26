package me.matl114.matlib.nmsUtils;

import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import org.bukkit.persistence.PersistentDataAdapterContext;

public class CraftBukkitUtils {
    private static final Object DATA_TYPE_REGISTRY;
    private static final PersistentDataAdapterContext PDC_ADAPTOR_CONTEXT;

    public static Object getPdcDataTypeRegistry() {
        return DATA_TYPE_REGISTRY;
    }

    public static PersistentDataAdapterContext getPdcAdaptorContext() {
        return PDC_ADAPTOR_CONTEXT;
    }

    public static Pair<Object, PersistentDataAdapterContext> newPdcContext() {
        var registry = CraftBukkit.PERSISTENT_DATACONTAINER.createRegistry();
        PersistentDataAdapterContext pdcAdaptor = CraftBukkit.PERSISTENT_DATACONTAINER.createAdaptorContext(registry);
        return Pair.of(registry, pdcAdaptor);
    }

    static {
        DATA_TYPE_REGISTRY = CraftBukkit.PERSISTENT_DATACONTAINER.createRegistry();
        PDC_ADAPTOR_CONTEXT = CraftBukkit.PERSISTENT_DATACONTAINER.createAdaptorContext(DATA_TYPE_REGISTRY);
    }
}
