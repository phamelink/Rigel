package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

/**
 * Sun Model
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public enum SunModel implements CelestialObjectModel<Sun> {
    SUN(Angle.ofDeg(279.557208), Angle.ofDeg(283.112438), 0.016705, Angle.ofDeg(0.533128));

    private final double jLon;
    private final double perigeeLon;
    private final double exc;
    private final double theta0;

    /**
     * Constructor of a Sun Model (only one instance exists)
     * @param jLon longitude at J2010
     * @param perigeeLon longitude of perigee
     * @param exc excentricity
     * @param theta0 constant for angular size
     */
    SunModel(double jLon, double perigeeLon, double exc, double theta0) {
        this.jLon = jLon;
        this.perigeeLon = perigeeLon;
        this.exc = exc;
        this.theta0 = theta0;
    }

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        final double meanAnomaly = (Angle.TAU * daysSinceJ2010) / 365.242191 + jLon - perigeeLon;
        final double trueAnomaly = meanAnomaly + 2 * exc * Math.sin(meanAnomaly);
        EclipticCoordinates eclPos = EclipticCoordinates.of(Angle.normalizePositive(trueAnomaly + perigeeLon),0);
        EquatorialCoordinates eqPos = eclipticToEquatorialConversion.apply(eclPos);

        final float angSize = (float) (theta0 * ((1 + exc * Math.cos(trueAnomaly))/(1-exc*exc)));
        return new Sun(eclPos, eqPos, angSize, (float) Angle.normalizePositive(meanAnomaly));

    }
}
