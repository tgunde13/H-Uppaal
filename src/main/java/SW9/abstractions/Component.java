package SW9.abstractions;

import SW9.utility.colors.Color;
import SW9.utility.serialize.Serializable;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.concurrent.atomic.AtomicInteger;

public class Component implements Serializable {

    // Used to generate unique IDs
    private static final AtomicInteger hiddenID = new AtomicInteger(0);
    private static final String NAME = "name";
    private static final String DECLARATIONS = "declarations";
    private static final String LOCATIONS = "locations";
    private static final String INITIAL_LOCATION = "initialLocation";
    private static final String FINAL_LOCATION = "finalLocation";
    private static final String EDGES = "edges";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String WIDTH = "width";
    private static final String HEIGHT = "height";
    private static final String COLOR = "color";
    private static final String COLOR_INTENSITY = "colorIntensity";
    // Verification properties
    @Expose
    private final StringProperty name = new SimpleStringProperty("");
    private final StringProperty declarations = new SimpleStringProperty("");
    private final ObservableList<Location> locations = FXCollections.observableArrayList();
    private final ObservableList<Edge> edges = FXCollections.observableArrayList();
    private final ObjectProperty<Location> initialLocation = new SimpleObjectProperty<>();
    private final ObjectProperty<Location> finalLocation = new SimpleObjectProperty<>();
    // Styling properties
    private final DoubleProperty x = new SimpleDoubleProperty(0d);
    private final DoubleProperty y = new SimpleDoubleProperty(0d);
    private final DoubleProperty width = new SimpleDoubleProperty(450d);
    private final DoubleProperty height = new SimpleDoubleProperty(600d);
    private final BooleanProperty declarationOpen = new SimpleBooleanProperty(false);
    private final ObjectProperty<Color> color = new SimpleObjectProperty<>(Color.GREY_BLUE);
    private final ObjectProperty<Color.Intensity> colorIntensity = new SimpleObjectProperty<>(Color.Intensity.I700);

    public Component() {
        this("Component" + hiddenID.getAndIncrement());
    }

    public Component(final String name) {
        setName(name);

        // A component must have at least one initial location
        final Location initialLocation = new Location();
        initialLocation.setType(Location.Type.INITIAL);
        initialLocation.setColorIntensity(getColorIntensity());
        initialLocation.setColor(getColor());
        this.initialLocation.set(initialLocation);

        // A component must have at least one final location
        final Location finalLocation = new Location();
        finalLocation.setType(Location.Type.FINAl);
        finalLocation.setColorIntensity(getColorIntensity());
        finalLocation.setColor(getColor());
        this.finalLocation.set(finalLocation);
    }

    public Component(final JsonObject object) {
        hiddenID.incrementAndGet();
        deserialize(object);
    }

    public String getName() {
        return name.get();
    }

    public void setName(final String name) {
        this.name.set(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public String getDeclarations() {
        return declarations.get();
    }

    public void setDeclarations(final String declarations) {
        this.declarations.set(declarations);
    }

    public StringProperty declarationsProperty() {
        return declarations;
    }

    public ObservableList<Location> getLocations() {
        return locations;
    }

    public boolean addLocation(final Location location) {
        return locations.add(location);
    }

    public boolean removeLocation(final Location location) {
        return locations.remove(location);
    }

    public ObservableList<Edge> getEdges() {
        return edges;
    }

    public boolean addEdge(final Edge edge) {
        return edges.add(edge);
    }

    public boolean removeEdge(final Edge edge) {
        return edges.remove(edge);
    }

    public double getX() {
        return x.get();
    }

    public void setX(final double x) {
        this.x.set(x);
    }

    public DoubleProperty xProperty() {
        return x;
    }

    public double getY() {
        return y.get();
    }

    public void setY(final double y) {
        this.y.set(y);
    }

    public DoubleProperty yProperty() {
        return y;
    }

    public double getWidth() {
        return width.get();
    }

    public void setWidth(final double width) {
        this.width.set(width);
    }

    public DoubleProperty widthProperty() {
        return width;
    }

    public double getHeight() {
        return height.get();
    }

    public void setHeight(final double height) {
        this.height.set(height);
    }

    public DoubleProperty heightProperty() {
        return height;
    }

    public Color getColor() {
        return color.get();
    }

    public void setColor(final Color color) {
        this.color.set(color);
    }

    public ObjectProperty<Color> colorProperty() {
        return color;
    }

    /*
     * SERIALIZATION OF CLASS
     */

    public Color.Intensity getColorIntensity() {
        return colorIntensity.get();
    }

    public void setColorIntensity(final Color.Intensity colorIntensity) {
        this.colorIntensity.set(colorIntensity);
    }

    public ObjectProperty<Color.Intensity> colorIntensityProperty() {
        return colorIntensity;
    }

    public boolean isDeclarationOpen() {
        return declarationOpen.get();
    }

    public BooleanProperty declarationOpenProperty() {
        return declarationOpen;
    }

    public Location getInitialLocation() {
        return initialLocation.get();
    }

    public void setInitialLocation(final Location initialLocation) {
        this.initialLocation.set(initialLocation);
    }

    public ObjectProperty<Location> initialLocationProperty() {
        return initialLocation;
    }

    public Location getFinalLocation() {
        return finalLocation.get();
    }

    public void setFinalLocation(final Location finalLocation) {
        this.finalLocation.set(finalLocation);
    }

    public ObjectProperty<Location> finalLocationProperty() {
        return finalLocation;
    }

    public Edge getUnfinishedEdge() {
        for (final Edge edge : edges) {
            if (edge.getTargetLocation() == null) return edge;
        }

        return null;
    }

    @Override
    public JsonObject serialize() {
        final JsonObject result = new JsonObject();

        result.addProperty(NAME, getName());
        result.addProperty(DECLARATIONS, getDeclarations());

        final JsonArray locations = new JsonArray();
        getLocations().forEach(location -> locations.add(location.serialize()));
        result.add(LOCATIONS, locations);

        result.add(INITIAL_LOCATION, getInitialLocation().serialize());
        result.add(FINAL_LOCATION, getFinalLocation().serialize());

        final JsonArray edges = new JsonArray();
        getEdges().forEach(edge -> edges.add(edge.serialize()));
        result.add(EDGES, edges);

        result.addProperty(X, getX());
        result.addProperty(Y, getY());
        result.addProperty(WIDTH, getWidth());
        result.addProperty(HEIGHT, getHeight());
        result.addProperty(COLOR, "");
        result.addProperty(COLOR_INTENSITY, "");

        return result;
    }

    @Override
    public void deserialize(final JsonObject json) {
        setName(json.getAsJsonPrimitive(NAME).getAsString());
        setDeclarations(json.getAsJsonPrimitive(DECLARATIONS).getAsString());

        json.getAsJsonArray(LOCATIONS).forEach(jsonElement -> {
            final Location newLocation = new Location((JsonObject) jsonElement);
            locations.add(newLocation);
        });

        final Location newInitialLocation = new Location(json.getAsJsonObject(INITIAL_LOCATION));
        setInitialLocation(newInitialLocation);

        final Location newFinalLocation = new Location(json.getAsJsonObject(FINAL_LOCATION));
        setFinalLocation(newFinalLocation);

        json.getAsJsonArray(EDGES).forEach(jsonElement -> {
            final Edge newEdge = new Edge((JsonObject) jsonElement, this);
            edges.add(newEdge);
        });
    }
}