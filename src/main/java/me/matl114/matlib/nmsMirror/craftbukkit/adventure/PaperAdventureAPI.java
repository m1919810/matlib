package me.matl114.matlib.nmsMirror.craftbukkit.adventure;

import static me.matl114.matlib.nmsMirror.Import.*;

import java.util.*;
import me.matl114.matlib.utils.reflect.classBuild.annotation.RedirectType;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MethodTarget;
import me.matl114.matlib.utils.reflect.descriptor.annotations.MultiDescriptive;
import me.matl114.matlib.utils.reflect.descriptor.buildTools.TargetDescriptor;
import net.kyori.adventure.text.Component;

@MultiDescriptive(targetDefault = "io.papermc.paper.adventure.PaperAdventure")
public interface PaperAdventureAPI extends TargetDescriptor {
    @MethodTarget(isStatic = true)
    Component asAdventure(@RedirectType(ChatComponent) Iterable<?> comp);

    @MethodTarget(isStatic = true)
    ArrayList<Component> asAdventure(List<? extends Iterable<?>> comps);

    @MethodTarget(isStatic = true)
    ArrayList<Component> asAdventureFromJson(List<String> jsonStrings);

    @MethodTarget(isStatic = true)
    ArrayList<String> asJson(List<? extends Component> adventure);

    @MethodTarget(isStatic = true)
    Iterable<?> asVanilla(Component component);

    @MethodTarget(isStatic = true)
    List<Iterable<?>> asVanilla(List<? extends Component> adventure);

    @MethodTarget(isStatic = true)
    String asJsonString(Component component, Locale locale);

    //    @MethodTarget(isStatic = true)
    //    @RedirectName("asVanilla")
    //    Object asVanillaStyle(net.kyori.adventure.text.format.Style style);
    //
    //    @MethodTarget(isStatic = true)
    //    @RedirectName("asAdventure")
    //    net.kyori.adventure.text.format.Style asAdventureStyle(@RedirectType(Style)Object style);
}
