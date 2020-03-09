package ch.epfl.rigel.coordinates;

import java.util.Locale;

public final class CartesianCoordinates {
    private final double x;
    private final double y;

    private CartesianCoordinates(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public static CartesianCoordinates of(double x, double y){
        return new CartesianCoordinates(x,y);
    }

    public double x(){
        return this.x;
    }

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
        return String.format(Locale.ROOT, "(x=%.4fh, y=%.4f°)", x, y);
    }
}
