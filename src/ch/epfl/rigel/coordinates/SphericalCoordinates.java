package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

/**
 * Spherical coordinates (sbtract class)
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
abstract class SphericalCoordinates {
    private final double longitude; //in radians
    private final double latitude; //in radians

    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * returns longitude (in radians)
     * @return (double) longitude in radians
     */
    double lon(){ return longitude; }

    /**
     * returns longitude (in degrees)
     * @return (double) longitude in degrees
     */
    double lonDeg(){ return Angle.toDeg(longitude); }

    /**
     * returns latitude (in radians)
     * @return (double) latitude in radians
     */
    double lat(){ return latitude; }

    /**
     * returns latitude (in degrees)
     * @return (double) latitude in degrees
     */
    double latDeg(){ return Angle.toDeg(latitude); }

    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

}
