package me.matl114.matlib.utils.config;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.common.lang.annotations.Note;

public class NodeReference<T> implements View {
    public static final NodeReference<?> NULL = new NodeReference<Object>(null, null, null) {
        @Override
        public boolean set(Object value) {
            throw new IllegalStateException("Can not set value to NULL DataReference");
        }

        @Override
        public boolean isNullNode() {
            return true;
        }
    };

    @Getter
    @Setter(AccessLevel.PACKAGE)
    private volatile T value;

    private final Consumer<T> makeDirty;
    Config.Node nodeRef;

    static <W> NodeReference<W> of(W object, Consumer<W> makeDirty, Config.Node node) {
        return new NodeReference<>(object, makeDirty, node);
    }

    NodeReference(T object, Consumer<T> makeDirty, Config.Node node) {
        this.value = object;
        this.makeDirty = makeDirty;
    }

    private boolean isLeafNode() {
        // todo enable subConfig parser
        return true;
    }

    public T get() {
        return value;
    }

    public boolean isNullNode() {
        return false;
    }

    // return if pass identifier check
    @Note("data must be set via view to avoid cache not sync")
    protected boolean set(T value) {
        this.value = value;
        this.makeDirty();
        return true;
    }

    public void makeDirty() {
        if (this.makeDirty != null) {
            this.makeDirty.accept(value);
        }
        updateView();
    }

    private void updateView() {
        if (customViewCreator == null) {
            view = parseView(this);
        } else {
            view = customViewCreator.createCustomView(view, this);
        }
        Preconditions.checkNotNull(view);
    }

    private volatile DataView<?> view;
    private ViewCreator<?> customViewCreator;

    public synchronized DataView<?> view() {
        if (view == null) {
            updateView();
        }
        return view;
    }

    public static DataView<?> parseView(NodeReference<?> configNode) {
        Object value = configNode.get();
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            return new NumericView.IntView((NodeReference<Number>) configNode);
        }
        if (value instanceof Long) {
            return new NumericView.LongView((NodeReference<Number>) configNode);
        }
        if (value instanceof Double || value instanceof Float) {
            return new NumericView.DoubleView((NodeReference<Number>) configNode);
        }
        if (value instanceof Boolean) {
            return new BoolView((NodeReference<Boolean>) configNode);
        }
        if (value instanceof String) {
            return new StringView((NodeReference<String>) configNode);
        }
        if (value instanceof List<?>) {
            return new ListView((NodeReference<List>) configNode);
        }
        if (value instanceof Map<?, ?>) {
            return new SubConfigView((NodeReference<Map>) configNode);
        }
        // 用户可能声明自定义类
        // 日期时间
        // 二进制
        // BigDecimal
        // null ?
        return new ObjectView(configNode);
    }

    @Note("you can create your own data codec at a Config Node")
    public <W extends DataView<?>> NodeReference<T> setView(ViewCreator<W> codec) {
        customViewCreator = codec;
        updateView();
        return this;
    }

    public static interface ViewCreator<T extends DataView<?>> {
        public T createCustomView(DataView<?> current, NodeReference<?> data);
    }
    // use view as a view
    public boolean getAsBoolean() {
        return view().getAsBoolean();
    }

    public int getAsInt() {
        return view().getAsInt();
    }

    public byte getAsByte() {
        return (byte) getAsInt();
    }

    public short getAsShort() {
        return (short) getAsInt();
    }

    public long getAsLong() {
        return view().getAsLong();
    }

    public float getAsFloat() {
        return (float) getAsDouble();
    }

    public double getAsDouble() {
        return view().getAsDouble();
    }

    public String getAsString() {
        return view().getAsString();
    }

    public List<String> getAsList() {
        return view().getAsList();
    }

    public boolean setBoolean(boolean val) {
        return view().setBoolean(val);
    }

    public boolean setInt(int val) {
        return view().setInt(val);
    }

    public boolean setLong(long val) {
        return view().setLong(val);
    }

    public boolean setDouble(double val) {
        return view().setDouble(val);
    }

    public boolean setFloat(float val) {
        return setDouble(val);
    }

    public boolean setList(List<String> val) {
        return view().setList(val);
    }

    public boolean setString(String val) {
        return view().setString(val);
    }
}
