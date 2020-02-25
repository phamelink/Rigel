package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;

import java.util.Locale;
import java.util.NoSuchElementException;

public final class HorizontalCoordinates extends SphericalCoordinates {

    public static final RightOpenInterval AZIMUTH_INTERVAL = RightOpenInterval.of(0, 360);
    public static final ClosedInterval ALTITUDE_INTERVAL = ClosedInterval.of(-90, 90);

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

        OCTANT(int key, int beginDeg, int endDeg, String name, CARDINAL[] placeholder){
            final double size = 45.0/2.0;
            octantInterval = RightOpenInterval.of(beginDeg-size, endDeg-size);
            this.key = key;
            this.name = name;
            this.placeholder = placeholder;
        }

        public static OCTANT octantOfDeg(double deg){
            double mod = deg % 360.0;
            for(OCTANT oc : OCTANT.values()){
                if (oc.octantInterval.contains(mod)) return oc;
            }
            throw new NoSuchElementException();
        }

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

    public static HorizontalCoordinates of(double az, double alt){
        double azDeg = Angle.toDeg(az);
        double altDeg = Angle.toDeg(alt);

        return ofDeg(azDeg, altDeg);

    }

    public static HorizontalCoordinates ofDeg(double azDeg, double altDeg) {
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

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(az=%.4f°, alt=%.4f°)", azDeg(), altDeg());
    }


}
