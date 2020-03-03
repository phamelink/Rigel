package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SiderealTimeTest {

    @Test
    void greenwich() {
        assertEquals(Angle.ofHr(4.668120), SiderealTime.greenwich(ZonedDateTime.of(1980, 4, 22, 14, 36, 52, 0, ZoneId.of("+0"))), 1e-4);
    }

    @Test
    void local() {
        assertEquals(Angle.ofDeg(146.02), SiderealTime.local(ZonedDateTime.of(1980, 4, 22, 14, 36, 52, 0, ZoneId.of("+0")), GeographicCoordinates.ofDeg(76,0)), 1e-4);
    }
}