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

    private static final RightOpenInterval AZIMUTH_INTERVAL = RightOpenInterval.of(0, Angle.TAU);
    private static final ClosedInterval ALTITUDE_INTERVAL = ClosedInterval.symmetric(Math.PI);

    private HorizontalCoordinates(double azimuth, double altitude) {
        super(azimuth, altitude);
    }

    /**
     * Return a HorizontalCoordinates object of azimuth az and altitude alt (in rad).
     * @param az
     *           azimuth in rad
     * @param alt
     *            altitude in rad
     * @return a HorizontalCoordinates object of azimuth az and altitude alt (in rad).
     */
    public static HorizontalCoordinates of(double az, double alt){
        if(!isValidAz(az) || !isValidAlt(alt)) throw new IllegalArgumentException();
        return new HorizontalCoordinates(az, alt);

    }

    /**
     * Return a HorizontalCoordinates object of azimuth az and altitude alt (in deg).
     * @param azDeg
     *              azimuth angle in deg
     * @param altDeg
     *               altitude angle in deg
     * @return a HorizontalCoordinates object of azimuth az and altitude alt (in deg).
     */
    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {

       return of(Angle.ofDeg(azDeg), Angle.ofDeg(altDeg));

    }

    /**
     * Returns true if valid azimuth.
     * @param az
     *           azimuth angle in rad
     * @return true if valid azimuth.
     */
    public static boolean isValidAz(double az){
        return AZIMUTH_INTERVAL.contains(az);
    }

    /**
     * Returns true if valid altitude.
     * @param alt
     *            altitude in rad
     * @return true if valid altitude
     */
    public static boolean isValidAlt(double alt){
        return ALTITUDE_INTERVAL.contains(alt);
    }

    /**
     * returns azimuth (in radians)
     * @return azimuth (in radians)
     */
    public double az(){
        return lon();
    }

    /**
     * returns azimuth (in degrees)
     * @return azimuth (in degrees)
     */
    public double azDeg(){
        return lonDeg();
    }

    /**
     * returns altitude (in radians)
     * @return altitude (in radians)
     */
    public double alt(){
        return lat();
    }

    /**
     * returns altitude (in degrees)
     * @return altitude (in degrees)
     */
    public double altDeg(){
        return latDeg();
    }

    /**
     * Returns a string with the octant name of the azimuth of this object generated using the given strings
     * @param n
     *          NORTH characters
     * @param e
     *          EAST characters
     * @param s
     *          SOUTH characters
     * @param w
     *          WEST characters
     * @return Combination from the given characters corresponding to the octant
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

    public enum OCTANT {
        N(0, "N", new CARDINAL[]{CARDINAL.N}),
        NE(45, "NE", new CARDINAL[]{CARDINAL.N, CARDINAL.E}),
        E(90, "E", new CARDINAL[]{CARDINAL.E}),
        SE(135,  "SE", new CARDINAL[]{CARDINAL.S, CARDINAL.E}),
        S(180,  "S", new CARDINAL[]{CARDINAL.S}),
        SO(225,  "SW", new CARDINAL[]{CARDINAL.S, CARDINAL.W}),
        O(270, "W", new CARDINAL[]{CARDINAL.W}),
        NO(315, "NW", new CARDINAL[]{CARDINAL.N, CARDINAL.W});

        final int orientation;
        final String name;
        final CARDINAL[] placeholder;
        static final double size = 45.0/2.0; //Rotation offset size to normalize in right interval

        OCTANT(int orientation, String name, CARDINAL[] placeholder){
            this.orientation = orientation;
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
                if (RightOpenInterval.of(0.0, 45.0).contains(mod - oc.orientation)) return oc;
            }
            throw new NoSuchElementException(Double.toString(mod));
        }

        public double getOctantAngle(){
            return this.orientation;
        }

    }

    public enum CARDINAL {N,S,E,W}

    /**
     * Calculates the angular distance between the coordinates of this object and another object (that)
     * @param that
     *             other object
     * @return the angular distance between the coordinates of this object and another object (that)
     */
    public double angularDistanceTo(HorizontalCoordinates that){
        return Math.acos(Math.sin(this.lat()) * Math.sin(that.lat()) + Math.cos(this.lat()) * Math.cos(that.lat()) *
                Math.cos(this.lon() - that.lon()));
    }

    /**
     * returns horizontal coordinates generated from this instance by adding given parameters
     * @param deltaAz azimuth change
     * @param deltaAlt altitude change
     * @return changed horizontal coordinates
     */
    public HorizontalCoordinates delta(double deltaAz, double deltaAlt){
        return HorizontalCoordinates.of(Angle.normalizePositive(this.az() + deltaAz),  ALTITUDE_INTERVAL.clip(this.alt() + deltaAlt));
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f??, alt=%.4f??)", azDeg(), altDeg());
    }


}
