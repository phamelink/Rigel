package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StereographicProjectionTest {

    @Test
    void circleCenterForParallel() {
        EquatorialToHorizontalConversion toHor = new EquatorialToHorizontalConversion(Epoch.J2000.epoch, GeographicCoordinates.ofDeg(0,0));
        HorizontalCoordinates hc = HorizontalCoordinates.ofDeg(0,0);
        StereographicProjection sp = new StereographicProjection(hc);
        HorizontalCoordinates hc2 = HorizontalCoordinates.ofDeg(0,0);
        System.out.println(hc2);
        CartesianCoordinates xy = sp.circleCenterForParallel(hc2);
        System.out.println("center: " + xy);
    }

    @Test
    void circleRadiusForParallel() {
        EquatorialToHorizontalConversion toHor = new EquatorialToHorizontalConversion(Epoch.J2000.epoch, GeographicCoordinates.ofDeg(0,0));
        HorizontalCoordinates hc = HorizontalCoordinates.ofDeg(0,0);
        StereographicProjection sp = new StereographicProjection(hc);
        HorizontalCoordinates hc2 = HorizontalCoordinates.ofDeg(90,45);
        System.out.println(sp.circleCenterForParallel(hc2));
        System.out.println(sp.apply(hc2));
        double r = sp.circleRadiusForParallel(hc2);
        System.out.println("radius: " + r);


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
        StereographicProjection sp = new StereographicProjection(HorizontalCoordinates.of(0,0));
        assertEquals(0.82842712474, sp.applyToAngle(Math.PI / 2), 1e-8);
        assertEquals(2.0, sp.applyToAngle(Math.PI ), 1e-8);
    }

  }