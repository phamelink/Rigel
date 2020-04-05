package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

/**
 * Celestial Object Model
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public interface CelestialObjectModel<O> {

    /**
     * returns modelised object by the model for the given number of days (eventually negative) since the epoch J2010
     * by using the conversion given to obtain its equatorial coordinates from the ecliptic coordinates
     * @param daysSinceJ2010 number of days (can be negative) since the epoch J2010
     * @param eclipticToEquatorialConversion the conversion given to obtain its equatorial coordinates from the ecliptic coordinates
     * @return modelised object by the model
     */
    O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);

}
