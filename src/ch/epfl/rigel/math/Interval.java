package ch.epfl.rigel.math;

/**
 * Interval
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public abstract class Interval {

    /**
     * Constructor
     * @param inf
     *            the lower bound of the interval
     * @param sup
     *            the upper bound of the interval
     */
    Interval(double inf, double sup) {
        this.inf = inf;
        this.sup = sup;
    }

    private final double inf;
    private final double sup;

    /**
     * returns upper bound
     * @return upper bound
     */
    public double high(){
        return sup;
    }

    /**
     * returns lower bound
     * @return lower bound
     */
    public double low(){
        return inf;
    }

    /**
     * returns size of the interval
     * @return size of the interval
     */
    public double size(){
        return Math.abs(inf-sup);
    }

    /**
     * Tells if the value is included in the interval or not
     * @param v
     *          value to be checked
     * @return  if the value is included in the interval or not
     */
    public abstract boolean contains(double v);

    //redefinitions to raise exceptions

    @Override
    public final int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }




}
