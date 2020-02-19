package ch.epfl.rigel.math;

import java.util.Locale;

public final class RightOpenInterval extends Interval {
    private RightOpenInterval(double inf, double sup) {
        super(inf, sup);
    }
    public static RightOpenInterval of(double low, double high){
        if(low >= high) throw new IllegalArgumentException();
        return new RightOpenInterval(low, high);
    }

    public static RightOpenInterval symmetric(double radius){
        if(radius <= 0) throw new IllegalArgumentException();
        return new RightOpenInterval(-radius,radius);
    }


    @Override
    boolean contains(double v) {
        return v >= this.low() && v < this.high();
    }

    public double reduce(double v){

        return this.low() + floorMod(v-this.low(), this.high()-this.low());
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "[%s,%s[", this.low(), this.high());
    }

    static double floorMod(double x, double y){
        return x-y*Math.floor(x/y);
    }
}
