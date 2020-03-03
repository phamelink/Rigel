package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EclipticToEquatorialConversionTest {

    @Test
    void apply() {

        EclipticToEquatorialConversion conv = new EclipticToEquatorialConversion(ZonedDateTime.of(2007,7,6,0,0,0,0, ZoneOffset.UTC));
        EclipticCoordinates ecl = EclipticCoordinates.of(Angle.ofDMS(139, 41, 10), Angle.ofDMS(4, 52,31));

        EquatorialCoordinates eq = conv.apply(ecl);
        System.out.println(eq);
        //assertEquals(eq.raDeg())

    }
}