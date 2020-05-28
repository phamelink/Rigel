package ch.epfl.rigel;

import ch.epfl.rigel.math.Interval;

/**
 * Preconditions
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class Preconditions {

    private Preconditions(){}

    /**
     * Throws IllegalArgumentException if the argument is false
     * @param isTrue (boolean):
     *               argument to be checked
     * @throws IllegalArgumentException if argument is false
     */
    public static void checkArgument(boolean isTrue){
        if(!isTrue) throw new IllegalArgumentException("Wrong argument");
    }

    /**
     * Throws exception if "value" does not belong to the interval
     * @param interval
     *                 interval in which we verify the value
     * @param value
     *              value to be verified
     * @return (double) value: returns the value or an IllegalArgumentException
     */
    public static double checkInInterval(Interval interval, double value){
        if(!interval.contains(value)){
            throw new IllegalArgumentException("Value is not in interval");
        }else{
            return value;
        }
    }

    /**
     * Throws exception if "value" is not positive
     *
     * @param value
     *              value to be verified
     * @return (double) value: returns the value or an IllegalArgumentException
     */
    public static double checkPositive(double value){
        if(value < 0.0 ){
            throw new IllegalArgumentException("Value is negative");
        }else{
            return value;
        }
    }

}
