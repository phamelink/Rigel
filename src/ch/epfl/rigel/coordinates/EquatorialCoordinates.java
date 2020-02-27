package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class EquatorialCoordinates extends SphericalCoordinates {
    public static final RightOpenInterval RIGHT_ASCENSION_INTERVAL = RightOpenInterval.of(0, 360);
    public static final ClosedInterval DECLINATION_INTERVAL = ClosedInterval.symmetric(180);

    private EquatorialCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Construction method
     * @param ra (double): right ascension (angle in hours [0h,24h[)
     * @param dec (double): declination (angle in degrees [-90°,90°])
     * @return (EquatorialCoordinates)
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        double raDeg = Angle.ofHr(ra);
        if (!isValidRa(raDeg) || !isValidDec(dec)) throw new IllegalArgumentException();

        return new EquatorialCoordinates(raDeg, dec);
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
     * @return
     */
    public static boolean isValidRa(double ra) {
        return RIGHT_ASCENSION_INTERVAL.contains(ra);
    }

    /**
     * checks if the given declination is valid
     * @param dec (double): dec in degrees
     * @return
     */
    public static boolean isValidDec(double dec) {
        return DECLINATION_INTERVAL.contains(dec);
    }

    public double ra() { return super.lon(); }

    public double raDeg() { return super.lonDeg(); }

    public double raHr() { return Angle.toHr(super.lon()); }

    public double dec() { return super.lon(); }

    public double decDeg() { return super.lonDeg(); }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }

}