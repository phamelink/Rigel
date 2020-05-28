package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Viewing Parameters Bean
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class ViewingParametersBean {
    private final DoubleProperty fieldOfViewDeg;
    private final ObjectProperty<HorizontalCoordinates> center;

    /**
     * ViewingParametersBean constructor
     * @param fieldOfViewDeg Initial FOV
     * @param hc Horizontal coordinates for the center of the projection
     */
    public ViewingParametersBean(double fieldOfViewDeg, HorizontalCoordinates hc) {
        this.fieldOfViewDeg = new SimpleDoubleProperty(fieldOfViewDeg);
        this.center = new SimpleObjectProperty<>(hc);
    }

    /**
     * Default ViewingParametersBean constructor initializing FOV at 68.4° and center at (0,0) as default values
     */
    public ViewingParametersBean() {
        this(68.4, HorizontalCoordinates.ofDeg(0,0));
    }

    /**
     * Setter for FOV with a given angle in degrees
     * @param fieldOfViewDeg FOV to set in degrees
     */
    public void setFieldOfViewDeg(double fieldOfViewDeg) {
        this.fieldOfViewDeg.set(fieldOfViewDeg);
    }

    /**
     * Setter for projection center
     * @param center Projection center in horizontal coordinates
     */
    public void setCenter(HorizontalCoordinates center) {
        this.center.set(center);
    }

    /**
     * Getter for FOV in degrees
     * @return FOV in degrees
     */
    public double getFieldOfViewDeg() {
        return fieldOfViewDeg.get();
    }

    /**
     * Getter for FOV property
     * @return FOV property
     */
    public DoubleProperty fieldOfViewDegProperty() {
        return fieldOfViewDeg;
    }

    /**
     * Getter for projection center in horizontal coordinates
     * @return Projection center in horizontal coordinates
     */
    public HorizontalCoordinates getCenter() {
        return center.get();
    }

    /**
     * Getter for projection center property
     * @return Projection center property
     */
    public ObjectProperty<HorizontalCoordinates> centerProperty() {
        return center;
    }


}
