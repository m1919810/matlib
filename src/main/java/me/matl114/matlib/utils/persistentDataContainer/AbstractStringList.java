package me.matl114.matlib.utils.persistentDataContainer;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import me.matl114.matlib.algorithms.dataStructures.struct.Union;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class AbstractStringList implements PersistentDataType<PersistentDataContainer, List<String>> {
    Union<String, Plugin> namespace;

    public AbstractStringList(Plugin namespace) {
        this.namespace = Union.ofB(namespace);
    }

    public AbstractStringList(String namespace) {
        this.namespace = Union.ofA(namespace);
    }

    @Nonnull
    public Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    private final Class clazz = (new ArrayList<String>()).getClass();

    @Nonnull
    public Class<List<String>> getComplexType() {

        return (Class<List<String>>) clazz;
    }

    private NamespacedKey ofNS(String val) {
        return this.namespace.isA()
                ? new NamespacedKey(this.namespace.getA(), val)
                : new NamespacedKey(this.namespace.getB(), val);
    }

    @Nonnull
    public PersistentDataContainer toPrimitive(
            @Nonnull List<String> complex, @Nonnull PersistentDataAdapterContext context) {
        PersistentDataContainer container = context.newPersistentDataContainer();

        for (int i = 0; i < complex.size(); ++i) {
            NamespacedKey key = ofNS("i_" + i);
            container.set(key, STRING, (String) complex.get(i));
        }

        return container;
    }

    @Nonnull
    public List<String> fromPrimitive(
            @Nonnull PersistentDataContainer primitive, @Nonnull PersistentDataAdapterContext context) {
        List<String> strings = new ArrayList<>();
        for (int i = 0; true; ++i) {
            NamespacedKey key = ofNS("i_" + i);
            if (primitive.has(key, STRING)) {
                strings.add(primitive.get(key, STRING));
            } else {
                break;
            }
        }
        return strings;
    }
}
