package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AsterismTest {

    @Test
    void stars() {
        ArrayList<Star> a = new ArrayList<>();
        assertThrows(IllegalArgumentException.class, ()->{new Asterism(a);});


    }
}