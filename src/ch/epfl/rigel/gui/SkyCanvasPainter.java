package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.xml.crypto.dsig.Transform;
import java.util.*;

public class SkyCanvasPainter extends Application {
    private Canvas canvas;
    private GraphicsContext gc;

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
    }

    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawStars (ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        drawAsterisms(sky);
    }

    private void drawAsterisms(ObservedSky sky) {
        List<Star> starsInSky = List.copyOf(sky.stars());
        List<Double> starCoord = List.copyOf(sky.starCoordinates());
        Set<Asterism> asterismsInCatalogue = Set.copyOf(sky.asterisms());
        Set<Asterism> asterismsInSky = new HashSet<>();

        //Takes in account only asterisms that contain at least one star in our observed sky
        for (Asterism asterism : asterismsInCatalogue) {
            if (!Collections.disjoint(asterism.stars(), starsInSky))
                asterismsInSky.add(asterism);
        }

        for (Asterism asterism : asterismsInSky) {
            List<Integer> asterismIndex = List.copyOf(sky.asterismIndex(asterism));
            List<CartesianCoordinates> coord = new ArrayList<>();
            for (Integer i : asterismIndex)
                coord.add(CartesianCoordinates.of(starCoord.get(2 * i), starCoord.get(2 * i + 1)));
            drawLinesForAsterism(coord);
        }
    }

    private void drawLinesForAsterism (List<CartesianCoordinates> coord) {
        Bounds b = canvas.getBoundsInLocal();
        gc.setStroke(Color.BLUE);
        gc.beginPath();
        boolean previousInBound = true;
        for (CartesianCoordinates c : coord) {
            double x = c.x();
            double y = c.y();
            //draws only a line if two consecutive stars not out of bound
            if (previousInBound) {
                gc.lineTo(x, y);
            } else {
                if (b.contains(x, y)) {
                    gc.lineTo(x, y);
                } else {
                    gc.moveTo(x, y);
                }
            }
            previousInBound = b.contains(x, y);
        }
        gc.stroke();
    }
}
