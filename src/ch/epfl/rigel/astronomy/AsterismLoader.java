package ch.epfl.rigel.astronomy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Asterism Loader
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public enum AsterismLoader implements StarCatalogue.Loader{
    INSTANCE;

    /**
     * loads asterisms from inputStream and adds them to the star catalogue being built by builder
     * @param inputStream stream to add stars from
     * @param builder builder to add stars with
     * @throws IOException if there is an error with the inputstream
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII)) {
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String s;
            while ((s=reader.readLine()) != null) {
                List<Star> starsInAsterism = new ArrayList<>();
                String[] values = s.split(",");
                for (String indice : values) {
                    starsInAsterism.add(builder.getStarById(Integer.parseInt(indice)));
                }
                builder.addAsterism(new Asterism(starsInAsterism));
            }
        }
    }


}
