package me.matl114.matlib.unitTest.autoTests.dependTests;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import com.xzavier0722.mc.plugin.slimefun4.storage.controller.SlimefunBlockData;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.DistinctiveItem;
import io.github.thebusybiscuit.slimefun4.core.services.LocalizationService;
import io.github.thebusybiscuit.slimefun4.core.services.localization.Language;
import io.github.thebusybiscuit.slimefun4.core.services.localization.LanguageFile;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.implementation.SlimefunItems;
import io.github.thebusybiscuit.slimefun4.libraries.dough.collections.Pair;
import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import java.io.File;
import java.lang.reflect.Type;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import me.matl114.matlib.algorithms.algorithm.FileUtils;
import me.matl114.matlib.algorithms.designs.serialize.JsonCodec;
import me.matl114.matlib.common.lang.exceptions.NotImplementedYet;
import me.matl114.matlib.core.bukkit.schedule.ScheduleManager;
import me.matl114.matlib.core.slimefun.manager.BlockDataCache;
import me.matl114.matlib.nmsMirror.impl.*;
import me.matl114.matlib.unitTest.MatlibTest;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.ItemStackCounter;
import me.matl114.matlib.utils.ItemStackWrapper;
import me.matl114.matlib.utils.inventory.itemStacks.CleanItemStack;
import me.matl114.matlib.utils.reflect.wrapper.FieldAccess;
import me.matl114.matlib.utils.reflect.wrapper.MethodAccess;
import me.matl114.matlib.utils.version.Version;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenuPreset;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class SlimefunTests implements TestCase {
    //  @OnlineTest(name = "Slimefun blockData test")
    public void test_blockDataTest() {
        SlimefunItem testItem = SlimefunItem.getByItem(SlimefunItems.ELECTRIC_ORE_GRINDER_3);
        World testWorld = Bukkit.getWorlds().get(0);
        Location location = new Location(testWorld, 0, 1, 0);
        BlockDataCache.getManager().removeBlockData(location);
        SlimefunBlockData data = BlockDataCache.getManager().createBlockData(location, testItem);
        Debug.logger(data.isDataLoaded());
        BlockDataCache.getManager().setCustomString(location, "not", "ok");
        Debug.logger(data.getData("not"));
        Config cfg = BlockStorage.getLocationInfo(location);
        Debug.logger(cfg.getClass());
        cfg.setValue("not", null);
        Debug.logger(data.getData("not"));
    }
    //   @OnlineTest(name = "Slimefun BlockStorage test")
    public void test_blockStorageTest() {
        SlimefunItem testItem = SlimefunItem.getByItem(SlimefunItems.ELECTRIC_ORE_GRINDER_3);
        World testWorld = Bukkit.getWorlds().get(0);
        Location location = new Location(testWorld, 3, 7, 2);
        BlockDataCache.getManager().removeBlockData(location);
        SlimefunBlockData data = BlockDataCache.getManager().createBlockData(location, testItem);
        Debug.logger(data.isDataLoaded());
        AtomicBoolean flag = new AtomicBoolean(false);
        ScheduleManager.getManager()
                .launchScheduled(
                        () -> {
                            boolean flag1 = flag.get();
                            BlockStorage.addBlockInfo(location, "test", flag1 ? "ok" : null);
                            flag.set(!flag1);
                        },
                        10,
                        false,
                        1);
        Debug.logger("launched Machine-BlockStorage-behaviour Simulation Thread");
    }
    //  @OnlineTest(name = "sf block menu test")
    public void test_blockMenu() {
        BlockMenuPreset preset = new BlockMenuPreset("BYD", "bydbyd") {
            @Override
            public void init() {
                this.addItem(36, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler());
            }

            @Override
            public boolean canOpen(@NotNull Block block, @NotNull Player player) {
                return true;
            }

            @Override
            public int[] getSlotsAccessedByItemTransport(ItemTransportFlow itemTransportFlow) {
                return IntStream.range(0, 37).toArray();
            }
        };
        BlockMenu menu = new BlockMenu(preset, new Location(testWorld(), 3, 64, 3));
        Debug.logger(menu.getInventory().getSize());
        menu.addMenuClickHandler(-1, ChestMenuUtils.getEmptyClickHandler());
        Debug.logger(menu.getInventory().getSize());
    }
    // @OnlineTest(name = "distinctive test")
    public void test_distinctive() {
        for (SlimefunItem item : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (item instanceof DistinctiveItem
                    && (item.getItem().getType() == Material.PLAYER_HEAD
                            || item.getItem().getType() == Material.SUGAR)) {
                Debug.logger(item);
            }
        }
    }

    protected static char[] GCE_GENE_DISPLAY_L = new char[] {'b', 'c', 'd', 'f', 's', 'w'};
    protected static char[] GCE_GENE_DISPLAY_U = new char[] {'B', 'C', 'D', 'F', 'S', 'W'};
    // @OnlineTest(name = "Slimefun Gce test")
    public void test_GceTest() throws Throwable {
        HashMap<String, String> dna2Id = new HashMap<>();
        HashMap<String, String> dna2Name = new HashMap<>();
        Class utilClass = Class.forName("net.guizhanss.gcereborn.utils.ChickenUtils");

        MethodAccess<ItemStack> access = MethodAccess.reflect(
                "getResource",
                utilClass,
                ItemStack.class); // ofName(utilClass,"getResource",ItemStack.class).initWithNull().printError(true);
        for (SlimefunItem items : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (items.getId().startsWith("GCE_") && items.getId().endsWith("_CHICKEN_ICON")) {
                String materialId =
                        items.getId().substring("GCE_".length(), items.getId().length() - "_CHICKEN_ICON".length());
                NamespacedKey key = new NamespacedKey("geneticchickengineering", "gce_pocket_chicken_dna");
                ItemStack realChicken = items.getRecipe()[4];
                PersistentDataContainer container = realChicken.getItemMeta().getPersistentDataContainer();
                int[] dna = container.get(key, PersistentDataType.INTEGER_ARRAY);

                StringBuilder dnaSequence = new StringBuilder();
                for (int i = 0; i < 6; i++) {
                    if (dna[i] == 0) {
                        dnaSequence.append(GCE_GENE_DISPLAY_L[i]).append(GCE_GENE_DISPLAY_L[i]);
                    } else {
                        dnaSequence.append(GCE_GENE_DISPLAY_U[i]).append(GCE_GENE_DISPLAY_U[i]);
                    }
                }
                String dnat = dnaSequence.toString();
                Debug.logger(dnat);
                ItemStack stack = access.invoke(null, realChicken);
                dna2Id.put(
                        dnat,
                        stack.getType().toString().toLowerCase(Locale.ROOT) + "|"
                                + Slimefun.getItemDataService()
                                        .getItemData(stack)
                                        .orElse("null"));
                dna2Name.put(dnat, materialId.toLowerCase(Locale.ROOT) + "_chicken");
            }
        }
        Debug.logger(dna2Id);
        Debug.logger(dna2Name);
    }

    private static String getItemFormat(ItemStack item) {
        String mat = item.getType().toString();
        if (item.getType() == Material.PLAYER_HEAD && item.getItemMeta() instanceof SkullMeta meta1) {
            URL t = meta1.getOwnerProfile().getTextures().getSkin();
            String path = t.getPath();
            String[] parts = path.split("/");
            mat += "$" + parts[parts.length - 1];
        }
        return mat;
    }
    // @OnlineTest(name = "Slimefun cultivation test")
    public void test_cultivation() throws Throwable {
        //        HashMap<String, RandomizedSet<ItemStack>> id2Result = new HashMap<>();
        //        for(SlimefunItem item: Slimefun.getRegistry().getAllSlimefunItems()){
        //            if(item.getClass().getSimpleName().equals("HarvestableBush")){
        //                id2Result.put(item.getId(),(RandomizedSet<ItemStack>)
        // FieldAccess.ofName("harvestItems").noSnapShot().getValue(item)) ;
        //            }
        //        }
        //        Map<String, List<Pair<String,String>>> id2ResultStackList =
        // id2Result.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,(entry)->{
        //            RandomizedSet<ItemStack> item = entry.getValue();
        //            List<Pair<String,String>> lst = ((Set<WeightedNode<ItemStack>>)
        // FieldAccess.ofName(RandomizedSet.class,"internalSet").ofAccess(item).getRaw()).stream().map(WeightedNode::getObject).map(i->{
        //                return new
        // Pair<>(getItemFormat(i),Slimefun.getItemDataService().getItemData(i).orElse("null"));
        //            }).toList();
        //            return lst;
        //
        //        }));
        //        Debug.logger(id2ResultStackList);
        //
        HashMap<String, List<Pair<String, String>>> tree2Product = new HashMap<>();
        for (SlimefunItem item : Slimefun.getRegistry().getAllSlimefunItems()) {
            if (item.getClass().getSimpleName().equals("CultivationTree")) {
                SlimefunItem product = (SlimefunItem)
                        FieldAccess.ofName("produce").ofAccess(item).getRaw();
                tree2Product.put(item.getId(), List.of(new Pair<>(getItemFormat(product.getItem()), product.getId())));
            }
        }
        Debug.logger(tree2Product);
    }
    // @OnlineTest(name = "Slimefun id name test")
    public void test_slimefunitemid() throws Throwable {
        HashMap<String, me.matl114.matlib.algorithms.dataStructures.struct.Pair<String, String>> map =
                new LinkedHashMap<>();
        for (SlimefunItem item : Slimefun.getRegistry().getAllSlimefunItems()) {
            ItemStack item1 = item.getItem();
            var meta = item1.getItemMeta();
            if (meta != null && meta.getDisplayName() != null) {
                map.put(
                        item.getId(),
                        me.matl114.matlib.algorithms.dataStructures.struct.Pair.of(
                                ChatColor.stripColor(meta.getDisplayName()),
                                item.getAddon().getName()));
            }
        }
        Debug.logger(map);
    }

    private Random random = new Random();

    private ItemStack getItem(RecipeType recipeType) throws Throwable {
        ItemStack item = recipeType.toItem();
        if (item == null) {
            return null;
        } else {
            Language language = Slimefun.getLocalization().getDefaultLanguage();
            NamespacedKey key = recipeType.getKey();
            return new CustomItemStack(item, (meta) -> {
                LanguageFile var10002 = LanguageFile.RECIPES;
                String var10003 = key.getNamespace();
                String displayName = null;
                try {
                    displayName = (String) MethodAccess.ofName(LocalizationService.class, "getStringOrNull")
                            .invoke(
                                    Slimefun.getLocalization(),
                                    language,
                                    var10002,
                                    var10003 + "." + key.getKey() + ".name");
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                if (displayName != null) {
                    meta.setDisplayName(ChatColor.AQUA + displayName);
                }
                var10002 = LanguageFile.RECIPES;
                var10003 = key.getNamespace();
                List<String> lore = null;
                try {
                    lore = (List<String>) MethodAccess.ofName(LocalizationService.class, "getStringListOrNull")
                            .invoke(
                                    Slimefun.getLocalization(),
                                    language,
                                    var10002,
                                    var10003 + "." + key.getKey() + ".lore");
                } catch (Throwable e) {
                    throw new RuntimeException(e);
                }
                if (lore != null) {
                    lore.replaceAll((line) -> {
                        return ChatColor.GRAY + line;
                    });
                    meta.setLore(lore);
                }

                meta.addItemFlags(new ItemFlag[] {ItemFlag.HIDE_ATTRIBUTES});
                meta.addItemFlags(new ItemFlag[] {ItemFlag.HIDE_ENCHANTS});
            });
        }
    }

    // @OnlineTest(name = "Slimefun recipe export test")
    public void test_exportrecipes() throws Throwable {
        JsonCodec<ItemStackWrapper> itemSample = Version.isDataComponentVersion()
                ? ItemStackWrapper.JSON_CODEC
                : new JsonCodec<ItemStackWrapper>() {
                    @Override
                    public ItemStackWrapper deserialize(
                            JsonElement json, Type typeOfT, JsonDeserializationContext context)
                            throws JsonParseException {
                        throw new NotImplementedYet();
                    }

                    @Override
                    public JsonElement serialize(
                            ItemStackWrapper src, Type typeOfSrc, JsonSerializationContext context) {
                        if (src == ItemStackWrapper.EMPTY) {
                            return JsonNull.INSTANCE;
                        }
                        JsonObject jsonMap = new JsonObject();
                        NamespacedKey key = null;
                        ItemStack stack = src.toBukkit();
                        if (stack != null) {
                            key = stack.getType().getKey();
                        }
                        jsonMap.addProperty("id", key == null ? "minecraft:air" : key.toString());
                        if (src.hasMeta())
                            jsonMap.addProperty(
                                    "nbt",
                                    NMSCore.COMPOUND_TAG.getAsString((NMSItem.ITEMSTACK)
                                            .getCustomedNbtView(src.getNMS(), true)
                                            .getView()));
                        return jsonMap;
                    }
                };
        Map<ItemStackWrapper, String> nmsMap = new HashMap<>();
        class CustomItemIdLookup {
            public String getOrAdd(ItemStackWrapper stack) {
                if (stack.hasMeta()) {
                    return nmsMap.computeIfAbsent(stack, (s) -> {
                        String newName;
                        do {
                            newName = "customitems:" + random.nextInt(1145141919);
                        } while (nmsMap.containsValue(newName));
                        return newName;
                    });
                } else {
                    return "minecraft:" + stack.toBukkit().getType().toString().toLowerCase(Locale.ROOT);
                }
            }
        }
        var tool = new CustomItemIdLookup();

        record CraftingType(String id, ItemStackCounter icon) {
            public static final ItemStackCounter ITEM_NULL_TYPE =
                    ItemStackCounter.of(new CleanItemStack(Material.BARRIER, "&c无"));
            public static final CraftingType EMPTY = new CraftingType("NULL", ITEM_NULL_TYPE);
        }
        JsonCodec<CraftingType> TYPE_CODEC = new JsonCodec<CraftingType>() {
            @Override
            public JsonElement serialize(CraftingType src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("rid", (String) src.id);
                jsonObject.add("icon", context.serialize(src.icon));
                return jsonObject;
            }

            @Override
            public CraftingType deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                JsonObject jsonObject = json.getAsJsonObject();
                String rid = jsonObject.getAsJsonPrimitive("rid").getAsString();
                ItemStackCounter icon = context.deserialize(jsonObject.getAsJsonObject("icon"), ItemStackCounter.class);
                return new CraftingType(rid, icon);
            }
        };

        record SlimefunRecipeEntry(
                String rid, String id, ItemStackCounter[] ingredientEntry, ItemStackCounter output) {}

        JsonCodec<ItemStackCounter> ITEM_CODEC = new JsonCodec<ItemStackCounter>() {
            @Override
            public ItemStackCounter deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                throw new NotImplementedYet();
            }

            @Override
            public JsonElement serialize(ItemStackCounter src, Type typeOfSrc, JsonSerializationContext context) {
                if (src.isAir()) {
                    return JsonNull.INSTANCE;
                }

                String typeid = tool.getOrAdd(src.newWrapper());
                var json = new JsonObject();
                json.addProperty("typeid", typeid);
                json.addProperty("amount", src.getAmount());
                return json;
            }
        };
        JsonCodec<SlimefunRecipeEntry> ENTRY_CODED = new JsonCodec<SlimefunRecipeEntry>() {
            @Override
            public SlimefunRecipeEntry deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                    throws JsonParseException {
                JsonObject data = json.getAsJsonObject();
                String rid = data.getAsJsonPrimitive("rid").getAsString();
                String id = data.getAsJsonPrimitive("id").getAsString();
                ItemStackCounter output = context.deserialize(data.getAsJsonObject("output"), ItemStackWrapper.class);
                ItemStackCounter[] ingredient =
                        context.deserialize(data.getAsJsonArray("ingredient"), ItemStackCounter[].class);
                ItemStackCounter[] ingredientEntry = new ItemStackCounter[ingredient.length];
                return new SlimefunRecipeEntry(rid, id, ingredientEntry, output);
            }

            @Override
            public JsonElement serialize(SlimefunRecipeEntry src, Type typeOfSrc, JsonSerializationContext context) {
                JsonObject data = new JsonObject();
                data.addProperty("rid", (String) src.rid);
                data.addProperty("id", (String) src.id);
                data.add("output", context.serialize((ItemStackCounter) src.output));
                data.add("ingredient", context.serialize((ItemStackCounter[]) src.ingredientEntry));
                return data;
            }
        };
        Type DATA_MAP_TYPE = new TypeToken<Map<String, ItemStackWrapper>>() {}.getType();
        Type TYPE_MAP_TYPE = new TypeToken<Map<String, CraftingType>>() {}.getType();
        Type RECIPE_MAP_TYPE = new TypeToken<Map<String, SlimefunRecipeEntry>>() {}.getType();
        Gson RECIPES_JSON_CODEC = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(ItemStackWrapper.class, itemSample)
                .registerTypeAdapter(ItemStackCounter.class, ITEM_CODEC)
                .registerTypeAdapter(CraftingType.class, TYPE_CODEC)
                .registerTypeAdapter(SlimefunRecipeEntry.class, ENTRY_CODED)
                .create();
        Map<String, CraftingType> ALL_RECIPE_TYPE = new HashMap<>();
        Map<String, SlimefunRecipeEntry> ALL_RECIPE_ENTRY = new HashMap<>();
        loop:
        for (var entry : Slimefun.getRegistry().getAllSlimefunItems()) {
            ItemStack output = entry.getRecipeOutput();
            Optional<String> id = Slimefun.getItemDataService().getItemData(output.getItemMeta());
            if (!id.isPresent()) continue;
            ItemStack[] recipes = entry.getRecipe();
            RecipeType type = entry.getRecipeType();
            ItemStack recipeIcon = getItem(type);
            String recipeTypeName = (recipeIcon == null)
                    ? "NULL_RECIPE"
                    : ChatColor.stripColor(recipeIcon.getItemMeta().getDisplayName());
            if (!ALL_RECIPE_TYPE.containsKey(recipeTypeName)) {
                ItemStackCounter icon =
                        recipeIcon == null ? CraftingType.ITEM_NULL_TYPE : ItemStackCounter.of(recipeIcon);
                icon = icon.copy();
                icon.setAmount(1);
                ALL_RECIPE_TYPE.put(recipeTypeName, new CraftingType(recipeTypeName, icon));
            }
            ItemStackCounter[] recipeEntrys = new ItemStackCounter[9];
            for (var i = 0; i < 9; ++i) {
                recipeEntrys[i] = ItemStackCounter.of(recipes[i]);
            }
            SlimefunRecipeEntry entry1 =
                    new SlimefunRecipeEntry(recipeTypeName, id.get(), recipeEntrys, ItemStackCounter.of(output));
            ALL_RECIPE_ENTRY.put(id.get(), entry1);
        }
        String craftingType = RECIPES_JSON_CODEC.toJson(ALL_RECIPE_TYPE);
        save("craft-types.json", craftingType);
        String recipeData = RECIPES_JSON_CODEC.toJson(ALL_RECIPE_ENTRY);
        save("recipe-data.json", recipeData);
        Map<String, ItemStackWrapper> inverseMap = new HashMap<>();
        nmsMap.forEach((k, v) -> inverseMap.put(v, k));
        String itemData = RECIPES_JSON_CODEC.toJson(inverseMap);
        save("item-database.json", itemData);
        Debug.logger("save finish");
    }

    private void save(String name, String data) throws Throwable {
        File parentFile = MatlibTest.getInstance().getDataFolder();
        File f1 = FileUtils.getOrCreateFile(new File(parentFile, name));
        Files.writeString(f1.toPath(), data);
    }

    @OnlineTest(name = "slimefun research test")
    public void test_researchs() throws Throwable {

        for (var sf : Slimefun.getRegistry().getAllSlimefunItems()) {
            if ((Objects.equals(sf.getItemGroup().getKey(), new NamespacedKey(Slimefun.instance(), "basic_machines"))
                            || Objects.equals(
                                    sf.getItemGroup().getKey(), new NamespacedKey(Slimefun.instance(), "magical_armor"))
                            || Objects.equals(
                                    sf.getItemGroup().getKey(), new NamespacedKey(Slimefun.instance(), "tools")))
                    && sf.hasResearch()) {
                Slimefun.getResearchCfg()
                        .setValue(
                                sf.getResearch().getKey().getNamespace() + "."
                                        + sf.getResearch().getKey().getKey() + ".enabled",
                                Boolean.FALSE);
            }
        }
        Slimefun.getResearchCfg().save();
    }
}
