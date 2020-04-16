package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Star;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.application.Application;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import java.util.*;

public class SkyCanvasPainter {
    private Canvas canvas;
    private GraphicsContext gc;

    private static ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();



    }


    public void clear() {

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void drawStars (ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        //Transform t = Transform.scale(1300, -1300);
        //t.createConcatenation(Transform.translate(canvas.getWidth()/2, canvas.getHeight()/2));


        int starCount = sky.starCoordinates().length /2;
        double[] transformedPoints = new double[starCount * 2];
        planeToCanvas.transform2DPoints(sky.starCoordinates(),0,transformedPoints,0, starCount );

        for (int i = 0; i < starCount; i++) {
            Star toDraw = sky.stars().get(i);
            double starDiameter = getStarDiameter(toDraw)*1300;
            gc.setFill(BlackBodyColor.colorForTemperature(toDraw.colorTemperature()));
            gc.fillOval(transformedPoints[2*i], transformedPoints[2*i + 1], starDiameter, starDiameter);
        }

        drawAsterisms(sky, projection, planeToCanvas);
    }

    private void drawAsterisms(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        List<Star> starsInSky = sky.stars();
        double[] starCoord =sky.starCoordinates();
        Set<Asterism> asterismsInCatalogue =sky.asterisms();
        Set<Asterism> asterismsInSky = new HashSet<>();

        //Takes in account only asterisms that contain at least one star in our observed sky
        for (Asterism asterism : asterismsInCatalogue) {
            if (!Collections.disjoint(asterism.stars(), starsInSky))
                asterismsInSky.add(asterism);
        }

        for (Asterism asterism : asterismsInSky) {
            List<Integer> asterismIndex = sky.asterismIndex(asterism);
            List<CartesianCoordinates> coord = new ArrayList<>();
            System.out.println(asterism);
            for (Integer i : asterismIndex)
               coord.add(CartesianCoordinates.of(starCoord[2 * i], starCoord[2 * i + 1]));
            drawLinesForAsterism(coord, planeToCanvas);
        }
    }

    private void drawLinesForAsterism (List<CartesianCoordinates> coord, Transform planeToCanvas) {
        Bounds b = canvas.getBoundsInLocal();
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        gc.beginPath();
        boolean previousInBound = true;
        for (CartesianCoordinates c : coord) {
            Point2D point = planeToCanvas.transform(c.x(), c.y());
            double x = point.getX();
            double y = point.getY();
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

    private double getStarDiameter(Star star){

        final double mag = MAGNITUDE_INTERVAL.clip(star.magnitude());
        final double factor = (99-17*mag) / (140);
        System.out.println(2 * factor * Math.tan(Angle.ofDeg(0.5) / 4)*1300);
        return 2 * factor * Math.tan(Angle.ofDeg(0.5) / 4);
    }
}
