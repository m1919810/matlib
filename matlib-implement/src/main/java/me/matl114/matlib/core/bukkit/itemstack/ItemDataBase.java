package me.matl114.matlib.core.bukkit.itemstack;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import me.matl114.matlib.algorithms.designs.concurrency.AsyncWorker;
import me.matl114.matlib.common.lang.annotations.NeedTest;
import me.matl114.matlib.core.Manager;
import me.matl114.matlib.implement.serialization.ItemStackSource;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

@NeedTest
public class ItemDataBase implements Manager {
    public ItemDataBase() {}

    private Plugin plugin;
    private String rootPath;
    private String dataBaseType;
    private String dataBaseserializationType;
    private Supplier<Map<ItemStack, String>> fastItemIdLookupFactory;
    private Map<String, ItemStackSource> items;
    private Set<String> unsavedItems;

    SaveFileType saveType;

    public enum SaveFileType {
        YAML(".yml"),
        JSON(".json");
        protected final String extensionName;

        SaveFileType(String extensionName) {
            this.extensionName = extensionName;
        }
    }
    //    SaveSerializationType serializationType;
    //    public enum SaveSerializationType {
    //        BUKKIT_MAP(RecordCodecBuilder.mapCodec(
    //            instance -> instance.group(
    //
    //            )
    //        )),
    //        @Experimental
    //        BUKKIT(),
    //        SNBT(),
    //        @Experimental
    //        NBT_MAP();
    //
    //        public MapCodec<ItemStack> codec;
    //
    //        SaveSerializationType(MapCodec<ItemStack> codec){
    //            this.codec = codec;
    //        }
    //    }

    private boolean registered = false;
    private AsyncWorker readWriteWorker;

    @Override
    public boolean isAutoDisable() {
        return false;
    }

    @Override
    public ItemDataBase init(Plugin pl, String... path) {
        this.plugin = pl;
        this.readWriteWorker = AsyncWorker.bindToSingleThread(128);
        this.rootPath = path[0];
        this.dataBaseType = path.length < 2 ? "yml" : path[1];
        this.dataBaseserializationType = path.length < 3 ? "bukkit" : path[2];
        this.saveType = switch (dataBaseType.toLowerCase()) {
            case "yaml", "yml" -> SaveFileType.YAML;
            case "json" -> SaveFileType.JSON;
            default -> throw new IllegalArgumentException("Unknown main save type: " + dataBaseType);};
        // this.serializationType =
        // Objects.requireNonNull(SaveSerializationType.valueOf(this.dataBaseserializationType.toUpperCase(Locale.ROOT)));
        this.items = new LinkedHashMap<String, ItemStackSource>();
        registerFunctional();
        return this;
    }

    private ItemDataBase registerFunctional() {
        Preconditions.checkState(!registered, "ItemDataBase functional have already been registered!");
        this.registered = true;
        return this;
    }

    private ItemDataBase unregisterFunctional() {
        Preconditions.checkState(registered, "ItemDataBase functional haven't been registered!");
        this.registered = false;
        return this;
    }

    @Override
    public ItemDataBase reload() {
        deconstruct();
        return null;
    }

    @Override
    public void deconstruct() {
        unregisterFunctional();
        this.items = null;
        this.saveType = null;
        this.readWriteWorker.shutdown(10, TimeUnit.SECONDS);
        this.readWriteWorker = null;
        this.plugin = null;
    }

    public void readDatabase() {
        this.readWriteWorker.execute(this::readDatabaseAsync);
    }

    public void readDatabaseAsync() {
        File file = new File(rootPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        File[] files = file.listFiles();
        if (files != null) {
            Arrays.sort(files, Comparator.comparingLong(File::lastModified));
            for (File f : files) {
                if (f.exists() && f.isFile()) {
                    String name = f.toPath().toString();
                    SaveFileType type;
                    if (name.endsWith(".yml") || name.endsWith(".yaml")) {
                        type = SaveFileType.YAML;
                    } else if (name.endsWith(".json")) {
                        type = SaveFileType.JSON;
                    }
                }
            }
        }
    }
}
