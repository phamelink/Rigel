package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableIntegerValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.Optional;

public class SkyCanvasManager {

    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 600;
    private static final TimeAccelerator DEFAULT_ACCELERATOR = NamedTimeAccelerator.TIMES_300.getAccelerator();
    private static final float ZOOM_FACTOR = 0.1f;
    private static final ClosedInterval FOV_BOUND = ClosedInterval.of(0, 180);



    private DateTimeBean dateTimeBean;
    private ObserverLocationBean observerLocation;
    private ViewingParametersBean viewingParameters;

    private ObservableObjectValue<Canvas> canvas;
    private ObservableObjectValue<SkyCanvasPainter> skyCanvasPainter;


    //Given
    private ObservableObjectValue<ObservedSky> observedSky;
    private ObservableObjectValue<StereographicProjection> projection;
    private ObservableObjectValue<Transform> planeToCanvas;
    private ObservableObjectValue<HorizontalCoordinates> mouseHorizontalPosition;
    private ObservableObjectValue<Point2D> mousePosition;
    private ObservableObjectValue<Point2D> mousePositionInPlane;


    //Additional
    private ObservableObjectValue<TimeAnimator> timeAnimator;
    private ObservableObjectValue<TimeAccelerator> timeAcc;
    private ObservableDoubleValue dilationFactor;




    public ObservableObjectValue<Optional<CelestialObject>> objectUnderMouse;
    public ObservableDoubleValue mouseAzDeg;
    public ObservableDoubleValue mouseAltDeg;

    private IntegerProperty mouseX;
    private IntegerProperty mouseY;




    public SkyCanvasManager(StarCatalogue starCatalogue, DateTimeBean dateTimeBean, ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {

        this.observerLocation = observerLocation;
        this.viewingParameters = viewingParameters;
        this.dateTimeBean = dateTimeBean;

        //Create canvas
        this.canvas = new SimpleObjectProperty<>(new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT));

        //Create bindings
        projection = Bindings.createObjectBinding(() -> new StereographicProjection(viewingParameters.getCenter()), viewingParameters.centerProperty());

        observedSky = Bindings.createObjectBinding(() ->new ObservedSky(this.dateTimeBean.getZonedDateTime(),
                        this.observerLocation.getCoordinates(), this.projection.get(), starCatalogue),this.dateTimeBean.dateProperty(), this.dateTimeBean.timeProperty(),
                        this.dateTimeBean.zoneIdProperty(), this.observerLocation.coordinatesProperty(),
                        this.projection);

        dilationFactor = Bindings.createDoubleBinding(() -> canvas.get().getWidth() /
                (2 * Math.tan(Angle.ofDeg(viewingParameters.getFieldOfViewDeg()) / 4)), canvas,
                viewingParameters.fieldOfViewDegProperty());

        planeToCanvas = Bindings.createObjectBinding(() -> Transform.affine(dilationFactor.get(), 0, 0, - dilationFactor.get(),
                canvas.get().getWidth() / 2,canvas.get().getHeight() / 2), canvas.get().widthProperty(), canvas.get().heightProperty(), dilationFactor );

        //Mouse listeners
        mouseX = new SimpleIntegerProperty(0);
        mouseY = new SimpleIntegerProperty(0);
        mousePosition = Bindings.createObjectBinding(() -> new Point2D(mouseX.get(), mouseY.get()), mouseX, mouseY);
        canvas.get().setOnMouseMoved((e) -> {mouseX.set((int) e.getX()); mouseY.set((int) e.getY());});
        canvas.get().setOnMousePressed((e) -> { if(e.isPrimaryButtonDown()) canvas.get().requestFocus(); });

        //Continue bindings
        mousePositionInPlane = Bindings.createObjectBinding(() ->
                planeToCanvas.get().inverseTransform(mousePosition.get().getX(),mousePosition.get().getY()), mousePosition);

        mouseHorizontalPosition = Bindings.createObjectBinding(() ->
               projection.get().inverseApply(CartesianCoordinates.of(mousePositionInPlane.get().getX(),
                       mousePositionInPlane.get().getY())), mousePositionInPlane);

        mouseAzDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().azDeg(), mouseHorizontalPosition);

        mouseAltDeg = Bindings.createDoubleBinding(() -> mouseHorizontalPosition.get().altDeg(), mouseHorizontalPosition);

