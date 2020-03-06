package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EquatorialToHorizontalConversionTest {

    @Test
    void apply() {

        EquatorialToHorizontalConversion converter = new EquatorialToHorizontalConversion(
                ZonedDateTime.of(2009, 7, 22, 5, 9, 50,0, ZoneOffset.of("Z")),
                GeographicCoordinates.ofDeg(17, 6));

        System.out.println(converter.apply(EquatorialCoordinates.of(Angle.ofDeg(121.71), Angle.ofDMS(20, 14, 47))));

    }
}