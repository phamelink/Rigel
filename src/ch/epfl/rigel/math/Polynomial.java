package ch.epfl.rigel.math;

import ch.epfl.rigel.Preconditions;

/**
 * Polynomial
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class Polynomial {
    private final double[] coeffs;

    private Polynomial(double coefficientN, double... coefficients) {
        this.coeffs = new double[coefficients.length + 1];
        coeffs[0] = coefficientN;
        System.arraycopy(coefficients, 0, coeffs, 1, coefficients.length);
    }


    /**
     * returns a polynomial function with the given coefficients in decreasing order
     * @param coefficientN
     *                     highest coefficient (cannot be 0)
     * @param coefficients
     *                     array of the remaining coefficients (can be empty)
     * @return a polynomial function with the given coefficients in decreasing order
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        Preconditions.checkArgument(coefficientN != 0);
        return new Polynomial (coefficientN, coefficients);
    }

    /**
     * returns value of the function with given parameter as x
     * @param x
     *          value given to calculate function
     * @return value of the function with given parameter as x
     */
    public double at(double x) {

        final int maxDeg = coeffs.length - 1;
        int k = maxDeg;
        double b = coeffs[0];
        while (k > 0) {
            b = coeffs[maxDeg - k + 1]+ b * x;
            --k;
        }
        return b;
            
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int i = 0; i <= coeffs.length - 1 ;++i) {

            if (coeffs[i] != 0) {
                if(coeffs[i] == -1.0){
                    str.append("-");
                } else if (coeffs[i] != 1.0) str.append(coeffs[i]);

                if (coeffs.length - 1 - i >= 2) {
                    str.append("x^").append(coeffs.length - 1 - i);
                } else if (coeffs.length - 1 - i == 1) {
                    str.append("x");
                }


            }
            if (coeffs.length - 1 - i != 0 && coeffs[i + 1] > 0) str.append("+");
        }

        return str.toString();
    }

    @Override
    public int hashCode() { throw new UnsupportedOperationException(); }


    @Override
    public boolean equals(Object obj) { throw new UnsupportedOperationException(); }

}
