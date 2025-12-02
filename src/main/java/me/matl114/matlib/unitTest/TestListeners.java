package me.matl114.matlib.unitTest;

import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import it.unimi.dsi.fastutil.objects.Reference2IntArrayMap;
import lombok.Getter;
import lombok.With;
import lombok.experimental.Accessors;
import lombok.experimental.WithBy;
import me.matl114.matlib.implement.slimefun.manager.BlockDataCache;
import me.matl114.matlib.nmsMirror.core.PosEnum;
import me.matl114.matlib.nmsMirror.impl.CraftBukkit;
import me.matl114.matlib.nmsMirror.impl.NMSItem;
import me.matl114.matlib.nmsMirror.impl.NMSLevel;
import me.matl114.matlib.nmsUtils.LevelUtils;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.WorldUtils;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.ServerLinks;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerLinksSendEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.sql.Ref;
import java.util.Arrays;
import java.util.HashMap;

import static me.matl114.matlib.nmsMirror.impl.NMSItem.*;
import static me.matl114.matlib.nmsMirror.impl.NMSLevel.*;

@Accessors(fluent = true, prefix = {"shit"})
@Getter
public class TestListeners implements Listener {
    //@EventHandler
    int shitA;
    public void checkClose(InventoryCloseEvent paper){

        Debug.logger("close Inventory here",paper.getInventory().getClass());
    }
   // @EventHandler
    public void checkOpen(InventoryOpenEvent paper){
        Debug.logger("open Inventory here",paper.getInventory().getClass());
    }
    //@EventHandler
    public void checkOpenView(InventoryClickEvent inventoryOpenEvent){
        Debug.logger("open Inventory View check", inventoryOpenEvent.getWhoClicked().getOpenInventory().getTopInventory());
    }

    @EventHandler
    public void checkServerLink(PlayerLinksSendEvent event){
        URI uri = URI.create("https://zh.minecraft.wiki/w/%E6%9A%82%E5%81%9C%E8%8F%9C%E5%8D%95");
        for (var type: ServerLinks.Type.values()){
            event.getLinks().addLink(type, uri);
        }
    }
//    @EventHandler
//    public void checkElytra(EntityToggleGlideEvent event){
//        Debug.logger("Toggle Glide", event.isGliding());
//    }



    public static final Inventory EMPTY_INVENTORY = CraftBukkit.INVENTORYS.createCustomInventory(new InventoryHolder() {
        @Override
        public @NotNull Inventory getInventory() {
            return EMPTY_INVENTORY;
        }
    },1,"custom"
    );
    public static final Inventory FULL_INVENTORY = CraftBukkit.INVENTORYS.createCustomInventory(new InventoryHolder() {
       @Override
       public @NotNull Inventory getInventory() {
           return FULL_INVENTORY;
       }
    },1,"custom"
    );
    static{
        FULL_INVENTORY.setItem(0, ChestMenuUtils.getBackground().clone());
    }
//    @EventHandler
//    public void testSlimefunInventoryHopper(HopperInventorySearchEvent hopper){
//
//        Location loc = hopper.getSearchBlock().getLocation();
//        BlockMenu menu = BlockDataCache.getManager().getMenu(loc);
//        if(menu != null){
//            if(hopper.getContainerType() == HopperInventorySearchEvent.ContainerType.SOURCE){
//                limitGrabbingSlots(menu, hopper.getBlock());
//                hopper.setInventory(EMPTY_INVENTORY);
//            }else {
//                limitPushingSlots(hopper.getBlock(), menu);
////                if(){
////                    //successfully push, set on cooldown
////                }else {
////                    //no push, let it go,
////                    hopper.setInventory(null);
////                }
//                //whatever, let on Cooldown
//                hopper.setInventory(FULL_INVENTORY);
//
//            }
//        }
//
//    }

