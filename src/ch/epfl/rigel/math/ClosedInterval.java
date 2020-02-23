package ch.epfl.rigel.math;

import java.util.Locale;

public class ClosedInterval extends Interval {
    private ClosedInterval(double inf, double sup) {
        super(inf, sup);
    }

    //Public construction methods

    public static ClosedInterval of(double low, double high){
        if(low >= high) throw new IllegalArgumentException();
        return new ClosedInterval(low, high);
    }

    public static ClosedInterval symmetric(double radius){
        if(radius <= 0) throw new IllegalArgumentException();
        return new ClosedInterval(-radius,radius);
    }


    @Override
    public boolean contains(double v) {
        return v >= this.low() && v <= this.high();
    }

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
