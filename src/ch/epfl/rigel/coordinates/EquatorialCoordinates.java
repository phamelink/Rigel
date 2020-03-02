package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class EquatorialCoordinates extends SphericalCoordinates {
    public static final RightOpenInterval RIGHT_ASCENSION_INTERVAL = RightOpenInterval.of(0, 24);
    public static final ClosedInterval DECLINATION_INTERVAL = ClosedInterval.symmetric(180);

    private EquatorialCoordinates(double raHr, double decDeg) {

        super(Angle.toDeg(Angle.ofHr(raHr)), decDeg);

    }

    /**
     * Construction method
     * @param ra (double): right ascension (angle in hours [0h,24h[)
     * @param dec (double): declination (angle in degrees [-90°,90°])
     * @return (EquatorialCoordinates)
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        if (!isValidRa(ra) || !isValidDec(dec)) throw new IllegalArgumentException();

        return new EquatorialCoordinates(Angle.toHr(ra), Angle.toDeg(dec));
    }

    /**
     * Construction method
     * @param raDeg (double): right ascension (angle in degrees [0°, 360°[)
     * @param dec (double): declination (angle in degrees [-90°,90°])
     * @return (EquatorialCoordinates)
     */
    public static EquatorialCoordinates ofDeg(double raDeg, double dec) {
        if (!isValidRa(raDeg) || !isValidDec(dec)) throw new IllegalArgumentException();

        return new EquatorialCoordinates(raDeg, dec);
    }

    /**
     * checks if the given right ascension is valid
     * @param ra (double): ra in degrees
     * @return (boolean): the right ascension is valid or not
     */
    public static boolean isValidRa(double ra) {
        return RIGHT_ASCENSION_INTERVAL.contains(Angle.toHr(ra));
    }

    /**
     * checks if the given declination is valid
     * @param dec (double): dec in degrees
     * @return (boolean): the declination is valid or not
     */
    public static boolean isValidDec(double dec) {
        return DECLINATION_INTERVAL.contains(Angle.toDeg(dec));
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
