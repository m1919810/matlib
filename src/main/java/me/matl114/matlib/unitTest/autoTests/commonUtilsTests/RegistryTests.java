package me.matl114.matlib.unitTest.autoTests.commonUtilsTests;

import java.util.concurrent.atomic.AtomicInteger;
import lombok.AllArgsConstructor;
import lombok.ToString;
import me.matl114.matlib.common.lang.exceptions.Abort;
import me.matl114.matlib.unitTest.OnlineTest;
import me.matl114.matlib.unitTest.TestCase;
import me.matl114.matlib.utils.Debug;
import me.matl114.matlib.utils.registry.Content;
import me.matl114.matlib.utils.registry.NamespacedRegistry;
import me.matl114.matlib.utils.registry.Registry;
import me.matl114.matlib.utils.registry.impl.NamespacedRegistryImpl;
import net.kyori.adventure.key.Namespaced;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

public class RegistryTests implements TestCase {

    public NamespacedRegistry<Shit, ShitOwner> ROOT =
            new NamespacedRegistryImpl<>("matlib", "shit-registry", true, new NamespacedKey("matlib", "0"));

    {
        Shit shit0 = new Shit();
        var content = ROOT.registerThis(shit0.id(), shit0); // Content.common(,"matlib","default-shit");
        Debug.logger(content);
    }

    @OnlineTest(name = "registry common test")
    public void test_registryTests() throws Throwable {
        Debug.logger(ROOT);
        ShitOwner subShit1 = new ShitOwner("slimefun");
        ShitOwner subShit2 = new ShitOwner("minecraft");
        Registry<Shit> subRegistry1 = ROOT.createSubRegistry(subShit1);
        Registry<Shit> subRegistry2 = ROOT.createSubRegistry(subShit2);
        checkErrorAction(() -> ROOT.createSubRegistry(subShit1));
        Shit shit1 = new Shit();
        Shit shit2 = new Shit();
        Shit shit3 = new Shit();
        Shit shit4 = new Shit();
        Shit shit5 = new Shit();
        Content<?> content = subRegistry1.registerThis(shit1.id(), shit1);
        Content<?> content2 = subRegistry1.registerThis(shit2.id(), shit2);
        Content<?> content3 = subRegistry2.registerThis(shit3.id(), shit3);
        Content<?> content4 = subRegistry2.registerThis(shit4.id(), shit4);
        Debug.logger(content, content2, content3, content4);
        checkErrorAction(() -> subRegistry1.registerThis("error-name", shit5));
        Debug.logger(subRegistry1);
        Debug.logger(subRegistry2);
        Debug.logger(ROOT.asRegistryView());
        checkErrorAction(() -> {
            Namespaced namespaced = new Namespaced() {
                @Override
                public @NotNull String namespace() {
                    return "slimefun";
                }
            };
            ((NamespacedRegistry) ROOT).removeSubRegistry(namespaced);
        });
        checkErrorAction(() -> {
            Shit shit6 = new Shit();
            ROOT.asRegistryView().registerThis(shit6.id(), shit6);
        });
        ROOT.removeSubRegistry(subShit1);
        ROOT.removeSubRegistry(subShit2);
        Assert(ROOT.asRegistryView().idSet().size() == 1);
        Debug.logger(ROOT);
        Debug.logger(content, content2, content3, content4);
        ShitOwner subShit3 = new ShitOwner("new-registry");
        Registry<Shit> subRegistry3 = ROOT.createSubRegistry(subShit3);
        Content<?> content5 = subRegistry3.registerThis(shit1.id(), shit1);
        Assert(content5 == content);
        Debug.logger(subRegistry3);
        ROOT.removeSubRegistry(subShit3);
    }

    private void checkErrorAction(Runnable task) throws Abort {
        try {
            // should stop when duplicate creating
            task.run();
            throw new Abort();
        } catch (Abort aaa) {
            throw aaa;
        } catch (Throwable e) {
            Debug.logger("Error Action successfully stopped");
        }
    }

    @AllArgsConstructor
    @ToString
    public static class ShitOwner implements Namespaced {
        String namespace;

        @Override
        public @NotNull String namespace() {
            return namespace;
        }
    }

    @ToString
    public class Shit {
        static AtomicInteger counter = new AtomicInteger(0);
        int id;

        {
            id = counter.getAndIncrement();
            ROOT.createRegistryContent(String.valueOf(id), this);
        }

        String id() {
            return String.valueOf(id);
        }
    }
}
