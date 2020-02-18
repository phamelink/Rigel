package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

public final class Preconditions {

    public static void checkArgument(boolean isTrue){
        if(isTrue) throw new IllegalArgumentException();
    }

    public static double checkInterval(Interval interval, double value){
        if(value < interval.low() || value > interval.high()){
            throw new IllegalArgumentException();
        }else{
            return value;
        }
    }

}
