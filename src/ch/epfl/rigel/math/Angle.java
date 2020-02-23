package ch.epfl.rigel.math;

public final class Angle {

    public static final double TAU = 2.0 * Math.PI;

    private static final double RAD_PER_SEC = 1.0 / 3600.0;
    private static final double RAD_PER_MIN = 1.0 / 60.0;
    private static final double HR_PER_RAD = 24.0 / TAU;
    private static final double RAD_PER_HR = TAU / 24.0;

    public static final RightOpenInterval STANDARD_INTERVAL = RightOpenInterval.of(0, TAU);
    private static final RightOpenInterval HOUR_INTERVAL = RightOpenInterval.of(0, 60);

    //prevent instantiation
    private Angle() {
    }

    public static double normalizePositive(double rad){
        return STANDARD_INTERVAL.reduce(rad);
    }

    public static double ofArcsec(double sec){ return sec * RAD_PER_SEC; }

    public static double ofDMS(int deg, int min, double sec){

        if(!HOUR_INTERVAL.contains(min) || !HOUR_INTERVAL.contains(sec)) throw new IllegalArgumentException();
        return Math.toRadians(deg + min * RAD_PER_MIN + sec * RAD_PER_SEC);

    }

    public static double ofDeg(double deg){ return Math.toRadians(deg); }

    public static double toDeg(double rad){
        return Math.toDegrees(rad);
    }

    public static double ofHr(double hr){ return hr * RAD_PER_HR; }

    public static double toHR(double rad){ return rad * HR_PER_RAD; }

}
