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
    SUN();

    private static final double jLon = Angle.ofDeg(279.557208);
    private static final double perigeeLon = Angle.ofDeg(283.112438);
    private static final double exc = 0.016705;
    private static final double theta0 = Angle.ofDeg(0.533128);

    @Override
    public Sun at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        double meanAnomaly = (Angle.TAU * daysSinceJ2010) / 365.242191 + jLon - perigeeLon;
        double trueAnomaly = meanAnomaly + 2 * exc * Math.sin(meanAnomaly);
        EclipticCoordinates eclPos = EclipticCoordinates.of(Angle.normalizePositive(trueAnomaly + perigeeLon),0);
        EquatorialCoordinates eqPos = eclipticToEquatorialConversion.apply(eclPos);

        float angSize = (float) (theta0 * ((1 + exc * Math.cos(trueAnomaly))/(1-exc*exc)));
        return new Sun(eclPos, eqPos, angSize, (float) Angle.normalizePositive(meanAnomaly));

    }
}
