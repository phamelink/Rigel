package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

public final class HorizontalCoordinates extends SphericalCoordinates {

    public static final RightOpenInterval AZIMUTH_INTERVAL = RightOpenInterval.of(0, 360);
    public static final ClosedInterval ALTITUDE_INTERVAL = ClosedInterval.of(-90, 90);

    public enum OCTANTS {
        N(),
        NE(),
        E(),
        SE(),
        S(),
        SO(),
        O(),
        NO();

        private RightOpenInterval octantInterval;

        OCTANTS(){

        }
    }


    private HorizontalCoordinates(double longitude, double latitude) {
        super(longitude, latitude);
    }

    public static HorizontalCoordinates of(double az, double alt){
        double azDeg = Angle.toDeg(az);
        double altDeg = Angle.toDeg(alt);

        return ofDeg(azDeg, altDeg);

    }

    private static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
        if(!isValidAz(azDeg) || !isValidAlt(altDeg)) throw new IllegalArgumentException();

        return new HorizontalCoordinates(azDeg, altDeg);
    }

    public static boolean isValidAz(double azDeg){
        return AZIMUTH_INTERVAL.contains(azDeg);
    }

    public static boolean isValidAlt(double altDeg){
        return ALTITUDE_INTERVAL.contains(altDeg);
    }

    public double az(){
        return Angle.ofDeg(super.lon());
    }

    public double azDeg(){
        return super.lon();
    }

    public double azOctantName(String n, String e, String s, String w){



    }
}
