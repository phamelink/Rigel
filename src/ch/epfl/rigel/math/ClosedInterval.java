package ch.epfl.rigel.math;

import java.util.Locale;

/**
 * Closed Interval
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class ClosedInterval extends Interval {
    private ClosedInterval(double inf, double sup) {
        super(inf, sup);
    }

    //Public construction methods

    /**
     * public method to instanciate a closed interval
     * @param low (double):
     *            lower bound
     * @param high (double):
     *             upper bound
     * @throws IllegalArgumentException if low is bigger or equal to high
     * @return (ClosedInterval)
     */
    public static ClosedInterval of(double low, double high){
        if(low >= high) throw new IllegalArgumentException();
        return new ClosedInterval(low, high);
    }

    /**
     * public method to instanciate a closed interval centered at 0
     * @param diameter (double):
     *                 lower and upper bound
     * @throws IllegalArgumentException if diameter is smaller or equal to than 0
     * @return (ClosedInterval) closed interval centered at 0
     */
    public static ClosedInterval symmetric(double diameter){
        if(diameter <= 0) throw new IllegalArgumentException();
        return new ClosedInterval(-diameter/2.0,diameter/2.0);
    }


    @Override
    public boolean contains(double v) {
        return v >= this.low() && v <= this.high();
    }

    /**
     * clips the value to the interval
     * @param v (double)
     *          value to clip
     * @throws ArithmeticException if v is not conatined in interval
     * @return (double) value if v is contained in interval
     */
    public double clip(double v){
        if(this.contains(v)){
            return v;
        }else if(v >= this.high()){
            return this.high();
        }else if(v <= this.low()){
            return this.low();
        }

        throw new ArithmeticException();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s]", this.low(), this.high());
    }
}
