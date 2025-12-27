package me.matl114.matlib.nmsMirror.chat.v1_20_R4;

import static me.matl114.matlib.nmsMirror.Import.ChatComponent;
import static me.matl114.matlib.nmsMirror.Import.ChatComponentSerializer;
import static me.matl114.matlib.nmsMirror.impl.NMSCore.*;

import com.google.gson.*;
import com.mojang.serialization.JsonOps;
import me.matl114.matlib.nmsMirror.chat.ComponentHelper;

import me.matl114.matlib.nmsMirror.impl.CodecEnum;
import me.matl114.matlib.nmsMirror.impl.Env;
import me.matl114.matlib.utils.reflect.classBuild.annotation.IgnoreFailure;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectClass;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;
import me.matl114.matlib.utils.version.Version;

@MultiDescriptive(targetDefault = "net.minecraft.network.chat.Component")
public interface ComponentHelper_1_20_R4 extends ComponentHelper {

    default String toJson(@RedirectType(ChatComponent) Iterable<?> comp) {
        return toJson(comp, Env.REGISTRY_FROZEN);
    }

    default JsonElement toJsonTree(@RedirectType(ChatComponent) Iterable<?> comp) {
        return serialize(comp, Env.REGISTRY_FROZEN);
    }

    default Iterable<?> fromJson(String json) {
        return fromJson(json, Env.REGISTRY_FROZEN);
    }

    default Iterable<?> fromJson(JsonElement json) {
        return fromJson(json, Env.REGISTRY_FROZEN);
    }

    static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R5)
    default String toJson(
            @RedirectType(ChatComponent) Iterable<?> comp,
            @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry) {
        return GSON.toJson(serialize(comp, registry));
    }

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R5)
    default Iterable<?> deserialize(
            JsonElement json, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry) {
        return DataHelper.A.I.getOrThrow(
                CodecEnum.CHAT_COMPONENT.parse(
                        REGISTRIES.provideRegistryForDynamicOp(registry, JsonOps.INSTANCE), json),
                JsonParseException::new);
    }

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R5)
    default JsonElement serialize(
            @RedirectType(ChatComponent) Iterable<?> comp,
            @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry) {
        return DataHelper.A.I.getOrThrow(
                CodecEnum.CHAT_COMPONENT.encodeStart(
                        REGISTRIES.provideRegistryForDynamicOp(registry, JsonOps.INSTANCE), comp),
                JsonParseException::new);
    }

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R5)
    default Iterable<?> fromJson(
            String json, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry) {
        JsonElement jsonElement = JsonParser.parseString(json);
        return jsonElement == null ? null : deserialize(jsonElement, registry);
    }

    @MethodTarget(isStatic = true)
    @RedirectClass(ChatComponentSerializer)
    @IgnoreFailure(thresholdInclude = Version.v1_21_R5)
    default Iterable<?> fromJson(
            JsonElement json, @RedirectType("Lnet/minecraft/core/HolderLookup$Provider;") Object registry) {
        return json == null ? null : deserialize(json, registry);
    }
}
