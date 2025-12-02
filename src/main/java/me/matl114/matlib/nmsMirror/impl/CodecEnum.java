package me.matl114.matlib.nmsMirror.impl;

import com.mojang.serialization.Codec;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.algorithms.dataStructures.struct.Pair;
import me.matl114.matlib.utils.version.Version;
import org.bukkit.entity.Cod;

import static me.matl114.matlib.nmsMirror.Import.*;
import static me.matl114.matlib.nmsMirror.impl.UtilsProtected.*;

public interface CodecEnum {

    Codec<Object> ITEMSTACK = fc(ItemStack,"CODEC");

//    Codec<Object> ITEM_NON_AIR = g1();

    Codec<Iterable<?>> CHAT_COMPONENT = fcVer( ComponentSerialization, Version.v1_20_R3, "CODEC");



}
