package me.matl114.matlib.utils.serialization;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import javax.annotation.Nonnull;
import me.matl114.matlib.utils.registry.Content;
import me.matl114.matlib.utils.registry.Registry;
import me.matl114.matlib.utils.registry.impl.RegistryContent;
import me.matl114.matlib.utils.serialization.datafix.DataHelper;

public class RegistryCodec<S> implements Codec<RegistryContent<S>> {
    Codec<RegistryContent<S>> registryLookup;
    Registry<S> registry;

    public RegistryCodec(Registry<S> registryView) {
        this.registry = registryView;
        this.registryLookup = Codec.STRING.comapFlatMap(
                id -> {
                    Content<S> content = registry.getContentById(id);
                    return content == null
                            ? DataHelper.A.I.error(() -> "Unknown registry key in " + registry.getKey() + ": " + id)
                            : safeCast(content);
                },
                RegistryContent::getId);
    }

    public DataResult<RegistryContent<S>> safeCast(@Nonnull Content<S> content) {
        return content instanceof RegistryContent<S> ref
                ? DataHelper.A.I.success(ref)
                : (DataHelper.A.I.error(() -> "Unregistered content in " + this.registry.getKey()));
    }

    @Override
    public <T> DataResult<Pair<RegistryContent<S>, T>> decode(DynamicOps<T> ops, T input) {
        return this.registryLookup.decode(ops, input);
    }

    @Override
    public <T> DataResult<T> encode(RegistryContent<S> input, DynamicOps<T> ops, T prefix) {
        return this.registryLookup.encode(input, ops, prefix);
    }
}
