package me.matl114.matlib.nmsMirror.level;

import static me.matl114.matlib.nmsMirror.Import.*;

import com.mojang.authlib.GameProfile;
import java.util.Locale;
import java.util.UUID;
import me.matl114.matlib.common.lang.annotations.ConstVal;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectName;
import me.matl114.matlib.utils.reflect.descriptor.annotations.CastCheck;
import me.matl114.matlib.utils.reflect.descriptor.annotations.Descriptive;
import me.matl114.matlib.utils.reflect.descriptor.annotations.FieldTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;

@Descriptive(target = "net.minecraft.server.level.ServerPlayer")
public interface ServerPlayerHelper extends TargetDescriptor {
    @CastCheck("net.minecraft.server.level.ServerPlayer")
    public boolean isPlayer(Object p);

    @MethodTarget
    org.bukkit.entity.Player getBukkitEntity(Object player);

    @MethodTarget
    public GameProfile getGameProfile(Object player);

    @MethodTarget
    public UUID getUUID(Object player);

    @FieldTarget
    public Object connectionGetter(Object player);

    @FieldTarget
    @ConstVal
    public Object inventoryMenuGetter(Object player);

    @FieldTarget
    public Object containerMenuGetter(Object player);

    @FieldTarget
    @RedirectName("adventure$localeGetter")
    Locale locale(Object player);

    @FieldTarget
    @RedirectName("adventure$localeSetter")
    void locale(Object player, Locale locale);
}
