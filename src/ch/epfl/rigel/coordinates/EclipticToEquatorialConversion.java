package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.function.Function;

public class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    public static final double OBLIQUITY_REF = Angle.ofDMS(23, 26, 21.45);
    private final double eclipticObliquity;

    private final double sinObliquity;
    private final double cosObliquity;


    public EclipticToEquatorialConversion(ZonedDateTime when) {
        this.eclipticObliquity = obliquityAt(Epoch.J2000.julianCenturiesUntil(when));
        this.sinObliquity = Math.sin(eclipticObliquity);
        this.cosObliquity = Math.cos(eclipticObliquity);

    }

    private static final double obliquityA = Angle.ofArcsec(0.00181);
    private static final double obliquityB = Angle.ofArcsec(-0.0006);
    private static final double obliquityC = Angle.ofArcsec(-46.815);
    private static final Polynomial obliquityExpression = Polynomial.of( obliquityA, obliquityB, obliquityC, OBLIQUITY_REF);

    private static double obliquityAt(double julianCenturiesFromJ200) {
        return obliquityExpression.at(julianCenturiesFromJ200);
    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        double sinLambda = Math.sin(ecl.lon());
        double cosLambda = Math.cos(ecl.lon());
        double sinBeta = Math.sin(ecl.lat());
        double cosBeta = Math.cos(ecl.lat());

        double ra = Math.atan2((cosObliquity * sinLambda * cosBeta - sinObliquity * sinBeta), cosBeta * cosLambda);
        double dec = Math.asin(cosObliquity * sinBeta + sinObliquity * cosBeta * sinLambda);
        EquatorialCoordinates eqc = EquatorialCoordinates.of(ra,dec);
        return eqc;
    }

    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
