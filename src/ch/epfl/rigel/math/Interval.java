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



}
