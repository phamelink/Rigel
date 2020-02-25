package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

abstract class SphericalCoordinates {
    private final double longitude;
    private final double latitude;

    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    double lon(){ return Angle.ofDeg(longitude); }

    double lonDeg(){ return longitude; }

    double lat(){
        return Angle.ofDeg(latitude);
    }

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
