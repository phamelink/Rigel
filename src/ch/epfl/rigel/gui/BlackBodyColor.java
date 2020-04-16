package ch.epfl.rigel.gui;


//import java.awt.*;
import javafx.scene.paint.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class BlackBodyColor {

    //Prevent instantiation
    private BlackBodyColor() {
    }

    private static final String DATA_FILE =
            "/bbr_color.txt";
    private static HashMap<Integer, Color> COLOR_MAP = loadMap(DATA_FILE);

    private static HashMap<Integer, Color> loadMap(String file) {

        HashMap<Integer, Color> mapToReturn = new HashMap<>();

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(BlackBodyColor.class.getResourceAsStream(file), StandardCharsets.US_ASCII))) {

            String lineContent;
            while((lineContent = reader.readLine() )!= null){
                if(lineContent.charAt(0) == '#' || lineContent.substring(10, 15).equals(" 2deg")) continue;
                Integer kelvin = parseInt(lineContent.substring(1,6));
                System.out.println(lineContent.substring(80,87));
                Color color = Color.web(lineContent.substring(80,87));
                System.out.println(kelvin + " - " + color.getRed());
                mapToReturn.put(kelvin, color);
            }
            System.out.println(mapToReturn);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapToReturn;
    }

    public static int parseInt(String str){
        String parsing = str;
        while(parsing.charAt(0) == ' '){
            parsing = parsing.substring(1, parsing.length()-1);
        }
        return Integer.parseInt(parsing);
    }

    public static Color colorForTemperature(int temperature){
        return COLOR_MAP.get(temperature);
    }

}
