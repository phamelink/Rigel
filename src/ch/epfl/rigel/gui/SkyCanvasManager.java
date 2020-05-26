package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.CelestialObject;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.EquatorialToHorizontalConversion;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.util.Optional;

public class SkyCanvasManager {

    private static final double CANVAS_WIDTH = 800;
    private static final double CANVAS_HEIGHT = 600;
    private static final float ZOOM_FACTOR = 0.05f;
    private static final ClosedInterval FOV_BOUND = ClosedInterval.of(30, 150);
    private static final double MOVEMENT_DELTA = Angle.ofDeg(1.0);
    private static final ClosedInterval ALT_BOUND = ClosedInterval.of(Angle.ofDeg(5.0), Angle.ofDeg(90.0));
    private static final double CONTROL_FACTOR_X = 1.32;
    private static final double CONTROL_FACTOR_Y = 0.74;


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
    private ObjectProperty<TimeAccelerator> timeAcc;
    private ObservableDoubleValue dilationFactor;
    private ObservableDoubleValue dilationFactorY;




    public ObservableObjectValue<Optional<CelestialObject>> objectUnderMouse;
    public ObservableDoubleValue mouseAzDeg;
    public ObservableDoubleValue mouseAltDeg;

    private IntegerProperty mouseX;
    private IntegerProperty mouseY;
    private Point2D lastMouseDragPosition;
    private GraphicsContext gc;




    public SkyCanvasManager(StarCatalogue starCatalogue, DateTimeBean dateTimeBean, ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {

        this.observerLocation = observerLocation;
        this.viewingParameters = viewingParameters;
        this.dateTimeBean = dateTimeBean;

        //Create canvas
        this.canvas = new SimpleObjectProperty<>(new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT));
        gc = canvas.get().getGraphicsContext2D();

        //Create bindings
        projection = Bindings.createObjectBinding(() -> new StereographicProjection(viewingParameters.getCenter()), viewingParameters.centerProperty());

        observedSky = Bindings.createObjectBinding(() ->new ObservedSky(this.dateTimeBean.getZonedDateTime(),
                        this.observerLocation.getCoordinates(), this.projection.get(), starCatalogue),this.dateTimeBean.dateProperty(), this.dateTimeBean.timeProperty(),
                        this.dateTimeBean.zoneIdProperty(), this.observerLocation.coordinatesProperty(),
                        this.projection);

        dilationFactor = Bindings.createDoubleBinding(() -> canvas.get().getWidth() /
                (2 * Math.tan(Angle.ofDeg(viewingParameters.getFieldOfViewDeg()) / 4)), canvas,
                viewingParameters.fieldOfViewDegProperty());

        dilationFactorY = Bindings.createDoubleBinding(() -> canvas.get().getHeight() /
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
        observedSky.addListener((p,o,n) -> refreshCanvas());
        planeToCanvas.addListener((p,o,n) -> refreshCanvas());
        timeAcc = new SimpleObjectProperty<>();
        timeAcc.addListener((p,o,n) -> this.timeAnimator.get().setAccelerator(n));


        //Bind controls
        canvas.get().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()){
                case UP:
                    viewingParameters.setCenter(HorizontalCoordinates.of(Angle.normalizePositive(viewingParameters.getCenter().az()), ALT_BOUND.clip(viewingParameters.getCenter().alt() + MOVEMENT_DELTA)));
                    event.consume();
                    break;
                case DOWN:
                    viewingParameters.setCenter(HorizontalCoordinates.of(Angle.normalizePositive(viewingParameters.getCenter().az()), ALT_BOUND.clip(viewingParameters.getCenter().alt() - MOVEMENT_DELTA)));
                    event.consume();
                    break;
                case LEFT:
                    viewingParameters.setCenter(HorizontalCoordinates.of(Angle.normalizePositive(viewingParameters.getCenter().az() - MOVEMENT_DELTA) , ALT_BOUND.clip(viewingParameters.getCenter().alt())));
                    event.consume();
                    break;
                case RIGHT:
                    viewingParameters.setCenter(HorizontalCoordinates.of(Angle.normalizePositive(viewingParameters.getCenter().az() + MOVEMENT_DELTA), ALT_BOUND.clip(viewingParameters.getCenter().alt())));
                    event.consume();
                    break;
                default:
                    event.consume();
                    break;

            }
        });

        //Bind drag controls

        canvas.get().addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
            if(event.isSecondaryButtonDown()) {
                lastMouseDragPosition = new Point2D(event.getX(), event.getY());
            }else if(event.isPrimaryButtonDown() && objectUnderMouse.get().isPresent()){
                refreshCanvas();
                CartesianCoordinates objPos = new StereographicProjection(viewingParameters.getCenter()).apply(new EquatorialToHorizontalConversion(dateTimeBean.getZonedDateTime(), observerLocation.getCoordinates()).apply(objectUnderMouse.get().get().equatorialPos()));
                Point2D posTrans = planeToCanvas.get().transform(objPos.x(), objPos.y());
                gc.setFill(Color.WHITE);
                gc.fillRoundRect(posTrans.getX() + 5, posTrans.getY() - 55,200, 50, 2, 2);
                gc.setFill(Color.BLACK);
                gc.fillText(objectUnderMouse.get().get().name(), posTrans.getX() + 15, posTrans.getY() - 40 );
                gc.fillText("Magnitude: " + objectUnderMouse.get().get().magnitude(), posTrans.getX() + 15, posTrans.getY() - 20 );
            }
        });


        canvas.get().addEventFilter(MouseEvent.MOUSE_DRAGGED, event-> {
            if(event.isSecondaryButtonDown()) {

                System.out.println(lastMouseDragPosition);
                Point2D nextPoint = new Point2D(event.getX(), event.getY());
                double deltaX = lastMouseDragPosition.getX() - nextPoint.getX();
                double deltaY = lastMouseDragPosition.getY() - nextPoint.getY();

                double deltaPlaneX = 4 * Math.atan(deltaX / (2* dilationFactor.get())) * CONTROL_FACTOR_X;
                double deltaPlaneY = -4 * Math.atan(deltaY / (2* dilationFactorY.get())) * CONTROL_FACTOR_Y;
                HorizontalCoordinates newCoords = viewingParameters.getCenter().delta(deltaPlaneX, deltaPlaneY);
                viewingParameters.setCenter(HorizontalCoordinates.of(newCoords.az(), ALT_BOUND.clip(newCoords.alt())));

                lastMouseDragPosition = nextPoint;
            }
        });

        canvas.get().setOnScroll((e) -> {
            System.out.println("scroll event");
            if(Math.abs(e.getDeltaX()) >= Math.abs(e.getDeltaY())){
                viewingParameters.setFieldOfViewDeg(FOV_BOUND.clip(viewingParameters.getFieldOfViewDeg() - ZOOM_FACTOR * e.getDeltaX()));
            }else{
                viewingParameters.setFieldOfViewDeg(FOV_BOUND.clip(viewingParameters.getFieldOfViewDeg() - ZOOM_FACTOR * e.getDeltaY()));
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

    public ObjectProperty<TimeAccelerator> timeAccProperty() {
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
