package me.matl114.matlib.nmsUtils;

import com.google.gson.JsonElement;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.utils.reflect.LambdaUtils;
import me.matl114.matlib.utils.serialization.TypeOps;

public class BridgeNMS {
    @Nullable private static final Class<?> DynamicOpUtils = Holder.of(BridgeNMS.class)
            .thenApply(cls -> cls.getPackageName() + ".DynamicOpUtils")
            .thenApplyCaught(Class::forName)
            .valException(null)
            .get();

    @Nullable private static final Supplier<DynamicOps<JsonElement>> jsonOpsGetter = Holder.of(DynamicOpUtils)
            .thenApplyCaught(cls -> ((Supplier<DynamicOps<JsonElement>>)
                    LambdaUtils.createLambdaForStaticMethod(Supplier.class, cls.getMethod("jsonOp"))))
            .valException(null)
            .get();

    @Nullable private static final Supplier<DynamicOps<Object>> primOpsGetter = Holder.of(DynamicOpUtils)
            .thenApplyCaught(cls -> ((Supplier<DynamicOps<Object>>)
                    LambdaUtils.createLambdaForStaticMethod(Supplier.class, cls.getMethod("primOp"))))
            .valException(null)
            .get();

    public static DynamicOps<JsonElement> getJsonOps() {
        if (jsonOpsGetter == null) {
            return JsonOps.INSTANCE;
        } else {
            return jsonOpsGetter.get();
        }
    }

    public static DynamicOps<Object> getPrimOps() {
        if (primOpsGetter == null) {
            return TypeOps.I;
        } else {
            return primOpsGetter.get();
        }
    }
}
