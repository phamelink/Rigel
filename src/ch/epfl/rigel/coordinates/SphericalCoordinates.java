package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;

abstract class SphericalCoordinates {
    private double longitude;
    private double latitude;

    SphericalCoordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    //TODO
    double lon(){
        return 0;
    }

    double lonDeg(){ return Angle.toDeg(lon()); }

    //TODO
    double lat(){
        return 0;
    }

    double latDeg(){ return Angle.toDeg(lat()); }

    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

}
