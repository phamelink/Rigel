package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class EquatorialCoordinates extends SphericalCoordinates {
    public static final RightOpenInterval RIGHT_ASCENSION_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    public static final ClosedInterval DECLINATION_INTERVAL = ClosedInterval.symmetric(Math.PI);

    private EquatorialCoordinates(double raHr, double decDeg) {

        super(Angle.ofHr(raHr), Angle.ofDeg(decDeg));

    }

    /**
     * Construction method
     * @param ra (double): right ascension (angle in radians)
     * @param dec (double): declination (angle in radians)
     * @return (EquatorialCoordinates)
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        if (!isValidRa(ra) || !isValidDec(dec)) throw new IllegalArgumentException();

        return new EquatorialCoordinates(Angle.toHr(ra), Angle.toDeg(dec));
    }

    /**
     * Construction method
     * @param raDeg (double): right ascension (angle in degrees [0°, 360°[)
     * @param decDeg (double): declination (angle in degrees [-90°,90°])
     * @return (EquatorialCoordinates)
     */
    public static EquatorialCoordinates ofDeg(double raDeg, double decDeg) {
        return of(Angle.ofDeg(raDeg), Angle.ofDeg(decDeg));
    }

    /**
     * checks if the given right ascension is valid
     * @param ra (double): ra in hours
     * @return (boolean): the right ascension is valid or not
     */
    public static boolean isValidRa(double ra) {
        return RIGHT_ASCENSION_INTERVAL.contains(ra);
    }

    /**
     * checks if the given declination is valid
     * @param dec (double): dec in degrees
     * @return (boolean): the declination is valid or not
     */
    public static boolean isValidDec(double dec) {
        return DECLINATION_INTERVAL.contains(dec);
    }

    /**
     * returns the right ascension (in radians)
     * @return (double) ra
     */
    public double ra() { return super.lon(); }

    /**
     * returns the right ascension (in degrees)
     * @return (double) ra
     */
    public double raDeg() { return super.lonDeg(); }

    /**
     * returns the right ascension (in hours)
     * @return (double) ra
     */
    public double raHr() { return Angle.toHr(super.lon()); }

    /**
     * returns the declination (in radians)
     * @return (double) dec
     */
    public double dec() { return super.lat(); }

    /**
     * returns the declination (in degrees)
     * @return (double) dec
     */
    public double decDeg() { return super.latDeg(); }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }

}
