package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StarTest {

    @Test
    void hipparcosId() {
        assertThrows(IllegalArgumentException.class, () -> {new Star(-1, "test", EquatorialCoordinates.of(0,0), 0,0);});
        assertThrows(IllegalArgumentException.class, () -> {new Star(0, "test", EquatorialCoordinates.of(0,0), 0,-0.5010000001f);});

    }

    @Test
    void colorTemperature() {
        Star rigel = new Star(0, "Rigel", EquatorialCoordinates.ofDeg(0,0), 0,-0.03f);
        Star betelgeuse = new Star(0, "Rigel", EquatorialCoordinates.ofDeg(0,0), 0,1.50f);
        assertEquals(10515, rigel.colorTemperature());
        assertEquals(3793, betelgeuse.colorTemperature());
    }
}