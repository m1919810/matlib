package me.matl114.matlib.Utils;

import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.libraries.dough.chat.ChatInput;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.common.ChatColors;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.LoreBuilder;
import me.matl114.matlib.Utils.Inventory.CleanItemStack;
import me.matl114.matlib.core.EnvironmentManager;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.abstractItems.MachineRecipe;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUtils {
    public static void init(String id,String addonName,Plugin pl){
        Debug.logger("Initializing Utils...");
        ADDON_ID=id;
        ADDON_NAME=addonName;
        ADDON_INSTANCE=pl;

    }
    public static String ADDON_NAME;
    public static String ADDON_ID;
    public static Plugin ADDON_INSTANCE;
    public static boolean USE_IDDECORATOR=true;
    private static final double SF_TPS = 20.0 / (double) Slimefun.getTickerTask().getTickRate();
    private static final DecimalFormat FORMAT = new DecimalFormat("###,###,###,###,###,###.#");
    private static Random random=new Random();
    private static Enchantment GLOW_EFFECT=EnvironmentManager.getManager().getVersioned().getEnchantment("infinity");
    protected static ItemStack RESOLVE_FAILED=AddUtils.addGlow( new CustomItemStack(Material.BARRIER,"&c解析物品失败"));
    public static String formatDouble(double s){
        return FORMAT.format(s);
    }
    public static String idDecorator(String b){
        if(USE_IDDECORATOR){
            return ADDON_ID+"_"+b;
        }
        else return b;
    }
    public static final String C="§";
    public static NamespacedKey getNameKey(String str) {
        return new NamespacedKey(ADDON_INSTANCE,str);
    }
    public static String desc(String str) {
        return "&7" + str;
    }
    public static String addonTag(String str) {
        return "&3"+ADDON_NAME+" " + str;
    }
    public static final String[] COLOR_MAP=new String[]{"&0","&1","&2","&3","&4","&5","&6","&7","&8","&9","&a","&b","&c","&d","&e","&f"};
    protected final HashMap<String, Color> STR2COLOR=new HashMap<>(){{
        //put("",Color.AQUA)
    }};
    public static String resolveRGB(int rgb){
        if(rgb>16777216){
            rgb=16777216;
        }
        else if (rgb<0){
            rgb=0;
        }
        String prefix="";
        for(int i=0;i<6;i++){
            int r=rgb%16;
            rgb=rgb/16;
            prefix=COLOR_MAP[r]+prefix;
        }
        prefix="&x"+prefix;
        return prefix;
    }

    public static String resolveRGB(String rgb) throws IllegalArgumentException {
        if(rgb.length()!=6){
            throw new IllegalArgumentException("Invalid RGB String");
        }
        String prefix="&x";
        for (int i=0;i<6;i++){
            prefix=prefix+"&"+rgb.substring(i,i+1);
        }
        return prefix;
    }
    public static int rgb2int(String rgb) throws IllegalArgumentException{
        if(rgb.length()!=6){
            throw new IllegalArgumentException("Invalid RGB String");
        }
        int value=0;
        for (int i=0;i<6;i++){
            char c=rgb.charAt(i);
            if(Character.isDigit(c)){
                value=value*16+(c-'0');
            }
            else if(c>='a'&&c<='f'){
                value=value*16+(c-'a'+10);
            }
            else if(c>='A'&&c<='F'){
                value=value*16+(c-'A'+10);
            }
            else throw new IllegalArgumentException("Invalid RGB String");
        }
        return value;
    }
    public static final int START_CODE=rgb2int("eb33eb");
    //15409899;
    public static final int END_CODE=rgb2int("970097");
    public static String color(String str){
        return resolveRGB(START_CODE)+str;
    }
    public static String colorful(String str) {
        int len=str.length()-1;
        if(len<=0){
            return resolveRGB(START_CODE)+str;
        }
        else{
            int start=START_CODE;
            int end=END_CODE;
            int[] rgbs=new int[9];
            for(int i=0;i<3;++i){
                rgbs[i]=start%256;
                rgbs[i+3]=end%256;
                rgbs[i+6]=rgbs[i+3]-rgbs[i];
                start=start/256;
                end=end/256;
            }
            String str2="";
            for(int i=0;i<=len;i++){
                str2=str2+resolveRGB(START_CODE+65536*((rgbs[8]*i)/len)+256*((rgbs[7]*i)/len)+((rgbs[6]*i)/len))+str.substring(i,i+1);
            }
            return str2;

        }
    }
    public static final ItemStack[] NULL_RECIPE=new ItemStack[]{null,null,null,null,null,null,null,null,null} ;
    public static final ItemMeta NULL_META=null;

    public static ItemStack resolveRandomizedItemStack(ItemStack stack){
        ItemStack result=null;
//        if(stack.getClass().getName().endsWith("RandomizedItemStack")){
//            try{
//                Object itemlist= ReflectUtils.invokeGetRecursively(stack, Settings.FIELD,"items");
//                if(itemlist!=null){
//                    ItemStack[] list1=(ItemStack[])itemlist;
//                    result=AddUtils.eqRandItemStackFactory(Arrays.stream(list1).toList());
//                }else if((itemlist=ReflectUtils.invokeGetRecursively(stack,Settings.FIELD,"itemStacks"))!=null){
//                    ItemStack[] list1=(ItemStack[])itemlist;
//                    result=AddUtils.eqRandItemStackFactory(Arrays.stream(list1).toList());
//                }
//            }catch (Throwable e){
//                e.printStackTrace();
//            }
//        }
        return result;
    }
    public static String getItemId(ItemStack its){
        if(its==null)return null;
        SlimefunItem sfitem=SlimefunItem.getByItem(its);
        if(sfitem==null){
            return (its.getAmount()==1?"":String.valueOf(its.getAmount()))+its.getType().toString().toUpperCase(Locale.ROOT);
        }else {
            return (its.getAmount()==1?"":String.valueOf(its.getAmount()))+sfitem.getId();
        }
    }
    public static ItemStack getCopy(ItemStack stack){
        ItemStack result;
//        if(stack instanceof AbstractItemStack abs){
//            return abs.copy();
//        }else
        if((result=resolveRandomizedItemStack(stack))!=null){
            return result;
        }else {
            return getCleaned(stack);
        }
    }
    public static ItemStack getCleaned(ItemStack stack){
        return stack==null?new ItemStack(Material.AIR): new CleanItemStack(stack);
    }

    public static ItemStack resolveItem(Object a){
        if(a==null)return null;
        if(a instanceof ItemStack item){
            return  getCopy(item);
        }else if(a instanceof SlimefunItem){
            return getCopy(((SlimefunItem) a).getItem());
        }else if(a instanceof  Material){
            return  new ItemStack((Material) a);
        }else if(a instanceof String){
            Pattern re=Pattern.compile("^([0-9]*)(.*)$");
            Matcher info= re.matcher((String)a);
            int cnt=-1;
            String id;
            if(info.find()){
                String amount=info.group(1);
                id=info.group(2);
                try{
                    cnt=Integer.parseInt(amount);
                }catch(NumberFormatException e){
                    cnt=-1;
                }
            }
            else{
                id=(String) a;
            }
            try{
                ItemStack b=getCopy(SlimefunItem.getById(id).getItem());
                if(cnt>0&&cnt!=b.getAmount()){
                    b.setAmount(cnt);
                }
                return b;
            }catch (Exception e){
                try{
                    ItemStack b=new ItemStack( EnvironmentManager.getManager().getVersioned().getMaterial(id));
                    if(cnt>0&&cnt!=b.getAmount()){
                        b=b.clone();
                        b.setAmount(cnt);
                    }
                    return b;
                }catch (Exception e2){
                    Debug.logger("WARNING: Object %s can not be solved ! Required Addon not installed ! Disabling relavent recipes...".formatted(a));
                    return RESOLVE_FAILED;
                }
            }
        } else {
            Debug.logger("WARNING: failed to solve Object "+a.toString());
            return RESOLVE_FAILED;
        }

    }
    public static Pair<ItemStack[], ItemStack[]> buildRecipes(Pair<Object[],Object[]> itemStacks){
        return buildRecipes(itemStacks.getFirstValue(),itemStacks.getSecondValue());
    }
    public static Pair<ItemStack[],ItemStack[]> buildRecipes (Object[] input,Object[] output){
        ItemStack[] a;
        ArrayList<ItemStack> a_=new ArrayList<>(){{

            Arrays.stream(input).forEach(
                    (obj)->{
                        ItemStack a__=resolveItem(obj);
                        this.add(a__);
                    }
            );
        }};
        a=a_.toArray(new ItemStack[a_.size()]);
        ItemStack[] b;
        ArrayList<ItemStack> b_=new ArrayList<>(){{

            Arrays.stream(output).forEach(
                    (obj)->{


                        ItemStack b__=resolveItem(obj);
                        this.add(b__);
                    }
            );

        }};
        b=b_.toArray(new ItemStack[b_.size()]);
        return new Pair<>(a,b);
    }
    public static MachineRecipe buildMachineRecipes(int time,Pair<Object[],Object[]> itemStacks){
        Pair<ItemStack[],ItemStack[]> b=buildRecipes(itemStacks);
        return new MachineRecipe(time,b.getFirstValue(),b.getSecondValue());
    }
    public static <T extends Object> List<Pair<Pair<ItemStack[],ItemStack[]>,Integer>> buildRecipeMap(List<Pair<T,Integer>> rawDataMap){
        if(rawDataMap==null)return new ArrayList<>();
        List<Pair<Pair<ItemStack[],ItemStack[]>,Integer>> map = new ArrayList<>();
        rawDataMap.forEach((p)->{
            var k=p.getFirstValue();
            var v=p.getSecondValue();
            if(k instanceof Object[]){

                map.add(new Pair<>(AddUtils.buildRecipes(
                        Arrays.copyOfRange((Object[])k,0,2),Arrays.copyOfRange((Object[])k,2,4)),v));
            }
            else if (k instanceof Pair){


                Object[] input=(Object[])((Pair)k).getFirstValue();
                if(input==null){
                    input=new Object[]{};
                }
                Object[] output=(Object[])((Pair)k).getSecondValue();
                if(output==null){
                    output=new Object[]{};
                }
                map.add(new Pair<>(AddUtils.buildRecipes(input,output),v));
            }
        });
        return map;
    }
    public static boolean copyItem(ItemStack from,ItemStack to){
        if(from==null||to==null)return false;
        to.setAmount(from.getAmount());
        to.setType(from.getType());
        to.setData(from.getData());
        return to.setItemMeta(from.getItemMeta());
    }

    public static ItemStack addLore(ItemStack item,String... lores){

        ItemStack item2=item.clone();

        ItemMeta meta=item2.getItemMeta();
        List<String> finallist=meta.hasLore() ? meta.getLore() : new ArrayList<>();
        for (String l:lores){
            finallist.add(resolveColor(l));
        }
        meta.setLore(finallist);
        item2.setItemMeta(meta);
        return item2;
    }
    public static ItemStack renameItem(ItemStack item,String name){
        ItemStack item2=item.clone();

        ItemMeta meta=item2.getItemMeta();
        meta.setDisplayName(resolveColor(name));
        item2.setItemMeta(meta);
        return item2;
    }
    public static String resolveColor(String s){
        return translateAlternateColorCodes('&', s);
    }
    public static String translateAlternateColorCodes(char altColorChar,  String textToTranslate) {
        Preconditions.checkArgument(textToTranslate != null, "Cannot translate null text");
        char[] b = textToTranslate.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRrXx".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
            }
        }

        return new String(b);
    }
    public static String formatEnergy(int energy) {
        return FORMAT.format((double)energy * SF_TPS);
    }
    public static String energyPerSecond(int energy) {
        return "&8⇨ &e⚡ &7" + formatEnergy(energy) + " J/s";
    }
    public static String speedDisplay(int multiply){
        return "&8⇨ &e⚡ &7速度: &b"+ multiply + "x";
    }
    public static String energyPerTick(int energy){
        return "&8⇨ &e⚡ &7" + FORMAT.format((double)energy) + " J/t";
    }
    public static String energyPerCraft(int energy){
        return "&8⇨ &e⚡ &7" + FORMAT.format((double)energy ) + " J 每次合成";
    }
    public static  ItemStack workBenchInfoAdd(ItemStack item,int energyBuffer,int energyConsumption){
        return AddUtils.addLore(item, LoreBuilder.powerBuffer(energyBuffer), AddUtils.energyPerCraft(energyConsumption));
    }
    public static  String tickPerGen(int time){
        return "&8⇨ &7速度: &b每 " + Integer.toString(time) + " 粘液刻生成一次";
    }

    public static ItemStack capacitorInfoAdd(ItemStack item,int energyBuffer){
        return AddUtils.addLore(item, LoreBuilder.powerBuffer(energyBuffer));
    }
    public static  ItemStack machineInfoAdd(ItemStack item,int energyBuffer,int energyConsumption){
        return machineInfoAdd(item,energyBuffer,energyConsumption, Flags.USE_SEC_EXP);
    }
    public static ItemStack machineInfoAdd(ItemStack item, int energyBuffer, int energyConsumption, Flags type){
        if(type== Flags.USE_SEC_EXP) {
            return AddUtils.addLore(item, LoreBuilder.powerBuffer(energyBuffer), AddUtils.energyPerSecond(energyConsumption));
        }
        else if(type== Flags.USE_TICK_EXP) {
            return  AddUtils.addLore( item, LoreBuilder.powerBuffer(energyBuffer), AddUtils.energyPerTick(energyConsumption));
        }
        else return null;
    }
    public static SlimefunItemStack smgInfoAdd(ItemStack item, int time){
        return (SlimefunItemStack) AddUtils.addLore( item, tickPerGen(time));
    }
    public static ItemStack advancedMachineShow(ItemStack stack,int limit){
        return AddUtils.addLore(stack,"&7机器合成进程数: %s".formatted(limit));
    }
    public static String getPercentFormat(double b){
        DecimalFormat df = new DecimalFormat("#.##");
        NumberFormat nf = NumberFormat.getPercentInstance();
        return nf.format(Double.parseDouble(df.format(b)));
    }

    /**
     * return int values in [0,length)
     * @param length
     * @return
     */
    public static int random(int length){
        return random.nextInt(length);
    }
    //generate rand in (0,1)
    public static double standardRandom(){
        return random.nextDouble();
    }
    //we supposed that u have checked these shits


    public static void forceGive(Player p, ItemStack toGive, int amount) {
        ItemStack incoming;
        int maxSize=toGive.getMaxStackSize();
        while(amount>0) {
            incoming = new ItemStack(toGive);
            int amount2=Math.min(maxSize, amount);
            incoming.setAmount(amount2);
            amount-=amount2;
            Collection<ItemStack> leftover = p.getInventory().addItem(incoming).values();
            for (ItemStack itemStack : leftover) {
                p.getWorld().dropItemNaturally(p.getLocation(), itemStack);
            }
        }
    }

    /**
     * add glowing effect to itemstack
     * no clone in this method
     * @param stack
     */

    public static ItemStack addGlow(ItemStack stack){
        //stack.addEnchantment(Enchantment.ARROW_INFINITE, 1);
        ItemMeta meta=stack.getItemMeta();
        meta.addEnchant(GLOW_EFFECT, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack hideAllFlags(ItemStack stack){
        ItemMeta meta=stack.getItemMeta();
        for (ItemFlag flag:ItemFlag.values()){
            meta.addItemFlags(flag);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack showAllFlags(ItemStack stack){
        ItemMeta meta=stack.getItemMeta();
        for (ItemFlag flag:ItemFlag.values()){
            meta.removeItemFlags(flag);
        }
        stack.setItemMeta(meta);
        return stack;
    }
    public static ItemStack setUnbreakable(ItemStack stack,boolean unbreakable){
        ItemMeta meta=stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * get a info display item to present in SF machineRecipe display
     * @param title
     * @param name
     * @return
     */
    public static ItemStack getInfoShow(String title,String... name){
        return new CustomItemStack(Material.BOOK,title,name);
    }

    /**
     * set the specific lore line in stack ,will not clone
     * @param stack
     * @param index
     * @param str
     * @return
     */
    public static ItemStack setLore(ItemStack stack,int index,String str){
        ItemMeta meta=stack.getItemMeta();
        List<String> lore=meta.getLore();
        while(index>=lore.size()){
            lore.add("");
        }
        lore.set(index,resolveColor(str));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }
    /**
     * set the total lore line in stack ,will not clone
     * @param stack
     * @param str
     * @return
     */
    public static ItemStack setLore(ItemStack stack,String... str){
        ItemMeta meta=stack.getItemMeta();
        List<String> lore=new ArrayList<>();
        int len=str.length;
        for (int i=0;i<len;++i) {
            lore.add(resolveColor(str[i]));
        }
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public static void sendMessage(CommandSender p, String msg){
        p.sendMessage(ChatColors.color(msg));
    }
    public static void sendTitle(Player p,String title,String subtitle){
        p.sendTitle(ChatColors.color(title),ChatColors.color(subtitle),-1,-1,-1);
    }
    public static ItemStack[] formatInfoRecipe(ItemStack stack,String source){
        return new ItemStack[]{
                null,stack,null,
                null,getInfoShow("&f获取方式","&7在 %s 中获取".formatted(source)),null,
                null,null,null
        };
    }
    //    public static MachineRecipe formatInfoMachineRecipe(ItemStack[] stack,int tick,String... description){
//        return MachineRecipeUtils.From(tick,new ItemStack[]{
//                getInfoShow("&f获取方式",description)
//        },stack);
//    }
    public static @Nonnull Optional<Material> getPlanks(@Nonnull Material log) {
        String materialName = log.name().replace("STRIPPED_", "");
        int endIndex = materialName.lastIndexOf('_');

        if (endIndex > 0) {
            materialName = materialName.substring(0, endIndex) + "_PLANKS";
            return Optional.ofNullable(Material.getMaterial(materialName));
        } else {
            // Fixed #3651 - Do not panic because of one weird wood type.
            return Optional.empty();
        }
    }
    public static void displayCopyString(Player player,String display,String hover,String copy){
        final TextComponent link = new TextComponent(display);
        link.setColor(ChatColor.YELLOW);
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(hover)));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD,copy));
        player.spigot().sendMessage(link);
    }
    public static void asyncWaitPlayerInput(Player player, Consumer<String> consumer){
        ChatInput.waitForPlayer(
                Slimefun.instance(),
                player,
                msg ->{
                    consumer.accept(msg);
                } );
    }
    public static ItemStack getGeneratorDisplay(boolean working,String type,int charge,int buffer){
        if(working){
            return new CustomItemStack(Material.GREEN_STAINED_GLASS_PANE,"&a发电中",
                    "&7类型:&6 %s".formatted(type),"&7&7电量: &6%s/%sJ".formatted(FORMAT.format((double)charge),FORMAT.format((double)buffer)));
        }else {
            return new CustomItemStack(Material.RED_STAINED_GLASS_PANE,"&a未发电",
                    "&7类型:&6 %s".formatted(type),"&7&7电量: &6%s/%sJ".formatted(FORMAT.format((double)charge),FORMAT.format((double)buffer)));
        }
    }
    public static String getUUID(){
        return UUID.randomUUID().toString();
    }
    public static void broadCast(String string){
        ADDON_INSTANCE.getServer().broadcastMessage(resolveColor(string));
    }
    public static ItemStack setCount(ItemStack stack,int amount){
        return new CustomItemStack(stack,amount);
    }
    public static String concat(String... strs){
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<strs.length;++i){
            sb.append(strs[i]);
        }
        return sb.toString();
    }
    public static void damageGeneric(Damageable e, double f){
        e.setHealth(Math.min( Math.max( e.getHealth()-f,0.0),e.getMaxHealth()));
    }
    public static ItemMeta setName(String name,ItemMeta meta){
        meta.setDisplayName(AddUtils.resolveColor(name));
        return meta;
    }
    public static String getDateString(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }
}
