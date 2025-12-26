package me.matl114.matlib.implement.custom.inventory;

import javax.annotation.Nullable;
import me.matl114.matlib.common.lang.annotations.DoNotOverride;
import me.matl114.matlib.common.lang.annotations.Internal;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface InventoryBuilder<T> {
    /**
     * basic information setup
     * @param builder
     * @param optionalTitle
     * @param pageIndex
     * @param sizePerPage
     * @param currentMaxPage
     */
    void visitPage(
            ScreenBuilder builder, @Nullable String optionalTitle, int pageIndex, int sizePerPage, int currentMaxPage);

    /**
     * set slot content
     * @param index
     * @param stack
     * @param handler
     */
    void visitSlot(int index, @Nullable ItemStack stack, @Nullable InteractHandler handler);

    /**
     * end of any
     */
    void visitEnd();

    /**
     * set open handler
     * @param handler
     */
    void visitOpen(@Nullable ScreenOpenHandler handler);

    /**
     * set close handler
     * @param handler
     */
    void visitClose(@Nullable ScreenCloseHandler handler);

    /**
     * set the handler of player click any place outside the fucking inventory slots
     */
    void visitScreenClick(InteractHandler handler);

    /**
     * must return the instance passed in the "visitPage()"
     * @return
     */
    ScreenBuilder getBuilder();

    /**
     * must return the value passed in the "visitPage()"
     * @return
     */
    int getPage();

    /**
     * must be present after visitEnd
     * @return
     */
    T getResult();

    /**
     * may different from player.openInventory
     * @param player
     */
    @Internal
    public void openInternal(Player player);

    @DoNotOverride
    default void open(Player player) {
        openInternal(player);
        this.getBuilder().switchCurrentScreenPage(player, this.getPage());
    }

    @DoNotOverride
    default void openWithHistory(Player player) {
        this.getBuilder().trackScreenOpen(this, player);
        open(player);
    }

    /**
     * must be present after visitEnd
     * @return
     */
    Inventory getInventory();

    /**
     * factory used for creating this
     * @return
     */
    InventoryFactory<T, ? extends InventoryBuilder<T>> getFactory();

    public interface InventoryFactory<T, W extends InventoryBuilder<T>> {
        W visitBuilder(ScreenBuilder builder);
    }
}
