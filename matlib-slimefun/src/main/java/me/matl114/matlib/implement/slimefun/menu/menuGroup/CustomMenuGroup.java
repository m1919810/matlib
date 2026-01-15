package me.matl114.matlib.implement.slimefun.menu.menuGroup;

import com.google.common.base.Preconditions;
import io.github.thebusybiscuit.slimefun4.utils.ChestMenuUtils;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.Getter;
import me.matl114.matlib.implement.slimefun.menu.MenuUtils;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CustomMenuGroup implements IMenuGroup {
    public interface CustomMenuClickHandler {
        static CustomMenuClickHandler of(ChestMenu.MenuClickHandler handler) {
            return (cm) -> handler;
        }

        static CustomMenuClickHandler ofEmpty() {
            return (cm) -> ChestMenuUtils.getEmptyClickHandler();
        }

        public ChestMenu.MenuClickHandler getHandler(CustomMenu menu);
        // public ChestMenu.MenuClickHandler getHandler(ChestMenu menu);

    }

    public interface CustomMenuHandler {
        static CustomMenuHandler of(ChestMenu.MenuOpeningHandler handler) {
            return (cm) -> handler;
        }

        public ChestMenu.MenuOpeningHandler getHandler(CustomMenu menu);
    }

    @Getter
    private String title;

    @Getter
    private int sizePerPage;

    @Getter
    private int pages;

    @Getter
    private int[] contents = null;

    @Getter
    private boolean placeItems = false;

    private boolean placeOverrides = false;
    private boolean placePresets = false;
    // private boolean placeBackHandlers=false;
    @Getter
    private final IntSet prev = new IntArraySet();

    @Getter
    private final IntSet next = new IntArraySet();
    //    @Getter
    //    private HashSet<Integer> back=new HashSet<>();
    private boolean enablePageChangeSlot;
    //
    private ArrayList<Supplier<ItemStack>> items = new ArrayList<>();
    private ArrayList<CustomMenuClickHandler> handlers = new ArrayList<>();
    private HashMap<Integer, ItemStack> overrideItem = new HashMap<>();
    private HashMap<Integer, CustomMenuClickHandler> overrideHandler = new HashMap<>();
    private HashSet<Consumer<CustomMenu>> preload = new LinkedHashSet<>();
    private HashSet<Consumer<CustomMenu>> postload = new LinkedHashSet<>();
    private IntFunction<ChestMenu> presetGenerator = null;
    // why do we need this?
    private CustomMenuClickHandler backHandlers = null;

    public CustomMenuGroup(String title, int PageSize, int pages) {
        Preconditions.checkArgument(pages > 0, "MenuGroup page should be above 0");
        this.title = title;
        this.sizePerPage = PageSize;
        this.pages = pages;
        init();
    }

    public void init() {}

    public void validSlot(int slot) {
        Preconditions.checkArgument(
                slot >= 0 && slot < this.sizePerPage, "Not a valid slot %d for %d", slot, this.sizePerPage);
    }

    public CustomMenuGroup enableContentPlace(int[] contents) {
        Preconditions.checkNotNull(contents, "Content should not be null");
        this.placeItems = true;
        for (int i = 0; i < contents.length; i++) {
            validSlot(contents[i]);
        }
        this.contents = contents;
        return this;
    }

    public CustomMenuGroup enableOverrides() {
        this.placeOverrides = true;
        return this;
    }

    public CustomMenuGroup enablePresets(IntFunction<ChestMenu> presetGenerator) {
        Preconditions.checkNotNull(presetGenerator, "Preset should not be null");
        this.placePresets = true;
        this.presetGenerator = presetGenerator;
        return this;
    }

    public CustomMenuGroup setPageChangeSlots(int prev, int next) {
        validSlot(prev);
        validSlot(next);
        this.enablePageChangeSlot = true;
        this.prev.add(prev);
        this.next.add(next);
        return this;
    }

    public CustomMenuGroup setPagePrevSlots(int... prev) {
        Arrays.stream(prev).peek(this::validSlot).forEach(this.prev::add);
        this.enablePageChangeSlot = true;
        return this;
    }

    public CustomMenuGroup setPageNextSlots(int... next) {
        Arrays.stream(next).peek(this::validSlot).forEach(this.next::add);
        this.enablePageChangeSlot = true;
        return this;
    }
    //    public CustomMenuGroup setBackHandler(CustomMenuClickHandler handler,int... backSlot){
    //        Arrays.stream(backSlot).peek(this::validSlot).forEach(this.back::add);
    //        this.placeBackHandlers=true;
    //    }
    private void checkOverride() {
        Preconditions.checkArgument(
                this.placeOverrides, "MenuGroup should toggle placeOverride flag on before adding Overrides");
    }

    public CustomMenuGroup setOverrideItem(int slot, ItemStack stack) {
        checkOverride();
        validSlot(slot);
        this.overrideItem.put(slot, stack);
        return this;
    }

    public CustomMenuGroup setOverrideHandler(int slot, CustomMenuClickHandler handler) {
        checkOverride();
        validSlot(slot);
        this.overrideHandler.put(slot, handler);
        return this;
    }

    public CustomMenuGroup setOverrideItem(int slot, ItemStack stack, CustomMenuClickHandler handler) {
        checkOverride();
        validSlot(slot);
        setOverrideItem(slot, stack);
        setOverrideHandler(slot, handler);
        return this;
    }

    private void checkPlace() {
        Preconditions.checkArgument(
                this.placeItems, "MenuGroup should toggle placeItem flag on before adding Item List");
    }
    // return if expand
    private <T extends Object> boolean addInternal(List<? super T> list, int slot, T value) {
        if (list.size() > slot) {
            list.set(slot, value);
            return false;
        }
        while (list.size() <= slot) {
            list.add(null);
        }
        list.set(slot, value);
        return true;
    }

    public CustomMenuGroup addItem(int slot, ItemStack item) {
        checkPlace();
        if (addInternal(items, slot, () -> item)) {
            resetPageSize();
        }
        return this;
    }

    public CustomMenuGroup addItem(int slot, Supplier<ItemStack> item) {
        checkPlace();
        if (addInternal(items, slot, item)) {
            resetPageSize();
        }
        return this;
    }

    public CustomMenuGroup resetPageSize() {
        checkPlace();
        int should = (this.items.size() - 1) / this.contents.length + 1;
        if (this.pages < should) {
            this.pages = should;
        }
        return this;
    }

    public CustomMenuGroup resetItems(List<ItemStack> items) {
        checkPlace();
        this.items = items.stream()
                .map(i -> ((Supplier<ItemStack>) () -> i))
                .collect(Collectors.toCollection(ArrayList::new));
        resetPageSize();
        return this;
    }

    public CustomMenuGroup resetItemSupplier(List<Supplier<ItemStack>> items) {
        checkPlace();
        this.items = new ArrayList<>(items);
        resetPageSize();
        return this;
    }

    public CustomMenuGroup resetHandlers(List<CustomMenuClickHandler> handlers) {
        checkPlace();
        this.handlers = new ArrayList<>(handlers);
        resetPageSize();
        return this;
    }

    public CustomMenuGroup addHandler(int slot, CustomMenuClickHandler handler) {
        checkPlace();
        if (addInternal(handlers, slot, handler)) {
            resetPageSize();
        }
        return this;
    }

    public CustomMenuGroup addItem(int slot, ItemStack item, CustomMenuClickHandler handler) {
        addItem(slot, item);
        addHandler(slot, handler);
        return this;
    }

    public CustomMenuGroup addItem(int slot, Supplier<ItemStack> item, CustomMenuClickHandler handler) {
        addItem(slot, item);
        addHandler(slot, handler);
        return this;
    }

    public CustomMenuGroup addPreloadTask(Consumer<CustomMenu> menu) {
        preload.add(menu);
        return this;
    }

    public CustomMenuGroup addPostloadTask(Consumer<CustomMenu> menu) {
        postload.add(menu);
        return this;
    }

    public ChestMenu.MenuClickHandler getHandler(CustomMenuClickHandler handler, CustomMenu menu) {
        if (handler == null) {
            return ChestMenuUtils.getEmptyClickHandler();
        } else {
            return handler.getHandler(menu);
        }
    }

    public IntFunction<ChestMenu> getDefaultGenerator() {
        return (integer) -> {
            ChestMenu cmenu = new ChestMenu(this.title);
            cmenu.addItem(this.sizePerPage - 1, null);
            for (int i = 0; i < this.sizePerPage; ++i) {
                cmenu.addMenuClickHandler(i, ChestMenuUtils.getEmptyClickHandler());
            }
            return cmenu;
        };
    }

    @Override
    public int[] getContentSlots() {
        return new int[0];
    }

    @Override
    public ChestMenu buildMenuPage(int page) {
        return buildPage(page).getMenu();
    }

    public CustomMenu buildPage(int page) {
        CustomMenu menu = new CustomMenu(this, page, presetGenerator != null ? presetGenerator : getDefaultGenerator());
        loadPage(menu);
        return menu;
    }

    public CustomMenuGroup openPage(Player p, int pages) {
        CustomMenu menu = buildPage(pages);
        menu.openMenu(p);
        return this;
    }

    public CustomMenuGroup loadPage(CustomMenu menu) {
        Preconditions.checkNotNull(menu, "menu should not be null");
        Preconditions.checkArgument(
                menu.getPage() >= 1 && menu.getPage() <= this.pages,
                "Page of menu out of range! expect %d found %d",
                this.pages,
                menu.getPage());
        menu.loadInternal();
        preload.forEach(i -> i.accept(menu));
        if (this.placeItems) {
            int len = this.contents.length;
            int startIndex = Math.max(len * (menu.getPage() - 1), 0);
            int endIndex = Math.min(len * (menu.getPage()), this.items.size());
            int i = 0;
            for (; i < endIndex - startIndex; i++) {
                Supplier<ItemStack> stackSupplier = this.items.get(startIndex + i);
                menu.getMenu().replaceExistingItem(contents[i], stackSupplier == null ? null : stackSupplier.get());
                menu.getMenu().addMenuClickHandler(contents[i], getHandler(this.handlers.get(startIndex + i), menu));
            }
            for (; i < len; i++) {
                menu.getMenu().addMenuClickHandler(contents[i], ChestMenuUtils.getEmptyClickHandler());
                menu.getMenu().replaceExistingItem(contents[i], null);
            }
        }
        if (this.placeOverrides) {
            HashSet<Integer> allOverrides = new HashSet<>(overrideItem.keySet());
            allOverrides.addAll(overrideHandler.keySet());
            for (Integer slot : allOverrides) {
                if (overrideItem.containsKey(slot)) {
                    menu.getMenu().replaceExistingItem(slot, overrideItem.get(slot));
                }
                menu.getMenu().addMenuClickHandler(slot, getHandler(overrideHandler.get(slot), menu));
            }
        }
        if (this.enablePageChangeSlot) {
            this.prev.forEach(i -> {
                menu.getMenu().replaceExistingItem(i, MenuUtils.getPreviousButton(menu.getPage(), this.pages));
                menu.getMenu().addMenuClickHandler(i, ((player, i1, itemStack, clickAction) -> {
                    if (menu.getPage() > 1) {
                        this.openPage(player, menu.getPage() - 1);
                    }
                    return false;
                }));
            });
            this.next.forEach(i -> {
                menu.getMenu().replaceExistingItem(i, MenuUtils.getNextButton(menu.getPage(), this.pages));
                menu.getMenu().addMenuClickHandler(i, ((player, i1, itemStack, clickAction) -> {
                    if (menu.getPage() < pages) {
                        this.openPage(player, menu.getPage() + 1);
                    }
                    return false;
                }));
            });
        }
        //        if(this.placeBackHandlers){
        //            //remain not completed
        //        }
        //
        postload.forEach(i -> i.accept(menu));
        return this;
    }

    public static CustomMenuGroup defaultGroupTemplate(String value) {
        return new CustomMenuGroup(value, 54, 1)
                .addPreloadTask((menu) -> {
                    IntStream.range(0, 9).forEach(t -> menu.getMenu()
                            .addItem(t, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler()));
                    IntStream.range(45, 54).forEach(t -> menu.getMenu()
                            .addItem(t, ChestMenuUtils.getBackground(), ChestMenuUtils.getEmptyClickHandler()));
                })
                .setPageNextSlots(52)
                .setPagePrevSlots(46)
                .enableContentPlace(IntStream.range(9, 45).toArray());
    }
}
