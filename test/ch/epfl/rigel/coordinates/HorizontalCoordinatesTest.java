package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HorizontalCoordinatesTest {

    @Test
    void ofThrowsException(){

        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates hc = HorizontalCoordinates.of(0.5, Angle.TAU);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates hc = HorizontalCoordinates.of(-0.1, -0.5);
        });
    }

}