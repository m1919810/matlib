package me.matl114.matlib.implement.custom.inventory;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ReferenceArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ReferenceMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import me.matl114.matlib.common.functions.FuncUtils;
import me.matl114.matlib.common.functions.core.TriFunction;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.*;

public abstract class ScreenBuilder {
    private IntList pageContentIndex;
    private Int2ReferenceMap<SlotType> buttonedIndex;
    private final int sizePerPage;
    private Int2ReferenceMap<SlotProvider> basicList;
    private List<SlotProvider> pageContentList;
    private Int2ReferenceMap<SlotProvider> overrideList;
    //************************* default value can be changed ***************************
    private ItemStack backgroundItem = ScreenUtils.UI_BACKGROUND;
    private ItemStack backButtonItem = ScreenUtils.BACK_BUTTON;
    private Function<InventoryFactory.InventoryBuilder, InteractHandler> backHandler = (i)->InteractHandler.task(p->{
        p.closeInventory();
        return false;
    });;
    private TriFunction<Integer, Integer, Integer, ItemStack> pageSwitchProvider = ScreenUtils::getPageSwitch;
    private ToIntFunction<ScreenBuilder> maxPageProvider = (sc)->{
        if(pageContentIndex.isEmpty())return 1;
        return Math.max(1, (pageContentList.size() - 1)/pageContentIndex.size() +1);
    };
    //*************************...
    public ScreenBuilder(int sizePerPage,List<SlotType> pageSlotType){
        this.sizePerPage = sizePerPage;
        this.basicList = new Int2ReferenceArrayMap<>();
        this.pageContentList = new ArrayList<>(this.sizePerPage);
        this.overrideList = new Int2ReferenceArrayMap<>();
        int size0 = pageSlotType.size();
        this.pageContentIndex = new IntArrayList();
        this.buttonedIndex = new Int2ReferenceArrayMap<>();
        SlotProvider backgroundProvider = SlotProvider.instance().withStack(()->backgroundItem);
        SlotProvider blankProvider = SlotProvider.instance().withStack(()->null);
        for (int i = 0 ; i< size0; ++i){
            SlotType slotType = pageSlotType.get(i);
            switch (slotType){
                case BLANK -> basicList.put(i, blankProvider);
                case BACKGROUND -> basicList.put(i, backgroundProvider);
                case PAGE_CONTENT -> pageContentIndex.add(i);
                case COMMON_BUTTON,PREV_PAGE,NEXT_PAGE,BACK_BUTTON -> buttonedIndex.put(i, slotType);
                default -> basicList.put(i, blankProvider);
            }
        }

    }
    public ScreenBuilder background(ItemStack background){
       this.backgroundItem = background.clone();
       return this;
    }

    public ScreenBuilder maxPage(ToIntFunction<ScreenBuilder> builder){
        this.maxPageProvider = builder;
        return this;
    }

    public ScreenBuilder pageSwitcher(TriFunction<Integer, Integer, Integer, ItemStack> pageSwitchProvider){
        this.pageSwitchProvider = pageSwitchProvider;
        return this;
    }

    public ScreenBuilder backHandler(Function<InventoryFactory.InventoryBuilder, InteractHandler> backHandler){
        this.backHandler = backHandler;
        return this;
    }
    private void ensureSize(int i){
        while (this.pageContentList.size() <= i)
        {
            this.pageContentList.add(null);
        }
    }
    public ScreenBuilder pageContent(int i, SlotProvider provider){
        ensureSize(i);
        this.pageContentList.set(i, provider);
        return this;
    }

    public ScreenBuilder pageContent(int i, Supplier<ItemStack> stack){
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if(provider != null){
            provider.withStack(stack);
        }else {
            this.pageContentList.set(i, SlotProvider.instance().withStack(stack));
        }
        return this;
    }
    public ScreenBuilder pageContent(int i, Supplier<ItemStack> stack, Supplier<InteractHandler> handlerSupplier){
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if(provider != null){
            provider.withStack(stack).withHandler(handlerSupplier);
        }else {
            this.pageContentList.set(i, SlotProvider.instance().withStack(stack).withHandler(handlerSupplier));
        }
        return this;
    }
    public ScreenBuilder pageContent(int i, ItemStack stack, InteractHandler handler){
        return pageContent(i, FuncUtils.value(stack), FuncUtils.value(handler));
    }
    public ScreenBuilder pageContentHandler(int i, Supplier<InteractHandler> handlerSupplier){
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if(provider != null){
            provider.withHandler(handlerSupplier);
        }else {
            this.pageContentList.set(i, SlotProvider.instance().withHandler(handlerSupplier));
        }
        return this;
    }
    public ScreenBuilder pageContent(int i, UnaryOperator<SlotProvider> operator){
        ensureSize(i);
        SlotProvider provider = this.pageContentList.get(i);
        if(provider == null)provider = SlotProvider.instance();
        this.pageContentList.set(i, operator.apply(provider));
        return this;
    }
    public ScreenBuilder pageContent(SlotProvider provider){
        this.pageContentList.add(provider);
        return this;
    }
    public ScreenBuilder pageContent(Supplier<ItemStack> itemStackSupplier){
        return pageContent(SlotProvider.instance().withStack(itemStackSupplier));
    }
    public ScreenBuilder pageContentHandler(Supplier<InteractHandler> handlerSupplier){
        return pageContent(SlotProvider.instance().withHandler(handlerSupplier));
    }
    public ScreenBuilder pageContent(Supplier<ItemStack> itemStackSupplier, Supplier<InteractHandler> handlerSupplier){
        return pageContent(SlotProvider.instance().withStack(itemStackSupplier).withHandler(handlerSupplier));
    }
    public ScreenBuilder pageContent(ItemStack stack, InteractHandler handler){
        return pageContent(FuncUtils.value(stack), FuncUtils.value(handler));
    }

