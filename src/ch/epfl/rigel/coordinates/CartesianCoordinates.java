package ch.epfl.rigel.coordinates;

import java.util.Locale;

/**
 * Cartesian coordinates class
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class CartesianCoordinates {
    private final double x;
    private final double y;

    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @param x (double)
     *          absciss coordinate
     * @param y (double)
     *          ordinate coordinate
     * @return (CartesianCoordinates) cartesian coordinates of absciss x and ordinate y
     */
    public static CartesianCoordinates of(double x, double y){
        return new CartesianCoordinates(x,y);
    }

    /**
     * returns absciss coordinate x
     * @return (double) absciss coordinate x
     */
    public double x(){
        return this.x;
    }

    /**
     * returns ordinate coordinate y
     * @return (double) ordinate coordinate y
     */
    public double y(){
        return this.y;
    }

    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT, "(x=%.4f, y=%.4f)", x, y);
    }
}
