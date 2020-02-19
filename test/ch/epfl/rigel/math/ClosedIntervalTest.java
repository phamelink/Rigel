package ch.epfl.rigel.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClosedIntervalTest {

    @Test
    void containsNonTrivialTest() {
        ClosedInterval a  = ClosedInterval.symmetric(4);
        Double b = 0.12;
        assertTrue(a.contains(b));
    }

    @Test
    void symmetricThrowsException(){
        assertThrows(IllegalArgumentException.class, () -> {ClosedInterval.symmetric(-1);});
    }
//sefhjnsdkfjnasdfkhjasfmnxydf ajsnraksrw
    @Test
    void clipTrivialTest() {
        ClosedInterval a  = ClosedInterval.of(0, 1);
        Double b = 0.12;
        assertEquals(a.clip(b), b, 1e-6);
    }

    @Test
    void clipTrivialTest2() {
        ClosedInterval a  = ClosedInterval.of(0, 1);
        Double b = 4.6;
        assertEquals(a.clip(b), 1, 1e-6);
    }

}