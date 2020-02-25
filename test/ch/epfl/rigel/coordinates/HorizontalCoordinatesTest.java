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

    @Test
    void octantTest1(){
        HorizontalCoordinates hc = HorizontalCoordinates.ofDeg(335,0);
        System.out.println(hc);
        assertEquals("NO", HorizontalCoordinates.ofDeg(335,0)
                .azOctantName("N", "E", "S", "O"));
        assertEquals("bimwee", HorizontalCoordinates.ofDeg(335,0)
                .azOctantName("bim", "bam", "boum", "wee"));
        assertEquals("wee", HorizontalCoordinates.of(Angle.normalizePositive(Angle.ofDeg(-90.0)),0.8)
                .azOctantName("bim", "bam", "boum", "wee"));
    }

}