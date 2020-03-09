package ch.epfl.rigel;

import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;

public final class Preconditions {

    private Preconditions(){}

    /**
     * Throws IllegalArgumentException if the argument is false
     * @param isTrue (boolean): argument to be checked
     */
    public static void checkArgument(boolean isTrue){
        if(!isTrue) throw new IllegalArgumentException();
    }

    /**
     * Throws exception if "value" does not belong to the interval
     * @param interval (Interval): interval in which we verify the value
     * @param value (double): value to be verified
     * @return (double) value: returns the value or an IllegalArgumentException
     */
    public static double checkInInterval(Interval interval, double value){
        if(!interval.contains(value)){
            throw new IllegalArgumentException();
        }else{
            return value;
        }
    }

}
