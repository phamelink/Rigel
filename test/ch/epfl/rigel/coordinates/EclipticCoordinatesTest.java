package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EclipticCoordinatesTest {

    @Test
    void testToString() {
        EclipticCoordinates a = EclipticCoordinates.ofDeg(45, 20);
        assertEquals("(λ=45.0000°, β=20.0000°)", a.toString());
        assertEquals(Angle.TAU / 8, a.lon(), 1e-4);
    }

    @Test
    void testThrows(){
        assertThrows(IllegalArgumentException.class, () -> {EclipticCoordinates.ofDeg(40000,-40000);});
    }
}