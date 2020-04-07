package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;


/**
 * Equatorial coordinates
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class EquatorialCoordinates extends SphericalCoordinates {
    public static final RightOpenInterval RIGHT_ASCENSION_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    public static final ClosedInterval DECLINATION_INTERVAL = ClosedInterval.symmetric(Math.PI);

    private EquatorialCoordinates(double ra, double dec) {
        super(ra, dec);
    }

    /**
     * Construction method
     * @param ra
     *           right ascension (angle in radians)
     * @param dec
     *            declination (angle in radians)
     * @return EquatorialCoordinates with right ascension ra and declination dec
     */
    public static EquatorialCoordinates of(double ra, double dec) {
        if (!isValidRa(ra) || !isValidDec(dec)) throw new IllegalArgumentException(ra + "|" + dec);

        return new EquatorialCoordinates(ra, dec);
    }

    /**
     * Construction method
     * @param raDeg
     *              right ascension (angle in degrees [0°, 360°[)
     * @param decDeg
     *               declination (angle in degrees [-90°,90°])
     * @return EquatorialCoordinates with right ascension raDeg and declination decDeg
     */
    public static EquatorialCoordinates ofDeg(double raDeg, double decDeg) {
        return of(Angle.ofDeg(raDeg), Angle.ofDeg(decDeg));
    }

    /**
     * checks if the given right ascension is valid
     * @param ra
     *           ra in hours
     * @return true if the right ascension is valid
     */
    public static boolean isValidRa(double ra) {
        return RIGHT_ASCENSION_INTERVAL.contains(ra);
    }

    /**
     * checks if the given declination is valid
     * @param dec
     *            dec in degrees
     * @return true if the declination is valid
     */
    public static boolean isValidDec(double dec) {
        return DECLINATION_INTERVAL.contains(dec);
    }

    /**
     * returns the right ascension (in radians)
     * @return the right ascension (in radians)
     */
    public double ra() { return super.lon(); }

    /**
     * returns the right ascension (in degrees)
     * @return the right ascension (in degrees)
     */
    public double raDeg() { return super.lonDeg(); }

    /**
     * returns the right ascension (in hours)
     * @return the right ascension (in hours)
     */
    public double raHr() { return Angle.toHr(super.lon()); }

    /**
     * returns the declination (in radians)
     * @return the declination (in radians)
     */
    public double dec() { return super.lat(); }

    /**
     * returns the declination (in degrees)
     * @return the declination (in degrees)
     */
    public double decDeg() { return super.latDeg(); }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(ra=%.4fh, dec=%.4f°)", raHr(), decDeg());
    }

}
