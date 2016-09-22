package SW9.model_canvas;

import SW9.MouseTracker;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Edge {

    private final Location sourceLocation;
    private Location targetLocation = null;


    private final List<Nail> nails = new ArrayList<>();
    private Line lineCue = new Line();

    private boolean skipLine = true;

    ObservableList<Line> lines = FXCollections.observableArrayList();
    private BooleanBinding linesIsEmpty = new BooleanBinding() {
        {
            super.bind(lines);
        }

        @Override
        protected boolean computeValue() {
            return lines.isEmpty();
        }
    };


    // Mouse trackers
    private final MouseTracker canvasMouseTracker;

    public Edge(final Location sourceLocation, final MouseTracker canvasMouseTracker) {
        this.sourceLocation = sourceLocation;
        this.canvasMouseTracker = canvasMouseTracker;

        DoubleBinding[] cueBindings = getCueBindings();

        lineCue.startXProperty().bind(cueBindings[0]);
        lineCue.startYProperty().bind(cueBindings[1]);
        lineCue.endXProperty().bind(cueBindings[2]);
        lineCue.endYProperty().bind(cueBindings[3]);
        lineCue.setMouseTransparent(true);
        addChildToParent(lineCue);

        this.canvasMouseTracker.registerOnMousePressedEventHandler(event -> {
            if (skipLine) {
                skipLine = false;
                return;
            } else if (this.targetLocation != null) {
                return;
            }

            // Create nail
            final Nail nail = new Nail(lineCue.getEndX(), lineCue.getEndY());

            // Create a new line
            final Line line = new Line();

            // If it is the first line to be placed on the canvas
            if (linesIsEmpty.get()) {
                Pair<DoubleBinding, DoubleBinding> newLineStartBindings = getStartBindings(
                        sourceLocation.centerXProperty(),
                        sourceLocation.centerYProperty(),
                        nail.centerXProperty(),
                        nail.centerYProperty()
                );
                line.startXProperty().bind(newLineStartBindings.getKey());
                line.startYProperty().bind(newLineStartBindings.getValue());
            } else {
                final Nail previousNail = nails.get(nails.size() - 1);
                line.startXProperty().bind(previousNail.centerXProperty());
                line.startYProperty().bind(previousNail.centerYProperty());
            }

            // If we are hovering a location we want to set the target location and draw the last line
            if (ModelCanvas.mouseIsHoveringLocation()) {

                this.targetLocation = ModelCanvas.getHoveredLocation();

                // If the source location should be connected directly to itself
                if(this.targetLocation.equals(this.sourceLocation) && nails.isEmpty()) {
                    // TODO connect them (draw two nails and three lines connecting it)
                } else if(!this.targetLocation.equals(this.sourceLocation) && nails.isEmpty()) {

                    Pair<DoubleBinding, DoubleBinding> newLineStartBindings = getStartBindings(
                            sourceLocation.centerXProperty(),
                            sourceLocation.centerYProperty(),
                            targetLocation.centerXProperty(),
                            targetLocation.centerYProperty()
                    );

                    Pair<DoubleBinding, DoubleBinding> newLineEndBindings = getEndBindings(
                            sourceLocation.centerXProperty(),
                            sourceLocation.centerYProperty(),
                            targetLocation.centerXProperty(),
                            targetLocation.centerYProperty()
                    );

                    line.startXProperty().bind(newLineStartBindings.getKey());
                    line.startYProperty().bind(newLineStartBindings.getValue());

                    line.endXProperty().bind(newLineEndBindings.getKey());
                    line.endYProperty().bind(newLineEndBindings.getValue());
                } else {

                    final Nail previousNail = nails.get(nails.size() - 1);

                    Pair<DoubleBinding, DoubleBinding> newLineEndBindings = getEndBindings(
                            previousNail.centerXProperty(),
                            previousNail.centerYProperty(),
                            targetLocation.centerXProperty(),
                            targetLocation.centerYProperty()
                    );

                    line.startXProperty().bind(previousNail.centerXProperty());
                    line.startYProperty().bind(previousNail.centerYProperty());

                    line.endXProperty().bind(newLineEndBindings.getKey());
                    line.endYProperty().bind(newLineEndBindings.getValue());
                }

            } else {
                line.endXProperty().bind(nail.centerXProperty());
                line.endYProperty().bind(nail.centerYProperty());
            }

            if (this.targetLocation == null) {
                // Add the nail to the collection
                nail.setMouseTransparent(true);
                addChildToParent(nail);
                nails.add(nail);
            } else {
                removeChildFromParent(lineCue);
            }

            // Add the line to the collection
            line.setMouseTransparent(true);
            addChildToParent(line);
            lines.add(line);
        });
    }

    private void removeChildFromParent(final Node node) {
        // Get the parent from the source location
        Pane parent = (Pane) this.sourceLocation.getParent();

        if (parent == null) return;

        parent.getChildren().remove(node);
    }

    private void addChildToParent(final Node node) {

        // Get the parent from the source location
        Pane parent = (Pane) this.sourceLocation.getParent();

        if (parent == null) return;

        parent.getChildren().add(node);
    }

    /**
     * Returns 4 elements in an array the index describes:
     * 0 - startXProperty
     * 1 - startYProperty
     * 2 - endXProperty
     * 3 - endYProperty
     *
     * @return the array of bindings
     */
    private DoubleBinding[] getCueBindings() {
        Pair<DoubleBinding, DoubleBinding> startBindingsWhenLinesEmpty = getStartBindings(
                sourceLocation.centerXProperty(),
                sourceLocation.centerYProperty(),
                canvasMouseTracker.getXProperty(),
                canvasMouseTracker.getYProperty()
        );

        return new DoubleBinding[]{
                new DoubleBinding() {
                    {
                        super.bind(lines,
                                sourceLocation.centerXProperty(),
                                sourceLocation.centerYProperty(),
                                canvasMouseTracker.getXProperty(),
                                canvasMouseTracker.getYProperty());
                    }

                    @Override
                    protected double computeValue() {

                        if (linesIsEmpty.get()) {
                            return startBindingsWhenLinesEmpty.getKey().get();
                        }

                        return lines.get(lines.size() - 1).endXProperty().get();
                    }
                },
                new DoubleBinding() {
                    {
                        super.bind(lines,
                                sourceLocation.centerXProperty(),
                                sourceLocation.centerYProperty(),
                                canvasMouseTracker.getXProperty(),
                                canvasMouseTracker.getYProperty());
                    }

                    @Override
                    protected double computeValue() {

                        if (linesIsEmpty.get()) {
                            return startBindingsWhenLinesEmpty.getValue().get();
                        }

                        return lines.get(lines.size() - 1).endYProperty().get();
                    }
                },
                new DoubleBinding() {
                    {
                        super.bind(canvasMouseTracker.getXProperty());
                    }

                    @Override
                    protected double computeValue() {

                        return canvasMouseTracker.getXProperty().get();
                    }
                },
                new DoubleBinding() {
                    {
                        super.bind(canvasMouseTracker.getYProperty());
                    }

                    @Override
                    protected double computeValue() {

                        return canvasMouseTracker.getYProperty().get();
                    }
                }

        };
    }

    // Bindings for starting in a location
    private Pair<DoubleBinding, DoubleBinding> getStartBindings(final ObservableDoubleValue startX, final ObservableDoubleValue startY, final ObservableDoubleValue endX, final ObservableDoubleValue endY) {
        return new Pair<>(
                new DoubleBinding() {
                    {
                        super.bind(startX, startY, endX, endY);
                    }

                    @Override
                    protected double computeValue() {
                        double angle = Math.atan2(startY.get() - endY.get(), startX.get() - endX.get()) - Math.toRadians(180);
                        return startX.get() + Location.RADIUS * Math.cos(angle);
                    }
                },
                new DoubleBinding() {
                    {
                        super.bind(startX, startY, endX, endY);
                    }

                    @Override
                    protected double computeValue() {
                        double angle = Math.atan2(startY.get() - endY.get(), startX.get() - endX.get()) - Math.toRadians(180);
                        return startY.get() + Location.RADIUS * Math.sin(angle);
                    }
                }
        );

    }

    // Bindings for ending in a location (handles mouse and hover location)
    private Pair<DoubleBinding, DoubleBinding> getEndBindings(final ObservableDoubleValue startX, final ObservableDoubleValue startY, final ObservableDoubleValue endX, final ObservableDoubleValue endY) {

        return new Pair<>(
                new DoubleBinding() {
                    {
                        super.bind(startX, startY, endX, endY);
                    }

                    @Override
                    protected double computeValue() {
                        double angle = Math.atan2(startY.get() - endY.get(), startX.get() - endX.get());
                        return endX.get() + Location.RADIUS * Math.cos(angle);
                    }
                },
                new DoubleBinding() {
                    {
                        super.bind(startX, startY, endX, endY);
                    }

                    @Override
                    protected double computeValue() {
                        double angle = Math.atan2(startY.get() - endY.get(), startX.get() - endX.get());
                        return endY.get() + Location.RADIUS * Math.sin(angle);
                    }
                }
        );

    }

    // Bindings for ending in a location
    /*private Pair<DoubleBinding, DoubleBinding> getEndBindings(final ObservableDoubleValue startX, final ObservableDoubleValue startY, final ObservableDoubleValue endX, final ObservableDoubleValue endY) {
        return new Pair<>(
                new DoubleBinding() {
                    {
                        super.bind(startX, startY, endX, endY);
                    }

                    @Override
                    protected double computeValue() {
                        double angle = Math.atan2(startY.get() - endY.get(), startX.get() - endX.get());
                        return startX.get() + Location.RADIUS * Math.cos(angle);
                    }
                },
                new DoubleBinding() {
                    {
                        super.bind(startX, startY, endX, endY);
                    }

                    @Override
                    protected double computeValue() {
                        double angle = Math.atan2(startY.get() - endY.get(), startX.get() - endX.get());
                        return startY.get() + Location.RADIUS * Math.sin(angle);
                    }
                }
        );

    }

*/
}
