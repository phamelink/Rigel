package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
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
        assertEquals(3.14, EquatorialCoordinates.of(12,45).ra(), 1e-2);
    }

    @Test
    void raDeg() {
        assertEquals(180, EquatorialCoordinates.of(12,45).raDeg(), 1e-2);
    }

    @Test
    void raHr() {
        assertEquals(14, EquatorialCoordinates.of(14,45).raHr(), 1e-4);
    }

    @Test
    void dec() {
        assertEquals(0.785, EquatorialCoordinates.of(12,45).dec(), 1e-2);
    }

    @Test
    void decDeg() {
        assertEquals(73, EquatorialCoordinates.of(12,73).decDeg(), 1e-2);
    }

    @Test
    void testToString() {
        EquatorialCoordinates ec1 = EquatorialCoordinates.of(13.4,38);
        assertEquals("(ra=13.4000h, dec=38.0000°)", ec1.toString());
        EquatorialCoordinates ec2 = EquatorialCoordinates.ofDeg(180,38);
        assertEquals("(ra=12.0000h, dec=38.0000°)", ec2.toString());
    }
}