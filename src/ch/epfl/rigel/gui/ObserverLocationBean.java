package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableObjectValue;

/**
 * Observer Location Bean
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class ObserverLocationBean {
    private final DoubleProperty lonDeg;
    private final DoubleProperty latDeg;
    private final ObservableObjectValue<GeographicCoordinates> coordinates;

    /**
     * Observer Location Bean constructor initializing the location with given latitude and longitude in degrees
     * @param lonDeg Initial observer longitude coordinates (in degrees)
     * @param latDeg Initial observer latitude coordinates (in degrees)
     */
    public ObserverLocationBean(double lonDeg, double latDeg) {
        this.lonDeg = new SimpleDoubleProperty(lonDeg);
        this.latDeg = new SimpleDoubleProperty(latDeg);
        this.coordinates = Bindings.createObjectBinding(() -> GeographicCoordinates.ofDeg(this.lonDeg.get(), this.latDeg.get()), this.lonDeg, this.latDeg);
    }

    /**
     * Default bean constructor with initial coordinates at (0,0)
     */
    public ObserverLocationBean() {
        this(0,0);
    }

    /**
     * Observer Location Bean constructor initializing the location with given Geographic coordinates
     * @param where Geographic coordinates of observer's initial location
     */
    public ObserverLocationBean(GeographicCoordinates where) {
        this();
        setCoordinates(where);
    }

    /**
     * Setter for longitue
     * @param lonDeg
     */
    public void setLonDeg(double lonDeg) {
        System.out.println(lonDeg);
        this.lonDeg.set(lonDeg);
    }

    /**
     * Setter for latitude
     * @param latDeg
     */
    public void setLatDeg(double latDeg) {
        this.latDeg.set(latDeg);
    }

    /**
     * Getter for longitude value
     * @return Longitude value
     */
    public double getLonDeg() {
        return lonDeg.get();
    }

    /**
     * Getter for longitude property
     * @return Longitude property
     */
    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }

    /**
     * Getter for latitude value
     * @return Latitude value
     */
    public double getLatDeg() {
        return latDeg.get();
    }

    /**
     * Getter for latitude property
     * @return Latitude Property
     */
    public DoubleProperty latDegProperty() {
        return latDeg;
    }

    /**
     * Getter for geographic coordinates value
     * @return Geographic coordinates value
     */
    public GeographicCoordinates getCoordinates() {
        return coordinates.get();
    }

    /**
     * Getter for geographic coordinates property as Observable Object Value
     * @return Geographic coordinates property as Observable Object Value
     */
    public ObservableObjectValue<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }

    /**
     * Setter for geographic coordinates
     * @param ofDeg Geographic Coordinates
     */
    public void setCoordinates(GeographicCoordinates ofDeg) {
        this.lonDeg.set(ofDeg.lonDeg());
        this.latDeg.set(ofDeg.latDeg());
    }
}
