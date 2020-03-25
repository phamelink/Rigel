package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PlanetModelTest {

    @Test
    void at() {
        ZonedDateTime when = ZonedDateTime.of(2003, 11, 22, 0, 0, 0, 0, ZoneOffset.UTC);
        double daysSince = Epoch.J2010.daysUntil(when);
        assertEquals(-2231, daysSince, 1e-8);
        EclipticToEquatorialConversion conv = new EclipticToEquatorialConversion(when);
        Planet j = PlanetModel.JUPITER.at(daysSince, conv);

        /*
        EquatorialCoordinates expEqCoord = conv.apply(EclipticCoordinates.of(Angle.ofDeg(166.310510), Angle.ofDeg(1.036466)));

        assertEquals((11 + 11.0/60 + 14.0/3600), expEqCoord.raHr());
        assertEquals(Angle.ofDMS(6,21,25), expEqCoord.dec());


         */
    }

    @Test
    void getDatedPlanetInfo() {
    }
}