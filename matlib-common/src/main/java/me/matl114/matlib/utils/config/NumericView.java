package me.matl114.matlib.utils.config;

public abstract class NumericView implements DataView<Number> {
    boolean initialized = false;
    private final NodeReference<Number> parent;
    Type rangeType;

    @Override
    public NodeReference<Number> getDelegate() {
        return parent;
    }

    protected NumericView(NodeReference<Number> parent) {
        this.parent = parent;
    }

    @Override
    public Number get() {
        return parent.get();
    }

    public boolean set(Number value) {
        return parent.set(value);
    }

    @Override
    public Type getType() {
        return rangeType;
    }

    static class IntView extends NumericView {
        {
            rangeType = Type.INT_RANGE;
        }

        int cache;

        protected IntView(NodeReference<Number> parent) {
            super(parent);
        }

        public int getAsInt() {
            if (!initialized) {
                cache = get().intValue();
                initialized = true;
            }
            return cache;
        }

        public long getAsLong() {
            if (!initialized) {
                cache = get().intValue();
                initialized = true;
            }
            return cache;
        }

        public boolean setInt(int val) {
            cache = val;
            initialized = true;
            return set(val);
        }

        @Override
        public boolean setString(String val) {
            cache = Integer.parseInt(val);
            initialized = true;
            return set(cache);
        }
    }

    static class LongView extends NumericView {
        {
            rangeType = Type.LONG_RANGE;
        }

        long cache;

        protected LongView(NodeReference<Number> parent) {
            super(parent);
        }

        public int getAsInt() {
            if (!initialized) {
                cache = get().longValue();
                initialized = true;
            }
            return (int) cache;
        }

        public long getAsLong() {
            if (!initialized) {
                cache = get().longValue();
                initialized = true;
            }
            return cache;
        }

        public boolean setLong(long val) {
            cache = val;
            initialized = true;
            return set(val);
        }

        public boolean setInt(int val) {
            cache = val;
            initialized = true;
            return set(cache);
        }

        @Override
        public boolean setString(String val) {
            cache = Long.parseLong(val);
            initialized = true;
            return set(cache);
        }
    }

    static class DoubleView extends NumericView {
        {
            rangeType = Type.FLOAT_RANGE;
        }

        protected DoubleView(NodeReference<Number> parent) {
            super(parent);
        }

        double cache;

        @Override
        public double getAsDouble() {
            if (!initialized) {
                cache = get().doubleValue();
                initialized = true;
            }
            return cache;
        }

        public int getAsInt() {
            if (!initialized) {
                cache = get().doubleValue();
                initialized = true;
            }
            return (int) cache;
        }

        public long getAsLong() {
            if (!initialized) {
                cache = get().doubleValue();
                initialized = true;
            }
            return (long) cache;
        }

        public boolean setInt(int val) {
            cache = (double) val;
            initialized = true;
            return set(cache);
        }

        public boolean setLong(long val) {
            cache = (double) val;
            initialized = true;
            return set(cache);
        }

        public boolean setDouble(double val) {
            cache = val;
            initialized = true;
            return set(val);
        }

        @Override
        public boolean setString(String val) {
            cache = Double.parseDouble(val);
            initialized = true;
            return set(cache);
        }
    }
}
