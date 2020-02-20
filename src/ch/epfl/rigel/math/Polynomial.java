package ch.epfl.rigel.math;

import java.util.List;
import java.util.Locale;

final public class Polynomial {
    private Polynomial(double coefficientN, double... coefficients) {
        this.coeffs = new double[coefficients.length + 1];
        System.arraycopy(coefficientN, 0, coeffs, 0, 1);
        System.arraycopy(coefficients, 0, coeffs, 1, coeffs.length);
    }

    private static double[] coeffs;

    public static Polynomial of(double coefficientN, double... coefficients) {
        if (coefficientN == 0) { throw new IllegalArgumentException(); }

        return new Polynomial (coefficientN, coefficients);
    }

    public static double at(double x) {
        double f=coeffs[0];
        if (coeffs.length == 1) { return f; }
        for (int i=1; i<coeffs.length; ++i) {
            f = f*x + coeffs[i];
        }
        return f;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (double val : coeffs) {
            if (val != 0) {

            }
        }
    }

}
