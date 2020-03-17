package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SunTest {

    @Test
    void magNameTest(){
        Sun sun = new Sun(EclipticCoordinates.of(0,0), EquatorialCoordinates.ofDeg(0,0), 2, 0);
        assertEquals(-26.7, sun.magnitude(), 0.1);
        assertEquals("Soleil", sun.name());
    }

}