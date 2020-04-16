package ch.epfl.rigel.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BlackBodyColorTest {

    @Test
    void colorForTemperature() {

        for (int i = 1000; i < 15000; i = i + 5) {
            BlackBodyColor.colorForTemperature(i);
        }
        System.out.println(BlackBodyColor.colorForTemperature(10500));
    }
}