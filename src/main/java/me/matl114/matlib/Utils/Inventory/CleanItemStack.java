package me.matl114.matlib.Utils.Inventory;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CleanItemStack extends ItemStack {
    public CleanItemStack(Material type, int amount, Function<ItemMeta,ItemMeta> metaMapper){
        super(type,amount);
        ItemMeta itemMeta = this.getItemMeta();
        this.setItemMeta(metaMapper.apply(itemMeta));
    }
    public CleanItemStack(Material type, int amount, Supplier<ItemMeta> metaSupplier){
        super(type,amount);
        this.setItemMeta(metaSupplier.get());
    }

    public CleanItemStack(ItemStack item, Consumer<ItemMeta> meta) {
        this(item.getType(),item.getAmount(),()->{
            ItemMeta meta1=item.getItemMeta();
            meta.accept(meta1);
            return meta1;
        });
    }

    public CleanItemStack(Material material,int amount, String name, String... lore) {
        this(material,amount, (im) -> {
            if (name != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            if (lore.length > 0) {
                List<String> lines = new ArrayList();
                String[] var4 = lore;
                int var5 = lore.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    String line = var4[var6];
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                im.setLore(lines);
            }
            return im;
        });
    }
    public CleanItemStack(ItemStack item, String name, String... lore){
        this(item.getType(),item.getAmount(), () -> {
            ItemMeta im=item.getItemMeta();
            if (name != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            if (lore.length > 0) {
                List<String> lines = new ArrayList();
                String[] var4 = lore;
                int var5 = lore.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    String line = var4[var6];
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                im.setLore(lines);
            }
            return im;
        });
    }

    public CleanItemStack(ItemStack item, Color color, String name, String... lore) {
        this(item, (im) -> {
            if (name != null) {
                im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
            }

            if (lore.length > 0) {
                List<String> lines = new ArrayList();
                String[] var5 = lore;
                int var6 = lore.length;

                for(int var7 = 0; var7 < var6; ++var7) {
                    String line = var5[var7];
                    lines.add(ChatColor.translateAlternateColorCodes('&', line));
                }

                im.setLore(lines);
            }

            if (im instanceof LeatherArmorMeta) {
                ((LeatherArmorMeta)im).setColor(color);
            }

            if (im instanceof PotionMeta) {
                ((PotionMeta)im).setColor(color);
            }

        });
    }


    public CleanItemStack(Material type, String name, String... lore) {
        this(type,1, name, lore);
    }

    public CleanItemStack(Material type, String name, List<String> lore) {
        this(type,1, name, (String[])lore.toArray(String[]::new ));
    }

    public CleanItemStack(ItemStack item, List<String> list) {
        this(item, (String)list.get(0), (String[])list.subList(1, list.size()).toArray(String[]::new ));
    }
    public CleanItemStack(Material item, List<String> list) {
        this(item, (String)list.get(0), (String[])list.subList(1, list.size()).toArray(String[]::new ));
    }

    public CleanItemStack(ItemStack item){
        this(item.getType(),item.getAmount(),item::getItemMeta);
    }
    public CleanItemStack(ItemStack item, int amount) {
        this(item.getType(),amount,item::getItemMeta);
    }

    public CleanItemStack(ItemStack item, Material type) {
        this(type,item.getAmount(),item::getItemMeta);
    }
}