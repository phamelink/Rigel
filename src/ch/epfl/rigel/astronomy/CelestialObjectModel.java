package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;

public interface CelestialObjectModel<O> {

    abstract public O at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion);

}