    public void limitGrabbingSlots(BlockMenu sf, Block hopperBlock){
        Object slimefunContainer = CraftBukkit.INVENTORYS.getInventory(sf.getInventory());
        Object hopperContainer = LevelUtils.getBlockEntityAsync(hopperBlock, false);
        if(TILE_ENTITIES.isHopper(hopperContainer)){
            var world = WorldUtils.getHandledWorld(sf.getLocation().getWorld());
            var direction = PosEnum.DIR_DOWN;
            TILE_ENTITIES.hopper$setSkipPullModeEventFire(true);
            int[] access = sf.getPreset().getSlotsAccessedByItemTransport(sf, ItemTransportFlow.WITHDRAW, null);
            int i = access.length;
            for (int j = 0; j< i; ++j){
                int k = access[j];
                if(tryTakeInItemFromSlot(hopperContainer, slimefunContainer, k, direction, world)){
                    return;
                }
            }
        }
    }
    public boolean tryTakeInItemFromSlot(Object hopper, Object slimefunContainer, int index, Object direction, Object world){
        var itemStack = NMSItem.CONTAINER.getItem(slimefunContainer, index);
        if(!NMSItem.ITEMSTACK.isEmpty(itemStack)){
            return TILE_ENTITIES.hopper$hopperPull(world, hopper, slimefunContainer, itemStack, index);
        }
        return false;
    }
    private static final Reference2IntArrayMap<World> configureHopperAmountCache = new Reference2IntArrayMap<>();
    private static final Reference2IntArrayMap<World> configureHopperTransferCache = new Reference2IntArrayMap<>();

    public boolean limitPushingSlots(Block hopperBlock, BlockMenu sf){
        Object hopperContainer = LevelUtils.getBlockEntityAsync(hopperBlock, false);
        Object slimefunContainer = CraftBukkit.INVENTORYS.getInventory(sf.getInventory());
        boolean foundItem = false;
        World world = sf.getLocation().getWorld();
        int hopperValue = configureHopperAmountCache.computeIfAbsent(world, (w)->{
            Object handled = WorldUtils.getHandledWorld((World) w);
            Object config = LEVEL.spigotConfigGetter(handled);
            return CraftBukkit.SPIGOT_CONFIG.hopperAmountGetter(config);
        });

        if(TILE_ENTITIES.isHopper(hopperContainer)){
            int size = CONTAINER.getContainerSize(hopperContainer);
            for (int i=0; i< size ;++ i){
                final var itemStack0 = CONTAINER.getItem(hopperContainer, i);
                if(!ITEMSTACK.isEmpty(itemStack0)){
                    var itemStack = itemStack0;
                    foundItem = true;
                    int originalItemCount = ITEMSTACK.getCount(itemStack);
                    int movedItemCount =  Math.min(hopperValue, originalItemCount);
                    ITEMSTACK.setCount(itemStack, movedItemCount);
                    var remainingItem =addItem(sf, hopperContainer, slimefunContainer, itemStack);
                    int remainingItemCount = ITEMSTACK.getCount(remainingItem);
                    if(remainingItemCount != movedItemCount){
                        //moved
                        itemStack = ITEMSTACK.copy(itemStack, true);
                        ITEMSTACK.setCount(itemStack, originalItemCount);
                        if(!ITEMSTACK.isEmpty(itemStack)){
                            ITEMSTACK.setCount(itemStack, originalItemCount - movedItemCount + remainingItemCount);
                        }
                        CONTAINER.setItem(hopperContainer, i, itemStack);
                        return true;
                    }
                    ITEMSTACK.setCount(itemStack, originalItemCount);
                }
            }
        }
        if(foundItem){
            int cooldown = configureHopperTransferCache.computeIfAbsent(world, (w)->{
                Object handled = WorldUtils.getHandledWorld((World) w);
                Object config = LEVEL.spigotConfigGetter(handled);
                return CraftBukkit.SPIGOT_CONFIG.hopperTransferGetter(config);
            });
            TILE_ENTITIES.hopper$setCooldown(hopperContainer, cooldown);
        }
        return false;
    }
    public Object addItem(BlockMenu sf, Object hopper, Object target, Object movedItem ){
        int[] access = sf.getPreset().getSlotsAccessedByItemTransport(sf, ItemTransportFlow.INSERT, null);
        int size = access.length;
        for (int i=0; i<size && !ITEMSTACK.isEmpty(movedItem); ++i){
            movedItem = TILE_ENTITIES.hopper$tryMoveInItem(hopper, target, movedItem, access[i], null);
        }
        return movedItem;
    }
}
