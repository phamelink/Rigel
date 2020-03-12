package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Ecliptic coordinates
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class EclipticCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    private static final ClosedInterval LATITUDE_INTERVAL = ClosedInterval.symmetric(Math.PI);

    private EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Construction method
     * @param lon (double):
     *            longitude (angle in radians)
     * @param lat (double):
     *            latitude (angle in radians)
     * @return (EclipticCoordinates)
     */
    public static EclipticCoordinates of(double lon, double lat){
        if(!isValidLat(lat)|| !isValidLon(lon)) throw new IllegalArgumentException();

        return new EclipticCoordinates(lon, lat);
    }

    /**
     * Construction method
     * @param lonDeg (double):
     *               longitude (angle in degrees [-180,180[)
     * @param latDeg (double):
     *               latitude (angle in degrees [-90,90])
     * @return (EclipticCoordinates)
     */
    public static EclipticCoordinates ofDeg(double lonDeg, double latDeg){
        return of(Angle.ofDeg(lonDeg), Angle.ofDeg(latDeg));
    }

    /**
     * checks if the given longitude is valid
     * @param lon (double):
     *            lon in radians
     * @return (boolean): the longitude is valid or not
     */
    private static boolean isValidLon(double lon){
        return LONGITUDE_INTERVAL.contains(lon);
    }

    /**
     * checks if the given latitude is valid
     * @param lat (double):
     *            lat in radians
     * @return (boolean): the latitude is valid or not
     */
    private static boolean isValidLat(double lat){
        return LATITUDE_INTERVAL.contains(lat);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(λ=%.4f°, β=%.4f°)", lonDeg(), latDeg());
    }

    /**
     * returns longitude (in radians)
     * @return (double) longitude
     */
    public double lon() {
        return super.lon();
    }

    /**
     * returns longitude (in degrees)
     * @return (double) longitude
     */
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * returns latitude (in radians)
     * @return (double) latitude
     */
    public double lat() {
        return super.lat();
    }

    /**
     * returns latitude (in degrees)
     * @return (double) latitude
     */
    public double latDeg() {
        return super.latDeg();
    }
}
