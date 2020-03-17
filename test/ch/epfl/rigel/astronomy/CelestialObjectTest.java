package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CelestialObjectTest {

    @Test
    void throwsIAE(){
        assertThrows(IllegalArgumentException.class, () -> {new Planet("test", EquatorialCoordinates.ofDeg(0,0), -1, -24);});
        assertThrows(NullPointerException.class, () -> {new Planet(null, EquatorialCoordinates.ofDeg(0,0), 0, -24);});
        assertThrows(NullPointerException.class, () -> {new Planet("test", null, 0, -24);});
    }

    @Test
    void sameInfo(){
        Planet test = new Planet("test", EquatorialCoordinates.ofDeg(0,0), 1, -24);
        assertEquals(test.toString(), test.info());
    }



}