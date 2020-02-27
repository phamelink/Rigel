package ch.epfl.rigel.math;

import java.util.List;
import java.util.Locale;

final public class Polynomial {
    private double[] coeffs;

    private Polynomial(double coefficientN, double... coefficients) {
        this.coeffs = new double[coefficients.length + 1];
        coeffs[0] = coefficientN;
        System.arraycopy(coefficients, 0, coeffs, 1, coefficients.length);
    }


    /**
     * returns a polynomial function with the given coefficients in decreasing order
     * @param coefficientN (double): highest coefficient (cannot be 0)
     * @param coefficients (double[]): array of the remaining coefficients (can be empty)
     * @return (Polynomial)
     */
    public static Polynomial of(double coefficientN, double... coefficients) {
        if (coefficientN == 0) throw new IllegalArgumentException();
        return new Polynomial (coefficientN, coefficients);
    }

    /**
     * returns value of the function with given parameter as x
     * @param x (double): value given to calculate function
     * @return (double)
     */
    public double at(double x) {

        final int maxDeg = coeffs.length - 1;
        int k = maxDeg;
        double[] b = new double[k + 1];
        b[k] = coeffs[0];
        while (k > 0) {
            b[k-1] = coeffs[maxDeg - k + 1]+ b[k] * x;
            --k;
        }
        return b[0];
            
    }










    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();

        for (int n = coeffs.length -1, i = 0; n >= 0 ; --n, ++i) {

            if (coeffs[i] != 0) {
                if(coeffs[i] == -1.0){
                    str.append("-");
                } else if (coeffs[i] != 1.0) str.append(coeffs[i]);

                if (n >= 2) {
                    str.append("x^").append(n);
                } else if (n == 1) {
                    str.append("x");
                }


            }
            if (n != 0 && coeffs[i + 1] > 0) str.append("+");
        }

        return str.toString();
    }

    @Override
    public int hashCode() { throw new UnsupportedOperationException(); }


    @Override
    public boolean equals(Object obj) { throw new UnsupportedOperationException(); }

}
