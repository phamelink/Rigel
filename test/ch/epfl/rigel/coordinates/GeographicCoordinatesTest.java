package ch.epfl.rigel.coordinates;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeographicCoordinatesTest {

    @Test
    void isValidLonDegTest() {
        assertTrue(GeographicCoordinates.isValidLonDeg(30.0));
        assertFalse(GeographicCoordinates.isValidLonDeg(180));
    }

    @Test
    void isValidLatDegTest() {
        assertTrue(GeographicCoordinates.isValidLonDeg(0.0));
        assertFalse(GeographicCoordinates.isValidLonDeg(-1939290000));
    }

    @Test
    void toStringTest(){
        GeographicCoordinates g = GeographicCoordinates.ofDeg(6.57, 46.52);
        System.out.println(g);
        assertEquals("(lon=6.5700°, lat=46.5200°)", g.toString());
    }
}