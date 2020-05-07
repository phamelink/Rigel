package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;
import javafx.beans.value.ObservableObjectValue;
import javafx.scene.canvas.Canvas;
import javafx.scene.transform.Transform;

import java.io.InputStream;
import java.time.ZonedDateTime;

public class SkyCanvasManager {

    private

    private DateTimeBean dateTimeBean;
    private ObserverLocationBean observerLocation;
    private ViewingParametersBean viewingParameters;

    private ObservableObjectValue<ZonedDateTime> dateTime;
    private Canvas canvas;

    private ObservableObjectValue<ObservedSky> observedSky;
    private ObservableObjectValue<StereographicProjection> projection;
    private ObservableObjectValue<Transform> planeToCanvas;
    private ObservableObjectValue<HorizontalCoordinates> mouseHorizontalPosition;
    private ObservableObjectValue<CartesianCoordinates> mousePosition;

    public ObservableObjectValue<CelestialObject> objectUnderMouse;
    public ObservableDoubleValue mouseAzDeg;
    public ObservableDoubleValue mouseAltDeg;

    public SkyCanvasManager(StarCatalogue starCatalogue, DateTimeBean dateTime, ObserverLocationBean observerLocation, ViewingParametersBean viewingParameters) {

        this.observerLocation = observerLocation;
        this.viewingParameters = viewingParameters;
        this.dateTime = new SimpleObjectProperty<>(dateTime);
        this.canvas = new SimpleObjectProperty<>(canvas);


        projection = Bindings.createObjectBinding(() -> new StereographicProjection(viewingParameters.centerProperty().get()), viewingParameters.centerProperty());
        mousePosition = Bindings.createObjectBinding(() -> CartesianCoordinates.of()
        observedSky = Bindings.createObjectBinding(() -> new ObservedSky(this.dateTime.get(),
                this.observerLocation.coordinatesProperty().get(), this.projection.get(), starCatalogue),
                this.dateTime, this.observerLocation.coordinatesProperty(), this.projection);

    }
}
