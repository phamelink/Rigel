package ch.epfl.rigel.math;

import java.util.List;
import java.util.Locale;

final public class Polynomial {
    private Polynomial(double coefficientN, double... coefficients) {
        this.coeffs = new double[coefficients.length + 1];
        coeffs[0] = coefficientN;
        System.arraycopy(coefficients, 0, coeffs, 1, coefficients.length);
    }

    private static double[] coeffs;

    public static Polynomial of(double coefficientN, double... coefficients) {
        if (coefficientN == 0) { throw new IllegalArgumentException(); }

        return new Polynomial (coefficientN, coefficients);
    }

    public static double at(double x) {
        double f=coeffs[0];
        if (coeffs.length == 1) { return f*x; }
        for (int i=1; i<coeffs.length; ++i) {
            f = f*x + coeffs[i];
        }
        return f;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        int exp = coeffs.length;

        for (int i = 0; i <= coeffs.length ; ++i) {
            if (coeffs[i] != 0 ) {
                if (i==0) {
                    str.append(coeffs[i]);
                } else if (i != 1) {
                    str.append(coeffs[i] + "x^" + i);
                } else {
                    str.append(coeffs[i] + "x");
                }
            }

            if(i != 0 && coeffs[i-1] > 0) str.append("+");
        }
        return str.toString();
    }

    @Override
    public int hashCode() { throw new UnsupportedOperationException(); }


    @Override
    public boolean equals(Object obj) { throw new UnsupportedOperationException(); }

}
