package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

/**
 * Planet
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class Planet extends CelestialObject{

    /**
     * Constructs a planet with it's name, situated in it's equatorial coordinates equatorialPos of
     * angular size angularSize and of it's magnitude
     * @param name
     *          name of planet
     * @param equatorialPos
     *          equatorial coordinates of planet
     * @param angularSize
     *          angular size of planet
     * @param magnitude
     *          magnitude of planet
     * @throws IllegalArgumentException if angular size is negative
     * @throws NullPointerException if name or equatorialPos is null
     */
    public Planet(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        super(name, equatorialPos, angularSize, magnitude);
    }
}
