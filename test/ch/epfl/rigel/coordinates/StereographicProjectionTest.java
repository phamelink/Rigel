package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StereographicProjectionTest {

    @Test
    void circleCenterForParallel() {
    }

    @Test
    void circleRadiusForParallel() {
    }

    @Test
    void apply() {
    }

    @Test
    void inverseApply() {
        StereographicProjection sp = new StereographicProjection(HorizontalCoordinates.of(1,0));
        sp.inverseApply(CartesianCoordinates.of(0,0));
    }

    @Test
    void applyToAngle() {
    }
}