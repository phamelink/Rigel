package ch.epfl.rigel.math;

public final class Angle {

    public static final double TAU = 6.2831853071;
    public static final RightOpenInterval STANDARD_INTERVAL = RightOpenInterval.of(0, TAU);
    private static final RightOpenInterval HOUR_INTERVAL = RightOpenInterval.of(0, 60);

    //prevent instantiation
    private Angle() {
    }

    public static double normalizePositive(double rad){
        return STANDARD_INTERVAL.reduce(rad);
    }

    public static double ofArcsec(double sec){

        return 0;
    }

    public static double ofDMS(int deg, int min, double sec){

        if(!HOUR_INTERVAL.contains(min) || !HOUR_INTERVAL.contains(sec)) throw new IllegalArgumentException();

        return 0;
    }
//hey
    public static double ofDeg(double deg){
        return (deg/360)*TAU;
    }

    public static double toDeg(double rad){
        return (rad/TAU)*360;
    }

    public static double ofHr(double hr){

        return 0;
    }

    public static double toHR(double rad){

        return 0;
    }

}
