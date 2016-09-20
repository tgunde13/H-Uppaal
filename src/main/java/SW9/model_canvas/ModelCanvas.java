package SW9.model_canvas;

import SW9.Keybind;
import SW9.KeyboardTracker;
import SW9.Main;
import SW9.utility.DropShadowHelper;
import javafx.fxml.FXML;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.Pane;

public class ModelCanvas extends Pane {

    public static Location locationOnMouse = null;
    public static Edge edgeOnMouse = null;
    public static Location hoveredLocation = null;

    public static boolean mouseHasLocation() {
        return locationOnMouse != null;
    }

    public static boolean mouseHasEdge() {
        return edgeOnMouse != null;
    }

    public static boolean locationIsHovered() {
        return hoveredLocation != null;
    }


    public ModelCanvas() {
        initialize();
    }

    @FXML
    public void initialize() {
        KeyboardTracker.registerKeybind(KeyboardTracker.ADD_NEW_LOCATION, new Keybind(new KeyCodeCombination(KeyCode.L), () -> {
            if (!mouseHasLocation()) {
                final Location newLocation = new Location(Main.mouseTracker);
                locationOnMouse = newLocation;

                newLocation.setEffect(DropShadowHelper.generateElevationShadow(22));
                ModelCanvas.this.getChildren().add(newLocation);
            }
        }));
    }


}