package ch.epfl.rigel.math;

abstract public class Interval {

    /**
     * Constructor
     * @param inf (double): the lower bound of the interval
     * @param sup (double): the upper bound of the interval
     */
    public Interval(double inf, double sup) {
        this.inf = inf;
        this.sup = sup;
    }

    private final double inf;
    private final double sup;

    /**
     * returns upper bound
     * @return (double)
     */
    public double high(){
        return sup;
    }

    /**
     * returns lower bound
     * @return (double)
     */
    public double low(){
        return inf;
    }

    /**
     * returns size of the interval
     * @return (double)
     */
    public double size(){
        return Math.abs(inf-sup);
    }

    /**
     * Tells if the value is included in the interval or not
     * @param v (double): value to be checked
     * @return (boolean)
     */
    abstract boolean contains(double v);

    //redefinitions to raise exceptions

    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }




}
