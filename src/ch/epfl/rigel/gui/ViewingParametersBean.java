package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

public class ViewingParametersBean {
    private DoubleProperty fieldOfViewDeg;
    private ObjectProperty<HorizontalCoordinates> center;

    public ViewingParametersBean(double fieldOfViewDeg, HorizontalCoordinates hc) {
        this.fieldOfViewDeg = new SimpleDoubleProperty(fieldOfViewDeg);
        this.center = new SimpleObjectProperty<>(hc);
    }

    public void setFieldOfViewDeg(double fieldOfViewDeg) {
        this.fieldOfViewDeg.set(fieldOfViewDeg);
    }

    public void setCenter(HorizontalCoordinates center) {
        this.center.set(center);
    }

    public double getFieldOfViewDegProperty() {
        return fieldOfViewDeg.get();
    }

    public DoubleProperty fieldOfViewDegPropertyProperty() {
        return fieldOfViewDeg;
    }

    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }
}
