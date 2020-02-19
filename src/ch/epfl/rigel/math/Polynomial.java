package ch.epfl.rigel.math;

import java.util.List;

final public class Polynomial {
    private Polynomial(double coefficientN, double... coefficients) {
        this.coeffs = new double[coefficients.length + 1];
        this.coeffs[0] = coefficientN;
        int pos=1;
        for (double coeff : coefficients) {
            coeffs[pos] = coeff;
            pos++;
        }
    }

    private double[] coeffs;

    public static Polynomial of(double coefficientN, double... coefficients) {
        if (coefficientN == 0) { throw new IllegalArgumentException(); }

        return new Polynomial (coefficientN, coefficients);
    }
}
