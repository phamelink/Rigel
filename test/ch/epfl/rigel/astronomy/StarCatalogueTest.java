package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

class StarCatalogueTest {
    private static final String HYG_CATALOGUE_NAME =
            "/hygdata_v3.csv";

    private static final String ASTERISMS =
            "/asterisms.txt";
    private static final List<Integer> ast = List.of(24436,27366,26727,27989,28614,29426,28716);

    @Test
    void buildTest(){
        try(InputStream stream = getClass().getResourceAsStream(HYG_CATALOGUE_NAME);
        InputStream asterisms = getClass().getResourceAsStream(ASTERISMS)) {
            StarCatalogue cat = new StarCatalogue.Builder().loadFrom(stream, HygDatabaseLoader.INSTANCE)
                    .loadFrom(asterisms, AsterismLoader.INSTANCE).build();
            ArrayList<Star> stars = new ArrayList<>();
            for(Integer i : ast){
                stars.add(new Star(i, "", EquatorialCoordinates.ofDeg(0,0),0,0));

            }

            //System.out.println(cat.asterismIndices(new Asterism(stars)));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}