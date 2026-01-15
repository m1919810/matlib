package me.matl114.matlib.implement.bukkit.entity.display;

import com.google.common.base.Preconditions;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.matl114.matlib.algorithms.algorithm.TransformationUtils;
import me.matl114.matlib.common.lang.annotations.ConstVal;
import me.matl114.matlib.utils.entity.preprocess.DisplayEntityBuilder;
import org.bukkit.util.Transformation;

/**
 * a transform applicable which has a display part blue print to show origin state
 */
public interface BluePrinted extends TransformApplicable {
    /**
     * what a group of display should look like
     * map: id->displayPart
     * @return
     */
    public Map<String, DisplayPart> getDisplayParts();
    /**
     * builder method for adding parts and choosing location to build
     * @param part
     * @return
     */
    public BluePrinted addDisplayPart(DisplayPart part);

    default BluePrinted addDisplayParts(List<DisplayPart> parts) {
        parts.forEach(this::addDisplayPart);
        return this;
    }

    default BluePrinted copyFrom(BluePrinted abs) {
        abs.getDisplayParts().values().forEach(this::addDisplayPart);
        return this;
    }

    /**
     * part which includes what display context is and what is the default transformation
     */
    @AllArgsConstructor
    @Getter
    @Setter
    @ConstVal
    public static class DisplayPart {
        public final String partIdentifier;
        //        public Transformation getTransformation(){
        //
        //            return this.transformation;
        //        }
        @Getter
        private final Transformation transformation;

        public final DisplayEntityBuilder context;

        public static DisplayPartBuilder builder() {
            return new DisplayPartBuilder();
        }

        public static class DisplayPartBuilder {
            private String partIdentifier;
            private Transformation transformation;
            private DisplayEntityBuilder context;

            public DisplayPartBuilder partIdentifier(String partIdentifier) {
                this.partIdentifier = partIdentifier;
                return this;
            }

            public DisplayPartBuilder transformation(Transformation transformation) {
                this.transformation = transformation;
                return this;
            }

            public DisplayPartBuilder context(DisplayEntityBuilder context) {
                this.context = context;
                return this;
            }

            public DisplayPart build() {
                Preconditions.checkNotNull(this.partIdentifier);
                Preconditions.checkNotNull(this.context);
                return new DisplayPart(
                        this.partIdentifier,
                        this.transformation == null ? TransformationUtils.defaultTrans() : this.transformation,
                        this.context);
            }
        }
    }
}
