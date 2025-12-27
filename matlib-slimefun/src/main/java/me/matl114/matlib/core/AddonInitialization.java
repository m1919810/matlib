package me.matl114.matlib.core;

import lombok.Getter;
import me.matl114.matlib.algorithms.dataStructures.frames.initBuidler.InitializeSafeProvider;
import me.matl114.matlib.common.lang.annotations.Note;
import me.matl114.matlib.implement.slimefun.core.CustomRegistries;
import me.matl114.matlib.implement.slimefun.manager.BlockDataCache;
import me.matl114.matlib.slimefunUtils.SlimefunSetup;
import me.matl114.matlib.utils.Debug;
import org.bukkit.plugin.Plugin;

@Note("Manage class marked as @AutoInit(level = \"SlimefunAddon\")")
public class AddonInitialization extends PluginInitialization {
    @Getter
    private BlockDataCache dataManager = null;

    @Getter
    private CustomRegistries registries = null;

    @Getter
    private final boolean hasSlimefun = new InitializeSafeProvider<>(Boolean.class, () -> {
                try {
                    return io.github.thebusybiscuit.slimefun4.implementation.Slimefun.instance() != null;
                } catch (Throwable e) {
                    return false;
                }
            })
            .v();

    private boolean hasGuizhanLib = true;
    /**
     * load as a Slimefun addon helper
     * @param plugin
     * @param addonName
     */
    public AddonInitialization(Plugin plugin, String addonName) {
        super(plugin, addonName);
    }

    public AddonInitialization onEnable() {
        super.onEnable();
        if (plugin != null) {
            if (hasSlimefun) {
                // inject hooks
                SlimefunSetup.init();
                // core
                this.registries = new CustomRegistries().init(plugin);
                // datas
                this.dataManager = new BlockDataCache().init(plugin);
            } else {
                throw new UnsupportedOperationException(
                        "Slimefun addon must be enabled after Slimefun plugin is enabled!");
            }
            this.hasGuizhanLib = this.plugin.getServer().getPluginManager().isPluginEnabled("GuizhanLibPlugin");
            if (this.hasGuizhanLib) {

            } else {
                Debug.warn(
                        "GuizhanLibPlugin not detected,It is recommended that you add it to soft-depends, otherwise relevant API not usable");
            }
            // tasks
        }
        return this;
    }

    public AddonInitialization onDisable() {
        super.onDisable();
        return this;
    }
}
