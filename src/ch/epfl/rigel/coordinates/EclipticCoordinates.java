package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class EclipticCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.of(-180, 180);
    private static final ClosedInterval LATITUDE_INTERVAL = ClosedInterval.of(-90, 90);

    private EclipticCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    /**
     * Construction method
     * @param lon (double): longitude (angle in radians)
     * @param lat (double): latitude (angle in radians)
     * @return (EclipticCoordinates)
     */
    public static EclipticCoordinates of(double lon, double lat){
        return ofDeg(Angle.toDeg(lon), Angle.toDeg(lat));
    }

    /**
     * Construction method
     * @param lonDeg (double): longitude (angle in degrees [-180,180[)
     * @param latDeg (double): latitude (angle in degrees [-90,90])
     * @return (EclipticCoordinates)
     */
    public static EclipticCoordinates ofDeg(double lonDeg, double latDeg){
        if(!isValidLatDeg(latDeg)|| !isValidLonDeg(lonDeg)) throw new IllegalArgumentException();

        return new EclipticCoordinates(lonDeg, latDeg);
    }

    /**
     * checks if the given longitude is valid
     * @param lonDeg (double): lon in degrees
     * @return (boolean): the longitude is valid or not
     */
    public static boolean isValidLonDeg(double lonDeg){
        return LONGITUDE_INTERVAL.contains(lonDeg);
    }

    /**
     * checks if the given latitude is valid
     * @param latDeg (double): lat in degrees
     * @return (boolean): the latitude is valid or not
     */
    public static boolean isValidLatDeg(double latDeg){
        return LATITUDE_INTERVAL.contains(latDeg);
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
