package me.matl114.matlib.nmsUtils.nbt;

import com.destroystokyo.paper.Namespaced;
import com.google.common.base.Suppliers;
import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.collection.ListMapView;
import me.matl114.matlib.algorithms.dataStructures.frames.mmap.ValueAccess;
import me.matl114.matlib.nmsMirror.impl.NMSChat;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsUtils.ChatUtils;
import me.matl114.matlib.nmsUtils.ItemUtils;
import me.matl114.matlib.nmsUtils.VersionedUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.version.Version;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ItemMetaView extends ItemMeta {
    @Getter
    public static boolean versionAtLeast1_20_R4 = Version.getVersionInstance().isAtLeast(Version.v1_20_R4);
    @Getter
    public static boolean versionAtLeast1_21_R4 = Version.getVersionInstance().isAtLeast(Version.v1_21_R4);
    public static Supplier<Function<Object, ItemMetaView>> metaViewFactory = Suppliers.memoize(()->{
        if(versionAtLeast1_21_R4) {
            return ItemMetaViewImpl_1_21_R4::new;
        } else if (versionAtLeast1_20_R4) {
            return ItemMetaViewImpl_1_20_R4::new;
        } else {
            return ItemMetaViewImpl::new;
        }
    });

    public static ItemMetaView of(Object itemStack){
        //empty item should not have meta! or someone may modify ItemStack.EMPTY using meta
        if(NMSItem.ITEMSTACK.isEmpty(itemStack))return null;

        return metaViewFactory.get().apply(itemStack);
    }
    public static ItemMetaView ofCraft(ItemStack stack){
        return of(ItemUtils.unwrapHandle(stack));
    }

    @Override
    default String getAsString(){
        return getAsTag().toString();
    }

    //------------------------------- Custom API -------------------------------

    public Object getNMSItemStack();

    public ListMapView<?, Iterable<?>> getNMSLoreView();

    public ValueAccess<Iterable<?>> getNMSNameView();

    public Object getAsTag();

    public Object getAsComponentPatch();

    public PersistentDataContainer getInternalNbt();

    default void addLore(List<String> lore){
        var lst = getNMSLoreView();
        for (var str: lore){
            lst.add(ChatUtils.deserializeLegacy(str));
        }
        lst.batchWriteback();
    }

    default void addLoreAdventure(List<Component> adventure){
        var lst = getNMSLoreView();
        for (var str: adventure){
            lst.add(NMSChat.CHATCOMPONENT.newAdventure(str));
        }
        lst.batchWriteback();
    }
    default void addLoreAt(int index, String... lore){
        var lst = getNMSLoreView();
        int size = lst.size();
        if(index >= 0  && index < size){
            for (int i=0; i<lore.length; ++i){
                lst.add(i + index, ChatUtils.deserializeLegacy(lore[i]));
            }
        }else {
            for (var str: lore){
                lst.add(ChatUtils.deserializeLegacy(str));
            }
        }
        lst.batchWriteback();
    }
    default void addLoreAdventureAt(int index, Component... lore){
        var lst = getNMSLoreView();
        int size = lst.size();
        if(index >= 0  && index < size){
            for (int i=0; i<lore.length; ++i){
                lst.add(i + index, NMSChat.CHATCOMPONENT.newAdventure(lore[i]));
            }
        }else {
            for (var str: lore){
                lst.add(NMSChat.CHATCOMPONENT.newAdventure(str));
            }
        }
        lst.batchWriteback();
    }

    default void removeLore(int from){
        var lst = getNMSLoreView();
        int size = lst.size();
        if(from > size)return;
        lst.subList(from, size).clear();
        lst.batchWriteback();
    }

    default void removeLore(int from, int to){
        var lst = getNMSLoreView();
        int size = lst.size();
        if(from > size )return;
        lst.subList(from, Math.min(to, size)).clear();
        lst.batchWriteback();
    }
    //---------------------------ENSURE VERSIONED API --------------------------

    void removeEnchantments();
    
    //---------------------------ALL VERSION REMOVED API-------------------------
    @Override
    default Set<Material> getCanDestroy() {
        throw VersionedUtils.removal();
    }

    @Override
    default void setCanDestroy(Set<Material> set) {
        throw VersionedUtils.removal();
    }

    @Override
    default Set<Material> getCanPlaceOn() {
        throw VersionedUtils.removal();
    }

    @Override
    default void setCanPlaceOn(Set<Material> set) {
        throw VersionedUtils.removal();
    }

    @Override
    default @NotNull Set<Namespaced> getDestroyableKeys() {
        throw VersionedUtils.removal();
    }

    @Override
    default void setDestroyableKeys(@NotNull Collection<Namespaced> collection) {
        throw VersionedUtils.removal();
    }

    @Override
    default @NotNull Set<Namespaced> getPlaceableKeys() {
        throw VersionedUtils.removal();
    }

    @Override
    default void setPlaceableKeys(@NotNull Collection<Namespaced> collection) {
        throw VersionedUtils.removal();
    }

}
