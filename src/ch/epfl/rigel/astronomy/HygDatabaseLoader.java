package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

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
            while ((s = reader.readLine()) != null) {
                String[] values = s.split(",");

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
                int hipID = parseIntColumn(ColumnNames.HIP, values);
                float magnitude = parseFloatColumn(ColumnNames.MAG, values);
                float colorIndex = parseFloatColumn(ColumnNames.CI, values);
                builder.addStar(new Star(hipID, name, coord, magnitude, colorIndex));
            }

        }
    }

    private int parseIntColumn(ColumnNames column, String[] values){
        String value = values[column.ordinal()];
        if(value.equals("")){
            return 0;
        }else{
            return Integer.parseInt(value);
        }
    }

    private float parseFloatColumn(ColumnNames column, String[] values){
        String value = values[column.ordinal()];
        if(value.equals("")){
             return 0f;
        }else{
            return Float.parseFloat(value);
        }
    }


    private enum ColumnNames {
        ID, HIP, HD, HR, GL, BF, PROPER, RA, DEC, DIST, PMRA, PMDEC,
        RV, MAG, ABSMAG, SPECT, CI, X, Y, Z, VX, VY, VZ,
        RARAD, DECRAD, PMRARAD, PMDECRAD, BAYER, FLAM, CON,
        COMP, COMP_PRIMARY, BASE, LUM, VAR, VAR_MIN, VAR_MAX
    }
}