package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * HygDatabaseLoader
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public enum HygDatabaseLoader implements StarCatalogue.Loader {
    INSTANCE;

    /**
     * loads stars from inputStream and adds them to the star catalogue being built by builder
     * @param inputStream stream to add stars from
     * @param builder builder to add stars with
     * @throws IOException if there is an error with the inputstream
     */
    @Override
    public void load(InputStream inputStream, StarCatalogue.Builder builder) throws IOException {
        try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.US_ASCII)) {
            BufferedReader reader = new BufferedReader(inputStreamReader);

            reader.readLine();
            String s;
            while ((s = reader.readLine()) != null) {
                String[] values = s.split(",");
                int hipID;
                try {
                    hipID = Integer.parseInt(values[ColumnNames.HIP.ordinal()]);
                } catch (NumberFormatException e) {
                    hipID = 0;
                }

                String name;
                if (!values[ColumnNames.PROPER.ordinal()].isEmpty()) {
                    name = values[ColumnNames.PROPER.ordinal()];
                } else {
                    String bayer = !values[ColumnNames.BAYER.ordinal()].isEmpty() ? values[ColumnNames.BAYER.ordinal()] : "?";
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder
                            .append(bayer)
                            .append(" ")
                            .append(values[ColumnNames.CON.ordinal()]);
                    name = stringBuilder.toString();
                }

                EquatorialCoordinates coord = EquatorialCoordinates.of(
                        Double.parseDouble(values[ColumnNames.RARAD.ordinal()]),
                        Double.parseDouble(values[ColumnNames.DECRAD.ordinal()]));

                float magnitude;
                try {
                    magnitude = Integer.parseInt(values[ColumnNames.MAG.ordinal()]);
                } catch (NumberFormatException e) {
                    magnitude = 0;
                }

                float colorIndex = 0;
                try {
                    colorIndex = Integer.parseInt(values[ColumnNames.MAG.ordinal()]);
                } catch (NumberFormatException e) {
                    colorIndex = 0;
                }
                builder.addStar(new Star(hipID, name, coord, magnitude, colorIndex));
            }

        }
    }






    private enum ColumnNames {
        ID, HIP, HD, HR, GL, BF, PROPER, RA, DEC, DIST, PMRA, PMDEC,
        RV, MAG, ABSMAG, SPECT, CI, X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD, BAYER, FLAM, CON,
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX;
    }
}
