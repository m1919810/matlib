package me.matl114.matlib.implement.bukkit.entity.display.Implementation;

import java.util.HashMap;
import java.util.Map;
import lombok.NoArgsConstructor;
import me.matl114.matlib.algorithms.algorithm.TransformationUtils;
import me.matl114.matlib.implement.bukkit.entity.display.BluePrinted;

/**
 * a builder of display model, only carries blueprint information and basic Transformation
 */
@NoArgsConstructor
public class DisplayModelBuilder implements BluePrinted {
    public static DisplayModelBuilder builder() {
        return new DisplayModelBuilder();
    }

    Map<String, DisplayPart> model = new HashMap<>();

    @Override
    public Map<String, DisplayPart> getDisplayParts() {
        return model;
    }

    @Override
    public DisplayModelBuilder addDisplayPart(DisplayPart part) {
        model.put(part.partIdentifier, part);
        return this;
    }

    @Override
    public void updateStatus(boolean force) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TransformationUtils.LCTransformation getCurrentTransformation() {
        return TransformationUtils.LCTransformation.ofIdentical();
    }

    @Override
    public void setCurrentTransformation(TransformationUtils.LCTransformation transformation) {
        throw new UnsupportedOperationException();
    }
}
