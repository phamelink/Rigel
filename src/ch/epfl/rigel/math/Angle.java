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
        return sec/3600; }

    public static double ofDMS(int deg, int min, double sec){

        if(!HOUR_INTERVAL.contains(min) || !HOUR_INTERVAL.contains(sec)) throw new IllegalArgumentException();

        return ((deg + min/60.0 + sec/3600)*TAU)/360;
    }

    public static double ofDeg(double deg){
        return (deg/360)*TAU;
    }

    public static double toDeg(double rad){
        return (rad/TAU)*360;
    }

    public static double ofHr(double hr){

        return (TAU*hr)/24;
    }

    public static double toHR(double rad){

        return (rad*24)/TAU;
    }

}
