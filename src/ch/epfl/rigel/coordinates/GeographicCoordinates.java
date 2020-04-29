package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

/**
 * Geographic coordinates
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class GeographicCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.of(-180, 180);
    private static final ClosedInterval LATITUDE_INTERVAL = ClosedInterval.of(-90, 90);

    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Construction method
     * @param lonDeg
     *               longitude (angle in degrees [-180°, 180°[)
     * @param latDeg
     *               latitude (angle in degrees [-90°,90°])
     * @return GeographicCoordinates of longitude lonDeg and latitude latDeg
     */
    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg){
        Preconditions.checkArgument(isValidLatDeg(latDeg)|| isValidLonDeg(lonDeg));

        return new GeographicCoordinates(Angle.ofDeg(lonDeg),Angle.ofDeg(latDeg));
    }

    /**
     * checks if the given longitude is valid
     * @param lonDeg
     *               lon in degrees
     * @return true if the longitude is valid
     */
    public static boolean isValidLonDeg(double lonDeg){
        return LONGITUDE_INTERVAL.contains(lonDeg);
    }

    /**
     * checks if the given latitude is valid
     * @param latDeg
     *               lat in degrees
     * @return true if the given latitude is valid
     */
    public static boolean isValidLatDeg(double latDeg){
        return LATITUDE_INTERVAL.contains(latDeg);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }

    /**
     * returns longitude (in radians)
     * @return longitude (in radians)
     */
    @Override
    public double lon() {
        return super.lon();
    }

    /**
     * returns longitude (in degrees)
     * @return longitude (in degrees)
     */
    @Override
    public double lonDeg() {
        return super.lonDeg();
    }

    /**
     * returns latitude (in radians)
     * @return latitude (in radians)
     */
    @Override
    public double lat() {
        return super.lat();
    }

    /**
     * returns latitude (in degrees)
     * @return latitude (in degrees)
     */
    @Override
    public double latDeg() {
        return super.latDeg();
    }
}
