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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII))) {
            reader.readLine();
            String s;
            int line = 0;
            while ((s = reader.readLine()) != null) {
                ++line;
                String[] values = s.split(",");

                //Read hipparcos ID
                int hipID;
                try {
                    hipID = Integer.parseInt(values[ColumnNames.HIP.ordinal()]);
                } catch (NumberFormatException e) {
                    System.err.println("Defaulting to 0 for HPC ID on line " + line);
                    hipID = 0;
                }

                //Read star name
                final String name;
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

                //Read star equatorial coordinates (assuming non null)
                final EquatorialCoordinates coord = EquatorialCoordinates.of(
                        Double.parseDouble(values[ColumnNames.RARAD.ordinal()]),
                        Double.parseDouble(values[ColumnNames.DECRAD.ordinal()]));

                //Read star magnitude
                float magnitude;
                try {
                    magnitude = (float) Double.parseDouble(values[ColumnNames.MAG.ordinal()]);
                } catch (NumberFormatException e) {
                    System.err.println("Defaulting to 0 for MAGNITUDE on line " + line);
                    magnitude = 0;
                }

<<<<<<< HEAD
                //Read star magnitude
                float colorIndex;
=======
                float colorIndex = 0;
>>>>>>> ac054f3e2cd627c2fb1cc8c2dcffdf07b8318bec
                try {
                    colorIndex = (float) Double.parseDouble(values[ColumnNames.CI.ordinal()]);
                } catch (NumberFormatException e) {
                    System.err.println("Defaulting to 0 for COLOR INDEX on line " + line);
                    colorIndex = 0;
                }

<<<<<<< HEAD
                //Add star to builder using read values
=======
                StringBuilder str = new StringBuilder();
                str.append(hipID).append(" ").append(name).append(" ").append(coord).append(" ").append(magnitude).append(" ").append(colorIndex);
                //System.out.println(str);
>>>>>>> ac054f3e2cd627c2fb1cc8c2dcffdf07b8318bec
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