    public ScreenBuilder override(int i, SlotProvider override){
        this.overrideList.put(i, override);
        return this;
    }
    public ScreenBuilder override(int i, Supplier<ItemStack> stack){
        this.overrideList.compute(i, (t,v)->{
            if(v ==null){
                v = SlotProvider.instance();
            }
            v.withStack(stack);
            return v;
        });
        return this;
    }
    public ScreenBuilder overrideHandler(int i, Supplier<InteractHandler> stack){
        this.overrideList.compute(i, (t,v)->{
            if(v ==null){
                v = SlotProvider.instance();
            }
            v.withHandler(stack);
            return v;
        });
        return this;
    }

    public ScreenBuilder override(int i, ItemStack stack){
        return override(i, FuncUtils.value(stack));
    }
    public ScreenBuilder override(int i, InteractHandler handler){
        return overrideHandler(i, FuncUtils.value(handler));
    }

    public ScreenBuilder override(int i, ItemStack stack, InteractHandler handler){
        return override(i, SlotProvider.instance().withStack(stack).withHandler(handler));
    }

    public ScreenBuilder override(int i, UnaryOperator<SlotProvider> slotProviderUnaryOperator){
        this.overrideList.compute(i, (t,v)->{
            if(v == null)v = SlotProvider.instance();
            return slotProviderUnaryOperator.apply(v);
        });
        return this;
    }

    public <T> InventoryFactory.InventoryBuilder<T> createInventory(int page, InventoryFactory<T> fact){
        Preconditions.checkArgument(page>= 1);
        int currentMax = this.maxPageProvider.applyAsInt(this);
        Preconditions.checkArgument(page <= currentMax);
        var factory = fact.visitBuilder(this);
        factory.visitPage(page, this.sizePerPage, currentMax);
        for (var entry: this.basicList.int2ReferenceEntrySet()){
            int index = entry.getIntKey();
            if(index >= 0 && index < this.sizePerPage){
                factory.visitSlot(index, entry.getValue().getStack(factory), entry.getValue().getHandler(factory));
            }
        }
        int pageIndexRangeStart = (page - 1)* this.pageContentIndex.size();
        int pageIndexRangeEnd = pageIndexRangeStart + this.pageContentIndex.size();
        for (int i= pageIndexRangeStart ; i< pageIndexRangeEnd; ++i){
            if(i < pageContentList.size()){
                SlotProvider provider = pageContentList.get(i);
                if(provider != null){
                    factory.visitSlot(this.pageContentIndex.getInt(i - pageIndexRangeStart), provider.getStack(factory), provider.getHandler(factory));
                }
            }
        }
        for (var entry: buttonedIndex.int2ReferenceEntrySet()){
            int index = entry.getIntKey();
            if(index >=0 && index < this.sizePerPage){
                ItemStack stack ;
                InteractHandler handler = InteractHandler.EMPTY;
                switch (entry.getValue()){
                    case PREV_PAGE:
                        stack = this.pageSwitchProvider == null? null: this.pageSwitchProvider.apply(page, page-1,currentMax);
                        if(page != 1){
                            handler = InteractHandler.task(p->{
                                var builder = createInventory(page - 1, fact);
                                builder.open(p);
                                return false;
                            });
                        }
                        break;
                    case NEXT_PAGE:
                        stack = this.pageSwitchProvider == null? null: this.pageSwitchProvider.apply(page, page+1,currentMax);
                        if(page != 1){
                            handler = InteractHandler.task(p->{
                                var builder = createInventory(page + 1, fact);
                                builder.open(p);
                                return false;
                            });
                        }
                        break;
                    case BACK_BUTTON:
                        stack = backButtonItem;
                        handler = this.backHandler.apply(factory);
                        break;
                    default:
                        continue;
                }
                factory.visitSlot(index, stack, handler);
            }
        }
        for (var entry: this.overrideList.int2ReferenceEntrySet()){
            int index = entry.getIntKey();
            if(index >= 0 && index < this.sizePerPage){
                SlotProvider value = entry.getValue();
                if(value != null){
                    factory.visitSlot(index, value.getStack(factory), value.getHandler(factory));
                }

            }
        }
        factory.visitEnd();
        return factory;
    }

}
