package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public final class HorizontalCoordinatesTest {

    @Test
    void ofandOfDegThrowsException(){

        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates hc = HorizontalCoordinates.of(0.5, Angle.TAU);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates hc = HorizontalCoordinates.of(-0.1, -0.5);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates hc = HorizontalCoordinates.ofDeg(3, 360);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            HorizontalCoordinates hc = HorizontalCoordinates.of(-10, -10);
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

    @Test
    void angularDistanceTest(){
        HorizontalCoordinates a = HorizontalCoordinates.ofDeg(6.5682, 46.5183);
        HorizontalCoordinates b = HorizontalCoordinates.ofDeg(8.5476, 47.3763);

        double d = a.angularDistanceTo(b);
        assertEquals(0.0279, d, 1e-4);

        a = HorizontalCoordinates.ofDeg(8.5476, 0);
        b = HorizontalCoordinates.ofDeg(8.5476, 0);
        d = a.angularDistanceTo(b);
        assertEquals(0, d, 1e-4);

        a = HorizontalCoordinates.ofDeg(8.5476, 0);
        b = HorizontalCoordinates.ofDeg(0, 0);
        d = a.angularDistanceTo(b);
        assertEquals(Angle.ofDeg(8.5476), d, 1e-4);

        a = HorizontalCoordinates.ofDeg(0, 0);
        b = HorizontalCoordinates.ofDeg(0, 90);
        d = a.angularDistanceTo(b);
        assertEquals(Angle.ofDeg(90), d, 1e-4);


    }

}