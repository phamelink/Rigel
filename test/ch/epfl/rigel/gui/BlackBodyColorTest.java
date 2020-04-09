package ch.epfl.rigel.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackBodyColorTest {

    @Test
    void colorForTemperature() {
        System.out.println(BlackBodyColor.colorForTemperature(10500));
    }
}