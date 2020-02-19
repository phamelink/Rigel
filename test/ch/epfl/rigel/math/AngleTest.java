package ch.epfl.rigel.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AngleTest {

    @Test
    void normalizePositive() {
        assertEquals( 0 ,Angle.normalizePositive(Angle.TAU*4),1e-6);
    }

    @Test
    void ofArcsec() {
        assertEquals( 0.00666, Angle.ofArcsec(24),1e-6);
    }

    @Test
    void ofDMS() {
        assertEquals(0.637, Angle.ofDMS(36,32,34), 1e-4);
    }

    @Test
    void ofDMSExceptionTest() {
        assertThrows(IllegalArgumentException.class, () ->{Angle.ofDMS(0, 74,20);});
    }

    @Test
    void ofDeg() {
        assertEquals(1.5707, Angle.ofDeg(90), 1e-5);
    }

    @Test
    void toDeg() {
        assertEquals(11.45915, Angle.toDeg(0.2), 1e-6);
    }

    @Test
    void ofHr() {
        assertEquals(Angle.TAU, Angle.ofHr(24), 1e-6);
    }

    @Test
    void toHR() {
        assertEquals(24, Angle.toHR(Angle.TAU));
    }
}