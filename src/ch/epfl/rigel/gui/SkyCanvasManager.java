package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

import java.util.List;
import java.util.Optional;

/**
 * Sky Canvas Manager
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class SkyCanvasManager {

    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 600;
    private static final float ZOOM_FACTOR = 0.05f;
    private static final ClosedInterval FOV_BOUND = ClosedInterval.of(30, 150);
    private static final double HORIZONTAL_MOVEMENT_DELTA = Angle.ofDeg(10);
    private static final double VERTICAL_MOVEMENT_DELTA = Angle.ofDeg(5);
    private static final ClosedInterval ALT_BOUND = ClosedInterval.of(Angle.ofDeg(5.0), Angle.ofDeg(90.0));

    private final DateTimeBean dateTimeBean;
    private final ObserverLocationBean observerLocation;

    //Graphical elements
    private final ObservableObjectValue<Canvas> canvas;
    private final ObservableObjectValue<SkyCanvasPainter> skyCanvasPainter;

    //Given
    private final ObservableObjectValue<ObservedSky> observedSky;
    private final ObservableObjectValue<StereographicProjection> projection;
    private final ObservableObjectValue<Transform> planeToCanvas;
    private final ObservableObjectValue<HorizontalCoordinates> mouseHorizontalPosition;
    private final ObjectProperty<Point2D> mousePosition;
    private final ObservableObjectValue<Point2D> mousePositionInPlane;

    private final ObservableObjectValue<TimeAnimator> timeAnimator;
    private final ObjectProperty<TimeAccelerator> timeAcc;
    private final ObservableDoubleValue dilationFactor;

    private final ObservableObjectValue<Optional<CelestialObject>> objectUnderMouse;
    private final ObjectProperty<Optional<CelestialObject>> lastObjectInspected;
    private final ObservableDoubleValue mouseAzDeg;
    private final ObservableDoubleValue mouseAltDeg;
    private final BooleanProperty mousePresentOverPane;
    private Point2D lastMouseDragPosition;


    /**
     * Sky canvas manager constructor
     * @param starCatalogue Star catalogue
     * @param dateTimeBean Date time bean
     * @param observerLocation Observer location bean
     * @param viewingParameters Viewing parameters bean
     */
    public SkyCanvasManager(StarCatalogue starCatalogue, DateTimeBean dateTimeBean, ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {

        this.observerLocation = observerLocation;
        this.dateTimeBean = dateTimeBean;

        //Create canvas
        this.canvas = new SimpleObjectProperty<>(new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT));
        canvas.get().getGraphicsContext2D();


        //Create bindings for projection and observed sky
        projection = Bindings.createObjectBinding(() -> new StereographicProjection(viewingParameters.getCenter()), viewingParameters.centerProperty());

        observedSky = Bindings.createObjectBinding(() ->new ObservedSky(this.dateTimeBean.getZonedDateTime(),
                        this.observerLocation.getCoordinates(), this.projection.get(), starCatalogue),
                        this.dateTimeBean.dateProperty(), this.dateTimeBean.timeProperty(),
                        this.dateTimeBean.zoneIdProperty(), this.observerLocation.coordinatesProperty(),
                        this.projection);


        //Create bindings for transformation properties
        dilationFactor = Bindings.createDoubleBinding(() -> canvas.get().getWidth() /
                (2 * Math.tan(Angle.ofDeg(viewingParameters.getFieldOfViewDeg()) / 4)),
                viewingParameters.fieldOfViewDegProperty(), canvas.get().widthProperty());

        Bindings.createDoubleBinding(() -> canvas.get().getHeight() /
                        (2 * Math.tan(Angle.ofDeg(viewingParameters.getFieldOfViewDeg()) / 4)),
                viewingParameters.fieldOfViewDegProperty(), canvas.get().heightProperty());

        planeToCanvas = Bindings.createObjectBinding(() -> Transform.affine(dilationFactor.get(), 0, 0,
                - dilationFactor.get(), canvas.get().getWidth() / 2,canvas.get().getHeight() / 2),
                canvas.get().widthProperty(), canvas.get().heightProperty(), dilationFactor);


        //Mouse listeners and bindings

        mousePosition = new SimpleObjectProperty<>(new Point2D(0,0));
        canvas.get().setOnMouseMoved((e) -> mousePosition.set(new Point2D(e.getX(), e.getY())));


        canvas.get().setOnMousePressed((e) -> { if(e.isPrimaryButtonDown()) canvas.get().requestFocus(); });

        mousePresentOverPane = new SimpleBooleanProperty(false);

        canvas.get().hoverProperty().addListener((p, o, isHovering) -> mousePresentOverPane.setValue(isHovering));

        mousePositionInPlane = Bindings.createObjectBinding(() ->
                planeToCanvas.get().inverseTransform(mousePosition.get().getX(),mousePosition.get().getY()),
                mousePosition, viewingParameters.fieldOfViewDegProperty());

        mouseHorizontalPosition = Bindings.createObjectBinding(() ->
                projection.get().inverseApply(CartesianCoordinates.of(mousePositionInPlane.get().getX(),
                       mousePositionInPlane.get().getY())), mousePositionInPlane);

        mouseAzDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);

        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);


        objectUnderMouse = Bindings.createObjectBinding(() -> {

            if (mousePresentOverPane.getValue()) {
                return observedSky.get().objectClosestTo(CartesianCoordinates.of(mousePositionInPlane.get().getX(),
                        mousePositionInPlane.get().getY()), planeToCanvas.get().inverseDeltaTransform(10, 0).getX());
            } else {
                return Optional.empty();
            }
        }, mousePositionInPlane, observedSky, mousePresentOverPane);

        //Bind sky painter
        this.skyCanvasPainter = new SimpleObjectProperty<>(new SkyCanvasPainter(this.canvas.get()));
        timeAcc = new SimpleObjectProperty<>();
        this.timeAnimator = new SimpleObjectProperty<> (new TimeAnimator(this.dateTimeBean));
        timeAcc.addListener((p,o,n) -> timeAnimator.getValue().setAccelerator(n));

        //Set sensitivities for canvas refresh
        observedSky.addListener((p,o,n) -> refreshCanvas());
        planeToCanvas.addListener((p,o,n) -> refreshCanvas());
        objectUnderMouse.addListener((p,o,n) -> refreshCanvas());
        addRefreshSensibilities(getSkyCanvasPainter().getRenderingProperties());

        //Bind keyboard controls
        canvas.get().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()){
                case UP:
                    viewingParameters.setCenter(HorizontalCoordinates
                            .of(Angle.normalizePositive(viewingParameters.getCenter().az()),
                                    ALT_BOUND.clip(viewingParameters.getCenter().alt() + VERTICAL_MOVEMENT_DELTA)));
                    break;
                case DOWN:
                    viewingParameters.setCenter(HorizontalCoordinates
                            .of(Angle.normalizePositive(viewingParameters.getCenter().az()),
                                    ALT_BOUND.clip(viewingParameters.getCenter().alt() - VERTICAL_MOVEMENT_DELTA)));
                    break;
                case LEFT:
                    viewingParameters.setCenter(HorizontalCoordinates
                            .of(Angle.normalizePositive(viewingParameters.getCenter().az() - HORIZONTAL_MOVEMENT_DELTA) ,
                                    ALT_BOUND.clip(viewingParameters.getCenter().alt())));
                    break;
                case RIGHT:
                    viewingParameters.setCenter(HorizontalCoordinates
                            .of(Angle.normalizePositive(viewingParameters.getCenter().az() + HORIZONTAL_MOVEMENT_DELTA),
                                    ALT_BOUND.clip(viewingParameters.getCenter().alt())));
                    break;


            }
            event.consume();
        });

        lastObjectInspected = new SimpleObjectProperty<>(Optional.empty());

        //Bind click and drag controls
        canvas.get().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            canvas.get().requestFocus();
            if (event.isSecondaryButtonDown()) {
                lastMouseDragPosition = new Point2D(event.getX(), event.getY()); //Set initial drag position
            } else if (event.isPrimaryButtonDown() ) {
                //Prevent selection of objects not displayed
                if( getObjectUnderMouse().isEmpty()
                        || (getSkyCanvasPainter().isStarsEnabled() && getObjectUnderMouse().get() instanceof Star)
                        || (getSkyCanvasPainter().isPlanetsEnabled() && getObjectUnderMouse().get() instanceof Planet)
                        || (getSkyCanvasPainter().isMoonEnabled() && getObjectUnderMouse().get() instanceof Moon)
                        || (getSkyCanvasPainter().isSunEnabled() && getObjectUnderMouse().get() instanceof Sun)) {
                    lastObjectInspected.set(objectUnderMouse.get());
                }else{
                    lastObjectInspected.set(Optional.empty());
                }
            }
        });

        /*
        BONUS
         */

        canvas.get().addEventFilter(MouseEvent.MOUSE_DRAGGED, event-> {
            if(event.isSecondaryButtonDown()) {
                Point2D nextPoint = new Point2D(event.getX(), event.getY());
                //Get drag movement delta from last drag position
                double deltaX = lastMouseDragPosition.getX() - nextPoint.getX();
                double deltaY = lastMouseDragPosition.getY() - nextPoint.getY();
                //Get the new center using inverse functions to get a smoot drag dependent on viewing parameters
                try {
                    Point2D pointFromPlane  = getPlaneToCanvas().inverseDeltaTransform(deltaX, deltaY);
                    HorizontalCoordinates newCoords = getProjection().inverseApply(CartesianCoordinates.of(pointFromPlane.getX(), pointFromPlane.getY()));
                    viewingParameters.setCenter(HorizontalCoordinates.of(newCoords.az(), ALT_BOUND.clip(newCoords.alt())));
                } catch (NonInvertibleTransformException e) {
                    e.printStackTrace();
                }
                lastMouseDragPosition = nextPoint;
            }
        });

        //Bind scroll controls
        canvas.get().setOnScroll((e) -> {
            if (Math.abs(e.getDeltaX()) >= Math.abs(e.getDeltaY())) {
                viewingParameters.setFieldOfViewDeg(FOV_BOUND
                        .clip(viewingParameters.getFieldOfViewDeg() - ZOOM_FACTOR * e.getDeltaX()));
            } else {
                viewingParameters.setFieldOfViewDeg(FOV_BOUND
                        .clip(viewingParameters.getFieldOfViewDeg() - ZOOM_FACTOR * e.getDeltaY()));
            }
            e.consume();
        });

    }

    private void addRefreshSensibilities(List<Property<Boolean>> sensibilities){
        for(Property<Boolean> s : sensibilities){
            s.addListener((p,o,n) -> refreshCanvas());
        }
    }


    /**
     * Refreshes canvas by clearing it and drawings all elements
     */
    public void refreshCanvas(){
        skyCanvasPainter.get().draw(observedSky.get(), projection.get(), planeToCanvas.get());
    }

    /**
     * Returns the sky canvas manager's canvas
     * @return Sky canvas manager's canvas
     */
    public Canvas getCanvas() {
        return canvas.get();
    }

    /**
     * Returns this sky canvas manager's canvas observable object value
     * @return Sky canvas manager's canvas observable object value
     */
    public ObservableObjectValue<Canvas> canvasProperty() {
        return canvas;
    }


    /**
     * Getter for this sky canvas manager's canvas painter
     * @return Sky canvas manager's canvas painter
     */
    public SkyCanvasPainter getSkyCanvasPainter() {
        return skyCanvasPainter.get();
    }

    /**
     * Returns this sky canvas manager's sky canvas painter observable object value
     * @return Sky canvas manager's sky canvas painter observable object value
     */
    public ObservableObjectValue<SkyCanvasPainter> skyCanvasPainterProperty() {
        return skyCanvasPainter;
    }

    /**
     * Getter for observed sky
     * @return Observed sky
     */
    public ObservedSky getObservedSky() {
        return observedSky.get();
    }

    /**
     * Returns observed sky property as observable object value
     * @return Observed sky property as observable object value
     */
    public ObservableObjectValue<ObservedSky> observedSkyProperty() {
        return observedSky;
    }

    /**
     * Getter for this sky manager's stereographic projection
     * @return Stereographic projection
     */
    public StereographicProjection getProjection() {
        return projection.get();
    }

    /**
     * Returns this sky canvas manager's stereographic projection observable object value
     * @return Stereographic projection observable object value
     */
    public ObservableObjectValue<StereographicProjection> projectionProperty() {
        return projection;
    }

    /**
     * Getter for plane to canvas transform
     * @return Plane to canvas transform
     */
    public Transform getPlaneToCanvas() {
        return planeToCanvas.get();
    }

    /**
     * Returns plane to canvas transform observable object value
     * @return Plane to canvas transform observable object value
     */
    public ObservableObjectValue<Transform> planeToCanvasProperty() {
        return planeToCanvas;
    }

    /**
     * Getter for horizontal mouse position in horizontal coordinates
     * @return Horizontal mouse position in horizontal coordinates
     */
    public HorizontalCoordinates getMouseHorizontalPosition() {
        return mouseHorizontalPosition.get();
    }

    /**
     * Returns horizontal mouse position property as observable object value
     * @return Horizontal mouse position property as observable object value
     */
    public ObservableObjectValue<HorizontalCoordinates> mouseHorizontalPositionProperty() {
        return mouseHorizontalPosition;
    }

    /**
     * Getter for a Point2D of the mouse's position
     * @return Point2D of the mouse's position
     */
    public Point2D getMousePosition() {
        return mousePosition.get();
    }

    /**
     * Returns the mouse position property as observable object value
     * @return Mouse position property as observable object value
     */
    public ObservableObjectValue<Point2D> mousePositionProperty() {
        return mousePosition;
    }

    /**
     * Getter for a Point2d of the mouse position in plane
     * @return Point2d of the mouse position in plane
     */
    public Point2D getMousePositionInPlane() {
        return mousePositionInPlane.get();
    }

    /**
     * Returns the mouse position in plane property as observable object value
     * @return Mouse position in plane property as observable object value
     */
    public ObservableObjectValue<Point2D> mousePositionInPlaneProperty() {
        return mousePositionInPlane;
    }

    /**
     * Getter for time animator
     * @return Time animator
     */
    public TimeAnimator getTimeAnimator() {
        return timeAnimator.get();
    }

    /**
     * Returns time animator property as observable object value
     * @return Time animator property as observable object value
     */
    public ObservableObjectValue<TimeAnimator> timeAnimatorProperty() {
        return timeAnimator;
    }

    /**
     * Getter for current time animator's accelerator
     * @return Time animator's accelerator
     */
    public TimeAccelerator getTimeAcc() {
        return timeAcc.get();
    }

    /**
     * Returns the time accelerator object property
     * @return Time accelerator object property
     */
    public ObjectProperty<TimeAccelerator> timeAccProperty() {
        return timeAcc;
    }

    /**
     * Setter for the time accelerator property
     * @param timeAcc new time accelerator
     */
    public void setTimeAcc(TimeAccelerator timeAcc) {
        this.timeAcc.set(timeAcc);
    }

    /**
     * Getter for the dilatation factor
     * @return Dilatation factor
     */
    public Number getDilationFactor() {
        return dilationFactor.get();
    }

    /**
     * Returns dilatation factor property as observable double value
     * @return Dilatation factor property as observable double value
     */
    public ObservableDoubleValue dilationFactorProperty() {
        return dilationFactor;
    }

    /**
     * Getter for an optional object under mouse
     * @return Optional object under mouse
     */
    public Optional<CelestialObject> getObjectUnderMouse() {
        return objectUnderMouse.get();
    }

    /**
     * Returns object under mouse property as observable object value
     * @return Object under mouse property as observable object value
     */
    public ObservableObjectValue<Optional<CelestialObject>> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    /**
     * Getter for mouse azimuth coordinate
     * @return Mouse azimuth coordinate
     */
    public Number getMouseAzDeg() {
        return mouseAzDeg.get();
    }

    /**
     * Returns mouse azimuth coordinate property as observable double value
     * @return Mouse azimuth coordinate property as observable double value
     */
    public ObservableDoubleValue mouseAzDegProperty() {
        return mouseAzDeg;
    }

    /**
     * Getter for mouse altitude coordinate
     * @return Mouse altitude coordinate
     */
    public Number getMouseAltDeg() {
        return mouseAltDeg.get();
    }

    /**
     * Returns mouse altitude coordinate property as observable double value
     * @return Mouse altitude coordinate property as observable double value
     */
    public ObservableDoubleValue mouseAltDegProperty() {
        return mouseAltDeg;
    }

    /**
     * Returns boolean indicating if the mouse pointer is over the canvas
     * @return boolean indicating if the mouse pointer is over the canvas
     */
    public boolean isMousePresentOverPane() {return mousePresentOverPane.get(); }

    /**
     * Returns boolean property indicating if the mouse pointer is over the canvas
     * @return boolean property indicating if the mouse pointer is over the canvas
     */
    public ObservableBooleanValue mousePresentOverPaneProperty() {return mousePresentOverPane;}

    /**
     * Returns time animator's running property as observable boolean value
     * @return Time animator's running property as observable boolean value
     */
    public ObservableBooleanValue timeAnimatorRunningProperty() {
        return timeAnimator.get().getRunningProperty();
    }

    /**
     * Getter for boolean value of time animator's running
     * @return Boolean value of time animator's running
     */
    public boolean getTimeAnimatorRunning() {
        return timeAnimator.get().getRunning();
    }

    /**
     * Returns last object inspected (can be empty)
     * @return last object inspected
     */
    public Optional<CelestialObject> getLastObjectInspected() {
        return lastObjectInspected.get();
    }

    /**
     * Returns property indicating the last object inspected
     * @return last object inspected property
     */
    public ObjectProperty<Optional<CelestialObject>> lastObjectInspectedProperty() {
        return lastObjectInspected;
    }
}
