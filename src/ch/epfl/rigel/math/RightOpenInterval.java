package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

import java.util.Locale;

/**
 * Right open interval
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class RightOpenInterval extends Interval {
    private RightOpenInterval(double inf, double sup) {
        super(inf, sup);
    }

    /**
     * public method to instanciate a right open interval
     * @param low
     *            lower bound
     * @param high
     *             upper bound
     * @throws IllegalArgumentException if low is bigger or equal to high
     * @return right open interval
     */
    public static RightOpenInterval of(double low, double high){
        Preconditions.checkArgument(low < high);
        return new RightOpenInterval(low, high);
    }

    /**
     * public method to instanciate a right open interval centered at 0
     * @param diameter
     *                 lower and upper bound
     * @throws IllegalArgumentException if diameter is smaller or equal to than 0
     * @return right open interval centered at 0
     */
    public static RightOpenInterval symmetric(double diameter){
        Preconditions.checkArgument(diameter > 0);
        return new RightOpenInterval(-diameter/2.0,diameter/2.0);
    }


    @Override
    public boolean contains(double v) {
        return v >= this.low() && v < this.high();
    }

    /**
     * reduces its argument to the interval
     * @param v
     *          argument to be reduced
     * @return the argument reduced to the interval
     */
    public double reduce(double v){

        return this.low() + floorMod(v-this.low(), size());
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s[", this.low(), this.high());
    }


    private static double floorMod(double x, double y){
        return x-y*Math.floor(x/y);
    }
}
