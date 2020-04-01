package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MoonModelTest {

    @Test
    void at() {
        ZonedDateTime time = ZonedDateTime.of(2003, 9, 1, 0,0,0,0, ZoneOffset.UTC);
        Moon test = MoonModel.MOON.at(Epoch.J2010.daysUntil(time),
                new EclipticToEquatorialConversion(time));
        EquatorialCoordinates eql = (new EclipticToEquatorialConversion(time)).apply(EclipticCoordinates.of(Angle.ofDeg(214.862515),Angle.ofDeg( 1.716257)));
        assertEquals(eql.ra(), test.equatorialPos().ra(), 1e-6);
        assertEquals(eql.dec(), test.equatorialPos().dec(), 1e-6);

    }
}