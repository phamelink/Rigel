package ch.epfl.rigel.math;

import java.util.Locale;

public final class RightOpenInterval extends Interval {
    private RightOpenInterval(double inf, double sup) {
        super(inf, sup);
    }

    /**
     * public method to instanciate a right open interval
     * @param low (double): lower bound
     * @param high (double): upper bound
     * @return (RightOpenInterval)
     */
    public static RightOpenInterval of(double low, double high){
        if(low >= high) throw new IllegalArgumentException();
        return new RightOpenInterval(low, high);
    }

    /**
     * public method to instanciate a right open interval centered at 0
     * @param diameter (double): lower and upper bound
     * @return (RightOpenInterval)
     */
    public static RightOpenInterval symmetric(double diameter){
        if(diameter <= 0) throw new IllegalArgumentException();
        return new RightOpenInterval(-diameter/2.0,diameter/2.0);
    }


    @Override
    public boolean contains(double v) {
        return v >= this.low() && v < this.high();
    }

    /**
     * reduces its argument to the interval
     * @param v (double): argument to be reduced
     * @return (double)
     */
    public double reduce(double v){

        return this.low() + floorMod(v-this.low(), this.high()-this.low());
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s[", this.low(), this.high());
    }


    private static double floorMod(double x, double y){
        return x-y*Math.floor(x/y);
    }
}
