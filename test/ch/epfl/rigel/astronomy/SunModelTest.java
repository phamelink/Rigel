package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SunModelTest {

    @Test
    void at() {
        ZonedDateTime when = ZonedDateTime.of(2003, 7, 27, 0, 0, 0, 0, ZoneOffset.UTC);
        double daysSince = Epoch.J2010.daysUntil(when);
        EclipticToEquatorialConversion conv = new EclipticToEquatorialConversion(when);
        Sun s = SunModel.SUN.at(daysSince, conv);
        assertEquals(123.580601, s.eclipticPos().lonDeg(), 1e-6);
        /*
        System.out.println("Ecliptic position of sun " + s.eclipticPos());
        System.out.println("Expected right ascension: " + (8 + 23.0/60 + 34.0/3600) + " hours" + ", " + Angle.ofHr(8 + 23.0/60 + 34.0/3600) + " rad");
        System.out.println("Actual: " + conv.apply(s.eclipticPos()).ra());
        System.out.println("Expected declination: 19° 21' 10'', " + Angle.ofDMS(19,21,10) + " rad");
        System.out.println("Actual: " + conv.apply(s.eclipticPos()).dec());
        */
    }
}