        objectUnderMouse = Bindings.createObjectBinding(() ->
                observedSky.get().objectClosestTo(CartesianCoordinates.of(mousePositionInPlane.get().getX(),
                        mousePositionInPlane.get().getY()), planeToCanvas.get().inverseDeltaTransform(10, 0).getX()), mousePositionInPlane, observedSky);

        //Bind sky painter
        this.skyCanvasPainter = new SimpleObjectProperty<>(new SkyCanvasPainter(this.canvas.get()));
        this.timeAnimator = new SimpleObjectProperty<>(new TimeAnimator(this.dateTimeBean));
        this.timeAnimator.get().setAccelerator(DEFAULT_ACCELERATOR);
        observedSky.addListener((p,o,n) -> refreshCanvas());
        planeToCanvas.addListener((p,o,n) -> refreshCanvas());


        canvas.get().setOnScroll((e) -> {
            if(Math.abs(e.getDeltaX()) >= Math.abs(e.getDeltaY())){
                viewingParameters.setFieldOfViewDeg(FOV_BOUND.clip(viewingParameters.getFieldOfViewDeg() + ZOOM_FACTOR * e.getDeltaX()));
            }else{
                viewingParameters.setFieldOfViewDeg(FOV_BOUND.clip(viewingParameters.getFieldOfViewDeg() + ZOOM_FACTOR * e.getDeltaY()));
            }
        });

    }

    public void refreshCanvas(){
        skyCanvasPainter.get().drawAll(observedSky.get(), projection.get(), planeToCanvas.get());
    }

    public Canvas canvas() {
        return canvas.get();
    }

    public ObservableObjectValue<Canvas> canvasProperty() {
        return canvas;
    }

    public SkyCanvasPainter getSkyCanvasPainter() {
        return skyCanvasPainter.get();
    }

    public ObservableObjectValue<SkyCanvasPainter> skyCanvasPainterProperty() {
        return skyCanvasPainter;
    }

    public ObservedSky getObservedSky() {
        return observedSky.get();
    }

    public ObservableObjectValue<ObservedSky> observedSkyProperty() {
        return observedSky;
    }

    public StereographicProjection getProjection() {
        return projection.get();
    }

    public ObservableObjectValue<StereographicProjection> projectionProperty() {
        return projection;
    }

    public Transform getPlaneToCanvas() {
        return planeToCanvas.get();
    }

    public ObservableObjectValue<Transform> planeToCanvasProperty() {
        return planeToCanvas;
    }

    public HorizontalCoordinates getMouseHorizontalPosition() {
        return mouseHorizontalPosition.get();
    }

    public ObservableObjectValue<HorizontalCoordinates> mouseHorizontalPositionProperty() {
        return mouseHorizontalPosition;
    }

    public Point2D getMousePosition() {
        return mousePosition.get();
    }

    public ObservableObjectValue<Point2D> mousePositionProperty() {
        return mousePosition;
    }

    public Point2D getMousePositionInPlane() {
        return mousePositionInPlane.get();
    }

    public ObservableObjectValue<Point2D> mousePositionInPlaneProperty() {
        return mousePositionInPlane;
    }

    public TimeAnimator getTimeAnimator() {
        return timeAnimator.get();
    }

    public ObservableObjectValue<TimeAnimator> timeAnimatorProperty() {
        return timeAnimator;
    }

    public TimeAccelerator getTimeAcc() {
        return timeAcc.get();
    }

    public ObservableObjectValue<TimeAccelerator> timeAccProperty() {
        return timeAcc;
    }

    public Number getDilationFactor() {
        return dilationFactor.get();
    }

    public ObservableDoubleValue dilationFactorProperty() {
        return dilationFactor;
    }

    public Optional<CelestialObject> getObjectUnderMouse() {
        return objectUnderMouse.get();
    }

    public ObservableObjectValue<Optional<CelestialObject>> objectUnderMouseProperty() {
        return objectUnderMouse;
    }

    public Number getMouseAzDeg() {
        return mouseAzDeg.get();
    }

    public ObservableDoubleValue mouseAzDegProperty() {
        return mouseAzDeg;
    }

    public Number getMouseAltDeg() {
        return mouseAltDeg.get();
    }

    public ObservableDoubleValue mouseAltDegProperty() {
        return mouseAltDeg;
    }

}
