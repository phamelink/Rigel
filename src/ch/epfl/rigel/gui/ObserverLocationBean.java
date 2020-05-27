package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableObjectValue;

public class ObserverLocationBean {
    private DoubleProperty lonDeg;
    private DoubleProperty latDeg;
    private ObservableObjectValue<GeographicCoordinates> coordinates;

    public ObserverLocationBean(double lonDeg, double latDeg) {
        this.lonDeg = new SimpleDoubleProperty(lonDeg);
        this.latDeg = new SimpleDoubleProperty(latDeg);
        this.coordinates = Bindings.createObjectBinding(() -> GeographicCoordinates.ofDeg(this.lonDeg.get(), this.latDeg.get()), this.lonDeg, this.latDeg);
    }

    public ObserverLocationBean() {
        this(0,0);
    }

    public ObserverLocationBean(GeographicCoordinates where) {
        this();
        setCoordinates(where);
    }

    public void setLonDeg(double lonDeg) {
        System.out.println(lonDeg);
        this.lonDeg.set(lonDeg);
    }

    public void setLatDeg(double latDeg) {
        this.latDeg.set(latDeg);
    }

    public double getLonDeg() {
        return lonDeg.get();
    }

    public DoubleProperty lonDegProperty() {
        return lonDeg;
    }

    public double getLatDeg() {
        return latDeg.get();
    }

    public DoubleProperty latDegProperty() {
        return latDeg;
    }

    public GeographicCoordinates getCoordinates() {
        return coordinates.get();
    }

    public ObservableObjectValue<GeographicCoordinates> coordinatesProperty() {
        return coordinates;
    }

    public void setCoordinates(GeographicCoordinates ofDeg) {
        this.lonDeg.set(ofDeg.lonDeg());
        this.latDeg.set(ofDeg.latDeg());
    }
}
