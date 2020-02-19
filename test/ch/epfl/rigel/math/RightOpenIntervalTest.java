package ch.epfl.rigel.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RightOpenIntervalTest {

    @Test
    void reduceTest(){
        RightOpenInterval a = RightOpenInterval.of(-180,180);
        System.out.println(a);
        assertEquals(a.reduce(200),-160, 1e-6);
    }

}