package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * Celestial object
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public abstract class CelestialObject {
    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;

    /**
     * Constructs a celestial object with it's name, situated in it's equatorial coordinates equatorialPos of
     * angular size angularSize and of it's magnitude
     * @param name
     *          name of object
     * @param equatorialPos
     *          equatorial coordinates of object
     * @param angularSize
     *          angular size of object
     * @param magnitude
     *          magnitude of object
     * @throws IllegalArgumentException if angular size is negative
     * @throws NullPointerException if name or equatorialPos is null
     */
    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        if (angularSize < 0) throw new IllegalArgumentException("Angular size is negative");
        Objects.requireNonNull(equatorialPos,"Equatorial position is null");
        Objects.requireNonNull(name, "Name is null");
        this.name = name;
        this.equatorialPos = equatorialPos;
        this.angularSize = angularSize;
        this.magnitude = magnitude;

    }

    /**
     * returns name of celestial object
     * @return name of celestial object
     */
    public String name() {
        return name;
    }

    /**
     * returns Equatorial coordinates of object
     * @return Equatorial coordinates of object
     */
    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    /**
     * returns angular size of object
     * @return angular size of object
     */
    public double angularSize() {
        return angularSize;
    }

    /**
     * returns magnitude of object
     * @return magnitude of object
     */
    public double magnitude() {
        return magnitude;
    }

    /**
     * returns information about celestial object
     * @return information about celestial object
     */
    public String info(){
        return name();
    }

    @Override
    public String toString() {
        return info();
    }
}
