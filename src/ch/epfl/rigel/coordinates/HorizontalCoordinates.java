package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;
import java.util.NoSuchElementException;

/**
 * Horizontal coordinates
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class HorizontalCoordinates extends SphericalCoordinates {

    public static final RightOpenInterval AZIMUTH_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    public static final ClosedInterval ALTITUDE_INTERVAL = ClosedInterval.symmetric(Math.PI);

    private GeographicCoordinates refPoint;

    public enum OCTANT {
        N(0,0, 45, "N", new CARDINAL[]{CARDINAL.N}),
        NE(1,45, 90, "NE", new CARDINAL[]{CARDINAL.N, CARDINAL.E}),
        E(2,90,135, "E", new CARDINAL[]{CARDINAL.E}),
        SE(3,135, 180, "SE", new CARDINAL[]{CARDINAL.S, CARDINAL.E}),
        S(4,180, 225, "S", new CARDINAL[]{CARDINAL.S}),
        SO(5,225, 270, "SW", new CARDINAL[]{CARDINAL.S, CARDINAL.W}),
        O(6,270, 315, "W", new CARDINAL[]{CARDINAL.W}),
        NO(7,315, 360, "NW", new CARDINAL[]{CARDINAL.N, CARDINAL.W});

        final int key;
        final RightOpenInterval octantInterval;
        final String name;
        final CARDINAL[] placeholder;
        static final double size = 45.0/2.0; //Rotation offset size to normalize in right interval

        OCTANT(int key, int beginDeg, int endDeg, String name, CARDINAL[] placeholder){

            octantInterval = RightOpenInterval.of(beginDeg, endDeg);
            System.out.println(octantInterval);
            this.key = key;
            this.name = name;
            this.placeholder = placeholder;
        }

        /**
         * Return octant corresponding to deg.
         * @param deg (double)
         *            azimuth in degrees
         * @return (OCTANT) corresponding octant
         */
        public static OCTANT octantOfDeg(double deg){
            double mod = (deg+size) % 360.0;
            for(OCTANT oc : OCTANT.values()){
                if (oc.octantInterval.contains(mod)) return oc;
            }
            throw new NoSuchElementException(Double.toString(mod));
        }

        /**
         * Return octant corresponding to rad.
         * @param rad (double)
         *            azimuth angle in rad
         * @return (OCTANT) corresponding octant
         */
        public OCTANT octantOf(double rad){
            return octantOfDeg(Angle.toDeg(rad));
        }
    }

    public enum CARDINAL {
        N,S,E,W;
    }


    private HorizontalCoordinates(double azimuth, double altitude) {
        super(azimuth, altitude);
    }

    /**
     * Return a HorizontalCoordinates object of azimuth az and altitude alt (in rad).
     * @param az (double)
     *           azimuth in rad
     * @param alt (double)
     *            altitude in rad
     * @return (HorizontalCoordinates) new HorizontalCoordinates object
     */
    public static HorizontalCoordinates of(double az, double alt){

        if(!isValidAz(az) || !isValidAlt(alt)) throw new IllegalArgumentException();
        return new HorizontalCoordinates(az, alt);

    }

    /**
     * Return a HorizontalCoordinates object of azimuth az and altitude alt (in deg).
     * @param azDeg (double)
     *              azimuth angle in deg
     * @param altDeg (double)
     *               altitude angle in deg
     * @return (HorizontalCoordinates) New HorizontalCoordinates object
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {

       return of(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));

    }

    /**
     * Returns true if valid azimuth.
     * @param az (double)
     *           azimuth angle in rad
     * @return (boolean) the angle is valid
     */
    public static boolean isValidAz(double az){
        return AZIMUTH_INTERVAL.contains(az);
    }

    /**
     * Returns true if valid altitude.
     * @param alt (double)
     *            altitude in rad
     * @return (boolean) the angle is valid
     */
    public static boolean isValidAlt(double alt){
        return ALTITUDE_INTERVAL.contains(alt);
    }

    public double az(){
        return lon();
    }

    public double azDeg(){
        return lonDeg();
    }

    public double alt(){
        return lat();
    }

    public double altDeg(){
        return latDeg();
    }

    /**
     * Returns a string with the octant name of the azimuth of this object generated using the given strings
     * @param n (String)
     *          NORTH characters
     * @param e (String)
     *          EAST characters
     * @param s (String)
     *          SOUTH characters
     * @param w (String)
     *          WEST characters
     * @return (String) Combination from the given characters corresponding to the octant
     */
    public String azOctantName(String n, String e, String s, String w){
        OCTANT currentOctant = OCTANT.octantOfDeg(this.azDeg());
        StringBuilder str = new StringBuilder();
        for(CARDINAL c : currentOctant.placeholder){
            switch (c) {
                case N:
                    str.append(n);
                    break;
                case W:
                    str.append(w);
                    break;
                case E:
                    str.append(e);
                    break;
                case S:
                    str.append(s);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        return str.toString();

    }

    /**
     * Calculates the angular distance between the coordinates of this object and another object (that)
     * @param that (HorizontalCoordinates)
     *             other object
     * @return (double) angular distance in rad
     */
    public double angularDistanceTo(HorizontalCoordinates that){
        return Math.acos(Math.sin(this.lat()) * Math.sin(that.lat()) + Math.cos(this.lat()) * Math.cos(that.lat()) *
                Math.cos(this.lon() - that.lon()));
    }

    /**
     * sets reference point
     * @param refPoint
     *          reference point to set
     */
    public void setRefPoint(GeographicCoordinates refPoint){
        this.refPoint = refPoint;
    }

    /**
     * returns reference point
     * @return reference point
     */
    public GeographicCoordinates getRefPoint(){
        return this.refPoint;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }


}
