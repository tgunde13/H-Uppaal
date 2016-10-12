package SW9.utility.helpers;

import SW9.model_canvas.Removable;

import java.util.ArrayList;
import java.util.List;

public class SelectHelper {

    private static final ArrayList<Removable> selectedElements = new ArrayList<>();

    public static void makeSelectable(final Removable removable) {
        removable.getMouseTracker().registerOnMousePressedEventHandler(event -> {
            select(removable);
        });
    }

    public static boolean select(final Removable removable) {
        // Check if the select went well, if so add it to the selected list
        if (removable.select()) {
            clearSelectedElements(removable);
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<Removable> getSelectedElements() {
        return selectedElements;
    }

    public static void clearSelectedElements() {
        while (!selectedElements.isEmpty()) {
            selectedElements.get(0).deselect();
            selectedElements.remove(0);
        }
    }

    public static void clearSelectedElements(final Removable exception) {
        selectedElements.remove(exception);

        clearSelectedElements();

        selectedElements.add(exception);
    }

    public static boolean isSelected(final Removable... needles) {
        for (Removable needle : needles) {
            if (selectedElements.contains(needle)) return true;
        }
        return false;
    }

    public static boolean isSelected(final List<? extends Removable> needles) {
        for (Removable needle : needles) {
            if (selectedElements.contains(needle)) return true;
        }
        return false;
    }

}
