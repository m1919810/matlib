package me.matl114.matlib.utils.serialization;

import com.mojang.serialization.*;
import java.util.*;

public class TypeOps {
    public static final JavaOps I = JavaOps.INSTANCE;

    public static final Codec<Map> MAP = new TypeCodec<>(I, Map.class);
}
