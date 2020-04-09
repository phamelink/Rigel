package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ObservedSkyTest {

    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    private static final String ASTERISMS =
            "/asterisms.txt";

    @Test
    void distanceTest(){
        StarCatalogue cat = new StarCatalogue.Builder().buildFromFiles(HYG_CATALOGUE_NAME, ASTERISMS);
        ObservedSky os = new ObservedSky(Epoch.J2010.epoch, GeographicCoordinates.ofDeg(0,0),
                new StereographicProjection(HorizontalCoordinates.ofDeg(0,0)),cat);
        long ref = System.currentTimeMillis();
        int trials = 100;
        for (int i = 0; i < trials; i++) {
            System.out.println(os.objectClosestTo(CartesianCoordinates.of(0.05*i,0), 0.2));
        }
        System.out.println("Average time per call: " +(System.currentTimeMillis() - ref)/trials+ "ms");


    }

}