package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EquatorialCoordinatesTest {

    @Test
    void ofThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates ec = EquatorialCoordinates.of(0,102);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates ec = EquatorialCoordinates.of(-3,45);
        });
    }

    @Test
    void ofDegThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates ec = EquatorialCoordinates.ofDeg(360,-46);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            EquatorialCoordinates ec = EquatorialCoordinates.ofDeg(-13,76);
        });
    }

    @Test
    void isValidRa() {
        assertTrue(EquatorialCoordinates.isValidRa(30.0));
        assertFalse(EquatorialCoordinates.isValidRa(360));
        assertFalse(EquatorialCoordinates.isValidRa(-60));
    }

    @Test
    void isValidDec() {
        assertTrue(EquatorialCoordinates.isValidDec(-90.0));
        assertFalse(EquatorialCoordinates.isValidDec(360));
    }

    @Test
    void ra() {
        assertEquals(180, EquatorialCoordinates.of(12,45).ra(), 1e-2);
    }

    @Test
    void raDeg() {
    }

    @Test
    void raHr() {
    }

    @Test
    void dec() {
    }

    @Test
    void decDeg() {
    }

    @Test
    void testToString() {
    }
}