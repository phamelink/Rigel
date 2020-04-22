package ch.epfl.rigel.gui;


import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;
import javafx.scene.paint.Color;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

public class BlackBodyColor {

    //Prevent instantiation
    private BlackBodyColor() {
    }

    private static final String DATA_FILE =
            "/bbr_color.txt";

    private static Interval temperatureDomain;

    /*
    KEY: Temperature
    Image: The corresponding color
     */
    private static HashMap<Integer, Color> COLOR_MAP = loadMap(DATA_FILE);

    private static HashMap<Integer, Color> loadMap(String file) {

        HashMap<Integer, Color> mapToReturn = new HashMap<>();

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(BlackBodyColor.class.getResourceAsStream(file), StandardCharsets.US_ASCII))) {

            String lineContent;

            while((lineContent = reader.readLine() )!= null){
                //Ignore irrelevant lines
                if(lineContent.charAt(0) == '#' || lineContent.substring(10, 15).equals(" 2deg")) continue;

                Integer kelvin = parseInt(lineContent.substring(1,6));
                Color color = Color.web(lineContent.substring(80,87));
                mapToReturn.put(kelvin, color);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        temperatureDomain = ClosedInterval.of(Collections.min(mapToReturn.keySet()),Collections.max(mapToReturn.keySet()));

        return mapToReturn;
    }

    /**
     * Custom Integer parser that removes unnecessary spaces in front of the String.
     * @param str sting to parse
     * @return an integer extracted from the string
     */
    public static int parseInt(String str){
        String parsing = str;
        while(parsing.charAt(0) == ' '){
            parsing = parsing.substring(1);
        }
        return Integer.parseInt(parsing);
    }


    public static Color colorForTemperature(int temperature){
        Preconditions.checkInInterval(temperatureDomain, temperature);
        int temperatureApproximation = Math.round((float) temperature / 100 ) * 100;
        return COLOR_MAP.get(temperatureApproximation);
    }

}
