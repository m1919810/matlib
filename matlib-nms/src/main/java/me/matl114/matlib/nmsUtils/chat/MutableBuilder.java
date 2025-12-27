package me.matl114.matlib.nmsUtils.chat;

import static me.matl114.matlib.nmsMirror.impl.NMSChat.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lombok.*;
import lombok.experimental.Accessors;
import me.matl114.matlib.nmsMirror.impl.NMSChat;

@Accessors(chain = true, fluent = true)
@Getter
@Setter
@AllArgsConstructor
@ToString
public class MutableBuilder implements Cloneable, BuildResult<Iterable<?>> {
    private MutableBuilder() {}

    StyleBuilder style;
    List<MutableBuilder> siblings;
    ContentBuilder componentContent;
    private static final MutableBuilder INSTANCE = new MutableBuilder();

    public static MutableBuilder builder() {
        MutableBuilder builder = INSTANCE.copy0();
        builder.siblings = new ArrayList<>(2);
        return builder;
    }

    protected MutableBuilder copy0() {
        try {
            MutableBuilder clone = (MutableBuilder) super.clone();
            // TODO: copy mutable state here, so the clone can't change the internals of the original
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public MutableBuilder clone() {
        return builder()
                .style(this.style == null ? null : this.style.clone())
                .siblings(new ArrayList<>(siblings))
                .componentContent(this.componentContent == null ? null : this.componentContent.clone());
    }

    @AllArgsConstructor
    private static class ImmutableBuilder extends MutableBuilder {
        Iterable<?> value;

        @Override
        public Iterable<?> toNMS() {
            return value;
        }

        @Override
        public MutableBuilder clone() {
            return new ImmutableBuilder(value);
        }

        public MutableBuilder toImmutable() {
            return this;
        }

        @Override
        public boolean isImmutable() {
            return true;
        }

        public String toString() {
            return "Immutable(" + value + ")";
        }
    }
    ;

    public static MutableBuilder immutable(Iterable<?> value) {
        return new ImmutableBuilder(value);
    }

    @Override
    public MutableBuilder toImmutable() {
        Iterable<?> value = toNMS();
        return new ImmutableBuilder(value);
    }

    public Iterable<?> toNMS() {
        Iterable<?> value = NMSChat.CHATCOMPONENT.create(this.componentContent.toNMS());
        if (this.style != null) {
            Object style = this.style.toNMS();
            value = NMSChat.CHATCOMPONENT.setStyle(value, style);
        }
        if (!this.siblings.isEmpty()) {
            List<Iterable<?>> siblings1 = NMSChat.CHATCOMPONENT.getSiblings(value);
            this.siblings.stream().map(MutableBuilder::toNMS).forEach((Consumer<Iterable<?>>) siblings1::add);
        }
        return value;
    }
}
