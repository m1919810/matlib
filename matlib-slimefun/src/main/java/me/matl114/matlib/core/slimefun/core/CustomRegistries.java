package me.matl114.matlib.core.slimefun.core;

import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Predicate;
import lombok.Getter;
import me.matl114.matlib.core.AutoInit;
import me.matl114.matlib.core.Manager;
import me.matl114.matlib.utils.Debug;
import org.bukkit.plugin.Plugin;

@AutoInit(level = "SlimefunAddon")
public class CustomRegistries implements Manager {
    Plugin plugin;

    @Getter
    static CustomRegistries manager;

    public CustomRegistries() {
        manager = this;
    }

    HashSet<Registry<?>> registries = new HashSet<>();

    private class Registry<T extends Object> {
        private Class<T> clazz;
        private boolean lock = false;

        public Registry(Class<T> clazz) {
            this.clazz = clazz;
        }

        private Predicate<T> registerPredicate;

        public Registry<T> predicate(Predicate<T> registerPredicate) {
            this.registerPredicate = registerPredicate;
            return this;
        }

        private HashMap<String, T> map = new HashMap<>();

        public T get(String key) {
            return map.get(key);
        }

        public T remove(String key) {
            return map.remove(key);
        }

        public void register(String key, T value) {
            Preconditions.checkArgument(!lock, "Registry is locked!");
            map.put(key, value);
        }

        public void tryPut(String key, Object val) {
            if (!lock && clazz.isInstance(val)) {
                try {
                    T value = (T) clazz.cast(val);
                    if (registerPredicate.test(value)) {
                        register(key, value);
                    }
                } catch (Throwable e) {
                    Debug.logger(
                            "Error while trying to register " + key + ":" + val, "Error message: " + e.getMessage());
                }
            }
        }

        protected Registry<T> register() {
            CustomRegistries.this.registries.add(this);
            return this;
        }
    }

    protected HashMap<String, RecipeType> recipeTypes = new HashMap<>();
    protected HashMap<String, CustomRegistries> customRecipetypes = new HashMap<>();

    protected void registerInternal(Object key, Object val) {
        String keyString = key instanceof String ? (String) key : key.toString();
        registries.forEach(r -> {
            r.tryPut(keyString, val);
        });
    }

    public <W extends Object> W getInternal(String key, Class<W> clazz) {
        Registry<W> re = (Registry<W>)
                registries.stream().filter(r -> r.clazz == clazz).findFirst().orElse(null);
        if (re == null) {
            return null;
        } else {
            return re.get(key);
        }
    }

    public <W extends Object> boolean removeInternal(String key, Class<W> clazz) {
        Registry<?> re =
                registries.stream().filter(r -> r.clazz == clazz).findFirst().orElse(null);
        if (re != null) {
            return re.remove(key) != null;
        }
        return false;
    }

    @Override
    public CustomRegistries init(Plugin pl, String... path) {
        this.plugin = pl;
        this.addToRegistry();
        return this;
    }

    @Override
    public CustomRegistries reload() {
        return this;
    }

    @Override
    public boolean isAutoDisable() {
        return true;
    }

    @Override
    public void deconstruct() {
        manager = null;
        this.removeFromRegistry();
    }

    private Registry<CustomRecipeType> customRecipeType =
            new Registry<>(CustomRecipeType.class).predicate((val) -> true).register();
    private Registry<CustomSlimefunItem> customSlimefunItem =
            new Registry<>(CustomSlimefunItem.class).predicate((val) -> true).register();
}
