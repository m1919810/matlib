package me.matl114.matlib.implement.serialization;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ReferenceMap;
import it.unimi.dsi.fastutil.objects.Object2ReferenceOpenHashMap;
import me.matl114.matlib.algorithms.dataStructures.struct.Holder;
import me.matl114.matlib.common.lang.annotations.NeedTest;
import me.matl114.matlib.utils.registry.impl.NamespacedRegistryImpl;
import me.matl114.matlib.utils.registry.impl.RegistryContent;
import me.matl114.matlib.utils.serialization.BukkitCodecs;
import me.matl114.matlib.utils.serialization.RegistryCodec;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

@NeedTest
public interface ItemStackCodec {
    public ItemStack getItemStack();

    public RegistryContent<Codec<ItemStack>> getCodec();

    public static ItemStackCodec of(ItemStack stack, String type) {
        return new Immutable(stack, type);
    }

    public record Immutable(ItemStack stack, String type) implements ItemStackCodec {

        @Override
        public ItemStack getItemStack() {
            return stack;
        }

        @Override
        public RegistryContent<Codec<ItemStack>> getCodec() {
            return (RegistryContent<Codec<ItemStack>>) CODEC_REGISTRY.getContentById(type);
        }

        @Override
        public String toString() {
            return "ItemStackCodec[" + (stack == null ? "AIR" : stack) + ", codec=" + type + "]";
        }
    }

    public static NamespacedRegistryImpl<Codec<ItemStack>, NamespacedKey> CODEC_REGISTRY = new NamespacedRegistryImpl<>(
            "minecraft", "itemstack_codec_registry", false, new NamespacedKey("minecraft", "bukkit"));

    public static Object2ReferenceMap<String, MapCodec<? extends ItemStackCodec>> MAPCODEC_CACHE =
            new Object2ReferenceOpenHashMap<>();

    //    public static MapCodec<ItemStack> BUKKIT_STRING_MAPCODEC = BukkitCodecs.ITEMSTACK_STRING.fieldOf("item");
    //
    //    public static MapCodec<ItemStack> BUKKIT_MAP_MAPCODEC = BukkitCodecs.ITEMSTACK_MAP.fieldOf("item");
    //
    //    public static MapCodec<ItemStack> BUKKIT_MAPCODEC = BukkitCodecs.ITEMSTACK.fieldOf("item");

    public static Codec<ItemStackCodec> CODEC = new RegistryCodec<>(CODEC_REGISTRY)
            .<ItemStackCodec>dispatch(
                    "type",
                    ItemStackCodec::getCodec,
                    codec -> MAPCODEC_CACHE.computeIfAbsent(codec.getId(), (k) -> codec.value()
                            .xmap(item -> new Immutable(item, codec.getId()), Immutable::stack)
                            .fieldOf("item")));

    private static void init() {
        CODEC_REGISTRY.registerThis("bukkit", BukkitCodecs.ITEMSTACK);
    }

    static Holder<Void> INIT_TASK = Holder.empty().thenRun(ItemStackCodec::init);
}
