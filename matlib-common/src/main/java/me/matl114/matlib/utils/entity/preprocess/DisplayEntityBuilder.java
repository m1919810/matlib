package me.matl114.matlib.utils.entity.preprocess;

import net.kyori.adventure.text.TextComponent;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Transformation;

public class DisplayEntityBuilder extends EntityBuilder<Display> {
    protected DisplayEntityBuilder(Class<? extends Display> entityClass) {
        super(entityClass);
    }

    public static DisplayEntityBuilder ofBlock(Material block) {
        return ofBlock(block.createBlockData());
    }

    public static DisplayEntityBuilder ofBlock(BlockData blockData) {
        return new DisplayEntityBuilder(BlockDisplay.class)
                .with(d -> ((BlockDisplay) d).setBlock(blockData))
                .cast();
    }

    public static DisplayEntityBuilder ofItem(ItemStack item) {
        return new DisplayEntityBuilder(ItemDisplay.class)
                .with(d -> ((ItemDisplay) d).setItemStack(item))
                .cast();
    }

    public static DisplayEntityBuilder ofText(TextComponent text) {
        return new DisplayEntityBuilder(TextDisplay.class)
                .with(t -> ((TextDisplay) t).text(text))
                .cast();
    }

    public DisplayEntityBuilder withTrans(Transformation transformation) {
        addProcessorInternal(d -> d.setTransformation(transformation));
        return this;
    }

    public DisplayEntityBuilder withGlowColor(Color color) {
        addProcessorInternal(d -> d.setGlowColorOverride(color));
        return this;
    }
}
