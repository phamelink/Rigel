package ch.epfl.rigel.math;

abstract public class Interval {


    public Interval(double inf, double sup) {
        this.inf = inf;
        this.sup = sup;
    }

    private final double inf;
    private final double sup;

    public double high(){
        return sup;
    }

    public double low(){
        return inf;
    }

    public double size(){
        return Math.abs(inf-sup);
    }

    abstract boolean contains(double v);

    //redefinitions to raise exceptions

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }




}
