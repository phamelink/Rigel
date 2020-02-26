package ch.epfl.rigel.math;

public final class Angle {

    public static final double TAU = 2.0 * Math.PI;

    private static final double RAD_PER_SEC = TAU / (360.0 * 3600.0);
    private static final double RAD_PER_MIN = TAU / (120.0 * 180);
    private static final double HR_PER_RAD = 24.0 / TAU;
    private static final double RAD_PER_HR = TAU / 24.0;

    public static final RightOpenInterval STANDARD_INTERVAL = RightOpenInterval.of(0, TAU);
    private static final RightOpenInterval HOUR_INTERVAL = RightOpenInterval.of(0, 60);

    //prevent instantiation
    private Angle() {
    }

    /**
     * normalizes the angle to reduce it to the interval [0,TAU[
     * @param rad (double): the angle in radians
     * @return (double): the normalized angle within the interval
     */
    public static double normalizePositive(double rad){
        return STANDARD_INTERVAL.reduce(rad);
    }

    /**
     * returns the angle according to the number of seconds in the given arc (negative or positive)
     * @param sec (double): seconds in the arc
     * @return (double): the angle in radians
     */
    public static double ofArcsec(double sec){ return sec * RAD_PER_SEC; }

    /**
     * returns the angle according to deg°min''sec' and throws an error if the argument is not between 0 and 60 for min and sec
     * @param deg (double): degrees
     * @param min (double): minutes
     * @param sec (double): seconds
     * @return (double): angle in radians
     */
    public static double ofDMS(int deg, int min, double sec){

        if(!HOUR_INTERVAL.contains(min) || !HOUR_INTERVAL.contains(sec)) throw new IllegalArgumentException();
        return Math.toRadians(deg) + min * RAD_PER_MIN + ofArcsec(sec);

    }

    /**
     * returns angle corresponding to the angle in degrees
     * @param deg (double): given angle in degrees
     * @return (double): angle in radians
     */
    public static double ofDeg(double deg){ return Math.toRadians(deg); }

    /**
     * returns angles in degrees corresponding to the given angle
     * @param rad (double): angle given
     * @return (double): angle in degrees
     */
    public static double toDeg(double rad){
        return Math.toDegrees(rad);
    }

    /**
     * returns the angle corresponding to the angle given in hours
     * @param hr (double): angle given in hours
     * @return (double): angle in rad
     */
    public static double ofHr(double hr){ return hr * RAD_PER_HR; }

    /**
     * returns angle in hours corresponding to the angle given in radians
     * @param rad (double): angle to translate in hours
     * @return (double): angle in hours
     */
    public static double toHr(double rad){ return rad * HR_PER_RAD; }

}
