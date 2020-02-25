package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;

public final class GeographicCoordinates extends SphericalCoordinates {

    private static final RightOpenInterval LONGITUDE_INTERVAL = RightOpenInterval.of(-180, 180);
    private static final ClosedInterval LATITUDE_INTERVAL = ClosedInterval.of(-90, 90);

    private GeographicCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public static GeographicCoordinates ofDeg(double lonDeg, double latDeg){
        if(!isValidLatDeg(latDeg)|| !isValidLonDeg(lonDeg)) throw new IllegalArgumentException();

        return new GeographicCoordinates(lonDeg, latDeg);
    }

    public static boolean isValidLonDeg(double lonDeg){
        return LONGITUDE_INTERVAL.contains(lonDeg);
    }

    public static boolean isValidLatDeg(double latDeg){
        return LATITUDE_INTERVAL.contains(latDeg);
    }

    public double lon() {
        return super.lon();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(lon=%.4f°, lat=%.4f°)", lonDeg(), latDeg());
    }

    public double lonDeg() {
        return super.lonDeg();
    }

    public double lat() {
        return super.lat();
    }

    public double latDeg() {
        return super.latDeg();
    }
}
