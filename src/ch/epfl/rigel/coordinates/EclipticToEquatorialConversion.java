package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * Ecliptic to equatorial conversion
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    public static final double OBLIQUITY_REF = Angle.ofDMS(23, 26, 21.45);
    private static final double OBLIQUITY_A = -0.00181;
    private static final double OBLIQUITY_B = 0.0006;
    private static final double OBLIQUITY_C = 46.815;
    private static final Polynomial OBLIQUITY_EXPRESSION = Polynomial.of( OBLIQUITY_A, OBLIQUITY_B, OBLIQUITY_C, 0);

    private final double sinObliquity;
    private final double cosObliquity;


    /**
     * Constructs a change of coordinate system between the ecliptic coordinates
     * and the equatorial coordinates for the couple (date/time) when
     * @param when
     *          date/time to convert
     */
    public EclipticToEquatorialConversion(ZonedDateTime when) {
        double eclipticObliquity = OBLIQUITY_At(Epoch.J2000.julianCenturiesUntil(when));
        this.sinObliquity = Math.sin(eclipticObliquity);
        this.cosObliquity = Math.cos(eclipticObliquity);

    }

    private static double OBLIQUITY_At(double julianCenturiesFromJ2000) {
        double DE = OBLIQUITY_EXPRESSION.at(julianCenturiesFromJ2000);
        double obl = Angle.ofArcsec(DE);
        return OBLIQUITY_REF - obl;
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        final double sinLambda = Math.sin(ecl.lon());
        final double cosLambda = Math.cos(ecl.lon());
        final double sinBeta = Math.sin(ecl.lat());
        final double cosBeta = Math.cos(ecl.lat());

        final double ra = Math.atan2((cosObliquity * sinLambda * cosBeta - sinObliquity * sinBeta), cosBeta * cosLambda);
        final double dec = Math.asin(cosObliquity * sinBeta + sinObliquity * cosBeta * sinLambda);
        return EquatorialCoordinates.of(Angle.normalizePositive(ra),dec);
    }

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
