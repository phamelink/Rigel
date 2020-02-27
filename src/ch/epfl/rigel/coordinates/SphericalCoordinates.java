package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

abstract class SphericalCoordinates {
    private final double longitude;
    private final double latitude;

    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * returns longitude (in radians)
     * @return (double) longitude
     */
    double lon(){ return Angle.ofDeg(longitude); }

    /**
     * returns longitude (in degrees)
     * @return (double) longitude
     */
    double lonDeg(){ return longitude; }

    /**
     * returns latitude (in radians)
     * @return (double) latitude
     */
    double lat(){
        return Angle.ofDeg(latitude);
    }

    /**
     * returns latitude (in degrees)
     * @return (double) latitude
     */
    double latDeg(){ return latitude; }

    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

}
