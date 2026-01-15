package me.matl114.matlib.implement.custom.inventory;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.*;
import me.matl114.matlib.algorithms.algorithm.FuncUtils;
import me.matl114.matlib.algorithms.algorithm.MathUtils;
import me.matl114.matlib.common.functions.core.TriFunction;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ScreenBuilder implements Screen {
    private IntList pageContentIndex;
    private Int2ReferenceMap<SlotType> slotTypeMap;
    private Int2ReferenceMap<SlotType> buttonedIndex;
    private final int sizePerPage;
    private Int2ReferenceMap<SlotProvider> basicList;
    private List<SlotProvider> pageContentList;
    private Int2ReferenceMap<SlotProvider> overrideList;
    private String screenTitle;
    // ************************* default value can be changed ***************************
    private ItemStack backgroundItem = ScreenUtils.UI_BACKGROUND;
    private ItemStack backButtonItem = ScreenUtils.BACK_BUTTON;
    private Function<InventoryBuilder, InteractHandler> backHandler =
            (i) -> InteractHandler.task(p -> goBackFrom(i, p));
    ;
    private TriFunction<Integer, Integer, Integer, ItemStack> pageSwitchProvider = ScreenUtils::getPageSwitch;
    private ToIntFunction<ScreenBuilder> maxPageProvider = (sc) -> {
        if (pageContentIndex.isEmpty()) return 1;
        return Math.max(1, (pageContentList.size() - 1) / pageContentIndex.size() + 1);
    };
    private Function<InventoryBuilder, InteractHandler> blankClickHandler = (i) -> null;
    private Function<InventoryBuilder, ScreenOpenHandler> openHandler = (i) -> null;
    private Function<InventoryBuilder, ScreenCloseHandler> closeHandler = (i) -> null;
    private ScreenHistoryStack relatedHistory;
    // *************************...
    public ScreenBuilder(ScreenTemplate screenTemplate) {
        this(screenTemplate.defaultTitle().orElse(null), screenTemplate.sizePerScreen(), screenTemplate.toList());
    }

    public ScreenBuilder(String defaultTitle, int sizePerPage, List<SlotType> pageSlotType) {
        this.screenTitle = defaultTitle;
        this.sizePerPage = sizePerPage;
        this.basicList = new Int2ReferenceArrayMap<>();
        this.pageContentList = new ArrayList<>(this.sizePerPage);
        this.overrideList = new Int2ReferenceArrayMap<>();
        int size0 = pageSlotType.size();
        this.pageContentIndex = new IntArrayList();
        this.buttonedIndex = new Int2ReferenceArrayMap<>();
        this.slotTypeMap = new Int2ReferenceOpenHashMap<>();
        SlotProvider backgroundProvider = SlotProvider.instance().withStack(() -> backgroundItem);
        SlotProvider blankProvider = SlotProvider.instance().withStack(() -> null);
        for (int i = 0; i < size0; ++i) {
            SlotType slotType = pageSlotType.get(i);
            this.slotTypeMap.put(i, slotType);
            switch (slotType) {
                case BLANK -> basicList.put(i, blankProvider);
                case BACKGROUND -> basicList.put(i, backgroundProvider);
                case PAGE_CONTENT -> pageContentIndex.add(i);
                case COMMON_BUTTON, PREV_PAGE, NEXT_PAGE, BACK_BUTTON -> buttonedIndex.put(i, slotType);
                default -> basicList.put(i, blankProvider);
            }
        }
    }

    public ScreenBuilder background(ItemStack background) {
        this.backgroundItem = background.clone();
        return this;
    }

    public ScreenBuilder maxPage(ToIntFunction<ScreenBuilder> builder) {
        this.maxPageProvider = builder;
        return this;
    }

    public ScreenBuilder pageSwitcher(TriFunction<Integer, Integer, Integer, ItemStack> pageSwitchProvider) {
        this.pageSwitchProvider = pageSwitchProvider;
        return this;
    }

    public ScreenBuilder backHandler(Function<InventoryBuilder, InteractHandler> backHandler) {
        this.backHandler = backHandler;
        return this;
    }

    private void ensureSize(int i) {
        while (this.pageContentList.size() <= i) {
            this.pageContentList.add(null);
        }
    }

    public ScreenBuilder pageContent(int i, SlotProvider provider) {
        ensureSize(i);
        this.pageContentList.set(i, provider);
        return this;
    }

    public ScreenBuilder pageContent(int i, Supplier<ItemStack> stack) {
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if (provider != null) {
            provider.withStack(stack);
        } else {
            this.pageContentList.set(i, SlotProvider.instance().withStack(stack));
        }
        return this;
    }

    public ScreenBuilder pageContent(int i, Supplier<ItemStack> stack, Supplier<InteractHandler> handlerSupplier) {
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if (provider != null) {
            provider.withStack(stack).withHandler(handlerSupplier);
        } else {
            this.pageContentList.set(i, SlotProvider.instance().withStack(stack).withHandler(handlerSupplier));
        }
        return this;
    }

    public ScreenBuilder pageContent(int i, ItemStack stack, InteractHandler handler) {
        return pageContent(i, FuncUtils.value(stack), FuncUtils.value(handler));
    }

    public ScreenBuilder pageContentHandler(int i, Supplier<InteractHandler> handlerSupplier) {
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if (provider != null) {
            provider.withHandler(handlerSupplier);
        } else {
            this.pageContentList.set(i, SlotProvider.instance().withHandler(handlerSupplier));
        }
        return this;
    }

    public ScreenBuilder pageContent(int i, UnaryOperator<SlotProvider> operator) {
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if (provider == null) provider = SlotProvider.instance();
        this.pageContentList.set(i, operator.apply(provider));
        return this;
    }

    public ScreenBuilder pageContent(SlotProvider provider) {
        this.pageContentList.add(provider);
        return this;
    }

    public ScreenBuilder pageContent(Supplier<ItemStack> itemStackSupplier) {
        return pageContent(SlotProvider.instance().withStack(itemStackSupplier));
    }

    public ScreenBuilder pageContentHandler(Supplier<InteractHandler> handlerSupplier) {
        return pageContent(SlotProvider.instance().withHandler(handlerSupplier));
    }

    public ScreenBuilder pageContent(Supplier<ItemStack> itemStackSupplier, Supplier<InteractHandler> handlerSupplier) {
        return pageContent(SlotProvider.instance().withStack(itemStackSupplier).withHandler(handlerSupplier));
    }

    public ScreenBuilder pageContent(ItemStack stack, InteractHandler handler) {
        return pageContent(FuncUtils.value(stack), FuncUtils.value(handler));
    }

    public ScreenBuilder override(int i, SlotProvider override) {
        this.overrideList.put(i, override);
        return this;
    }

    public ScreenBuilder override(int i, Supplier<ItemStack> stack) {
        this.overrideList.compute(i, (t, v) -> {
            if (v == null) {
                v = SlotProvider.instance();
            }
            v.withStack(stack);
            return v;
        });
        return this;
    }

    public ScreenBuilder overrideHandler(int i, Supplier<InteractHandler> stack) {
        this.overrideList.compute(i, (t, v) -> {
            if (v == null) {
                v = SlotProvider.instance();
            }
            v.withHandler(stack);
            return v;
        });
        return this;
    }

    public ScreenBuilder override(int i, ItemStack stack) {
        return override(i, FuncUtils.value(stack));
    }

    public ScreenBuilder override(int i, InteractHandler handler) {
        return overrideHandler(i, FuncUtils.value(handler));
    }

    public ScreenBuilder override(int i, ItemStack stack, InteractHandler handler) {
        return override(i, SlotProvider.instance().withStack(stack).withHandler(handler));
    }

    public ScreenBuilder override(int i, UnaryOperator<SlotProvider> slotProviderUnaryOperator) {
        this.overrideList.compute(i, (t, v) -> {
            if (v == null) v = SlotProvider.instance();
            return slotProviderUnaryOperator.apply(v);
        });
        return this;
    }

    public ScreenBuilder title(String title) {
        this.screenTitle = title;
        return this;
    }

    public ScreenBuilder openHandler(ScreenOpenHandler handler) {
        this.openHandler = (i) -> handler;
        return this;
    }

    public ScreenBuilder openHandler(Function<InventoryBuilder, ScreenOpenHandler> handlerFunction) {
        this.openHandler = handlerFunction;
        return this;
    }

    public ScreenBuilder closeHandler(ScreenCloseHandler handler) {
        this.closeHandler = (i) -> handler;
        return this;
    }

    public ScreenBuilder closeHandler(Function<InventoryBuilder, ScreenCloseHandler> handlerFunction) {
        this.closeHandler = handlerFunction;
        return this;
    }

    public ScreenBuilder screenClick(InteractHandler handler) {
        return screenClick((i) -> handler);
    }

    public ScreenBuilder screenClick(Function<InventoryBuilder, InteractHandler> handlerFunction) {
        this.blankClickHandler = handlerFunction;
        return this;
    }

    /**
     * set the screen related to the history,
     * will automatically change backhandler if backhandler returns a null InteractionHandler
     * will change the action of goBackFrom() and pushFrom()
     * @param stack
     * @return
     */
    @Override
    public ScreenBuilder relateToHistory(ScreenHistoryStack stack) {
        this.relatedHistory = stack;
        return this;
    }

    public <T, W extends InventoryBuilder<T>> W createInventory(
            int page0, InventoryBuilder.InventoryFactory<T, W> fact) {
        // Preconditions.checkArgument(page>= 1);
        int currentMax = this.maxPageProvider.applyAsInt(this);

        Preconditions.checkArgument(1 <= currentMax, "Max page < 1");
        int page = MathUtils.clamp(page0, 1, currentMax);
        W factory = fact.visitBuilder(this);
        factory.visitPage(this, this.screenTitle, page, this.sizePerPage, currentMax);
        for (var entry : this.basicList.int2ReferenceEntrySet()) {
            int index = entry.getIntKey();
            if (index >= 0 && index < this.sizePerPage) {
                factory.visitSlot(
                        index,
                        entry.getValue().getStack(factory),
                        entry.getValue().getHandler(factory),
                        slotTypeMap.get(index));
            }
        }
        int pageIndexRangeStart = (page - 1) * this.pageContentIndex.size();
        int pageIndexRangeEnd = pageIndexRangeStart + this.pageContentIndex.size();
        for (int i = pageIndexRangeStart; i < pageIndexRangeEnd; ++i) {
            if (i < pageContentList.size()) {
                SlotProvider provider = pageContentList.get(i);
                if (provider != null) {
                    factory.visitSlot(
                            this.pageContentIndex.getInt(i - pageIndexRangeStart),
                            provider.getStack(factory),
                            provider.getHandler(factory),
                            SlotType.PAGE_CONTENT);
                }
            }
        }
        for (var entry : buttonedIndex.int2ReferenceEntrySet()) {
            int index = entry.getIntKey();
            if (index >= 0 && index < this.sizePerPage) {
                ItemStack stack;
                InteractHandler handler = InteractHandler.EMPTY;
                switch (entry.getValue()) {
                    case PREV_PAGE:
                        stack = this.pageSwitchProvider == null
                                ? null
                                : this.pageSwitchProvider.apply(page, page - 1, currentMax);
                        if (page > 1) {
                            handler = InteractHandler.task(p -> openPage(fact, p, page - 1));
                        }
                        break;
                    case NEXT_PAGE:
                        stack = this.pageSwitchProvider == null
                                ? null
                                : this.pageSwitchProvider.apply(page, page + 1, currentMax);
                        if (page < currentMax) {
                            handler = InteractHandler.task(p -> openPage(fact, p, page + 1));
                        }
                        break;
                    case BACK_BUTTON:
                        stack = backButtonItem;
                        handler = this.backHandler.apply(factory);
                        break;
                    default:
                        continue;
                }
                factory.visitSlot(index, stack, handler, entry.getValue());
            }
        }
        for (var entry : this.overrideList.int2ReferenceEntrySet()) {
            int index = entry.getIntKey();
            if (index >= 0 && index < this.sizePerPage) {
                SlotProvider value = entry.getValue();
                if (value != null) {
                    factory.visitSlot(
                            index, value.getStack(factory), value.getHandler(factory), slotTypeMap.get(index));
                }
            }
        }
        factory.visitScreenClick(this.blankClickHandler.apply(factory));
        factory.visitOpen(this.openHandler.apply(factory));
        factory.visitClose(this.closeHandler.apply(factory));
        factory.visitEnd();
        return factory;
    }

    /**
     * invoke when player try to open this screen with history records
     */
    public void trackScreenOpen(InventoryBuilder builder, Player player) {
        trackScreenOpen(player, builder.getPage());
    }

    /**
     * invoke when player try to press go back screen
     * @param builder
     * @param player
     */
    public void goBackFrom(InventoryBuilder builder, Player player) {
        goBack(builder.getFactory(), player);
    }

    public void switchCurrentScreenPage(Player player, int page) {
        if (this.relatedHistory != null) {
            this.relatedHistory.switchTopPage(this, player, page);
        }
    }

    public void trackScreenOpen(Player player, int page) {
        if (this.relatedHistory != null) {
            this.relatedHistory.pushNew(this, player, page);
        }
    }

    public void goBack(InventoryBuilder.InventoryFactory factory, Player player) {
        if (this.relatedHistory != null) {
            if (this.relatedHistory.goBackToLast(factory, player)) {
                return;
            }
        }
        player.closeInventory();
    }

    @Override
    public int getMaxPages() {
        return this.maxPageProvider.applyAsInt(this);
    }

    @Override
    public int getPageSize() {
        return this.sizePerPage;
    }

    @Override
    public SlotType getSlotType(int idx) {
        return this.slotTypeMap.get(idx);
    }
}
