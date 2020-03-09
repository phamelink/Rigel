package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

public abstract class CelestialObject {
    private final String name;
    private final EquatorialCoordinates equatorialPos;
    private final float angularSize;
    private final float magnitude;

    CelestialObject(String name, EquatorialCoordinates equatorialPos, float angularSize, float magnitude) {
        if (angularSize < 0) throw new IllegalArgumentException("Angular size is negative");
        Objects.requireNonNull(equatorialPos,"Equatorial position is null");
        Objects.requireNonNull(name, "Name is null");
        this.name = name;
        this.equatorialPos = equatorialPos;
        this.angularSize = angularSize;
        this.magnitude = magnitude;

    }

    public String name() {
        return name;
    }

    public EquatorialCoordinates equatorialPos() {
        return equatorialPos;
    }

    public double angularSize() {
        return angularSize;
    }

    public double magnitude() {
        return magnitude;
    }

    public String info(){
        return name();
    }

    @Override
    public String toString() {
        return info();
    }
}
