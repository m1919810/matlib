package me.matl114.matlib.utils.entity.preprocess;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import me.matl114.matlib.common.lang.annotations.Note;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class EntityBuilder<T extends Entity> implements Consumer<T> {
    List<Consumer<T>> processor;

    protected void addProcessorInternal(Consumer<T> processor) {
        this.processor.add(processor);
    }

    Class<? extends T> entityClass;

    protected EntityBuilder(Class<? extends T> entityClass) {
        processor = new ArrayList<>();
        this.entityClass = entityClass;
    }

    @Override
    public final void accept(T t) {
        this.processor.forEach(processor -> processor.accept(t));
    }

    public static <T extends Entity> EntityBuilder<T> create(Class<T> clazz) {
        return new EntityBuilder<T>(clazz);
    }

    @Note("virtual builder only contains building processors,it can not be use for entity creation")
    public static <T extends Entity> EntityBuilder<T> createVirtual(Class<T> clazz) {
        return new EntityBuilder<T>(null);
    }

    public T newEntity(Location location) {
        T entity = location.getChunk().getWorld().spawn(location, entityClass);
        accept(entity);
        return entity;
    }

    public EntityBuilder<T> withGravity(boolean gravity) {
        addProcessorInternal(entity -> entity.setGravity(gravity));
        return this;
    }

    public EntityBuilder<T> withGlow(boolean glow) {
        addProcessorInternal(t -> t.setGlowing(glow));
        return this;
    }

    public EntityBuilder<T> withPhysics(boolean glow) {
        addProcessorInternal(t -> t.setNoPhysics(!glow));
        return this;
    }

    public EntityBuilder<T> withVelocity(double x, double y, double z) {
        addProcessorInternal(t -> t.setVelocity(new Vector(x, y, z)));
        return this;
    }

    public EntityBuilder<T> with(Consumer<T> consumer) {
        addProcessorInternal(consumer);
        return this;
    }

    public <W extends EntityBuilder<T>> W cast() {
        return (W) this;
    }

    public <W extends EntityBuilder<T>> W copy() {
        W e = (W) new EntityBuilder<>(entityClass);
        e.processor.addAll(processor);
        return e;
    }

    public <W extends EntityBuilder<T>> W copyTo(W e) {
        e.processor.addAll(processor);
        return e;
    }
}
