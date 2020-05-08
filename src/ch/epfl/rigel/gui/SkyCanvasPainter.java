package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
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

/**
 * SkyCanvasPainter
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class SkyCanvasPainter {
    private Canvas canvas;
    private GraphicsContext gc;

    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);
    private static final double HORIZON_DISTANCE_STEPS = 10;

    /**
     * SkyCanvasPainterConstructor
     * @param canvas canvas on which the sky will be painted
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
    }

    /**
     * clears the canvas to a black canvas
     */
    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /*
    DRAW STARS & ASTERISMS
     */

    /**
     * draws all stars and asterisms
     * @param sky observed sky
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void drawStars (ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        drawAsterisms(sky, planeToCanvas);
        List<CelestialObject> stars = List.copyOf(sky.stars());
        drawCelestialObject(sky.starCoordinates(), stars, projection, planeToCanvas);
    }

    private void drawAsterisms(ObservedSky sky, Transform planeToCanvas) {
        double[] starCoord = sky.starCoordinates();
        for (Asterism asterism : sky.asterisms()) {
            List<Integer> asterismIndex = sky.asterismIndex(asterism);
            List<CartesianCoordinates> coord = new ArrayList<>();
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

    /*
    DRAW PLANETS
     */

    /**
     * draws all planets
     * @param sky observed sky
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){
        List<CelestialObject> planets = List.copyOf(sky.planets());
        double[] planetCoordinates = new double[sky.planetCoordinates().size()];
        for (int i = 0; i < planetCoordinates.length; i++)  planetCoordinates[i] = sky.planetCoordinates().get(i);
        drawCelestialObject(planetCoordinates, planets, projection, planeToCanvas);
    }

    /*
    DRAW SUN
     */

    /**
     * draws sun
     * @param sky observed sky
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){
        final double sunDiameter = planeToCanvas.deltaTransform(projection.applyToAngle(Angle.ofDeg(0.5)), 0).getX();
        CartesianCoordinates sunPosition = sky.sunPosition();
        Point2D sunPositionOnCanvas = planeToCanvas.transform(sunPosition.x(), sunPosition.y());

        gc.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.25));
        drawCenteredCiruclarBody(sunPositionOnCanvas, sunDiameter*2.2);

        gc.setFill(Color.YELLOW);
        drawCenteredCiruclarBody(sunPositionOnCanvas, sunDiameter + 2);

        gc.setFill(Color.WHITE);
        drawCenteredCiruclarBody(sunPositionOnCanvas, sunDiameter);
    }

    /*
    DRAW MOON
     */

    /**
     * draws moon
     * @param sky observed sky
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){
        List<CelestialObject> moon = List.of(sky.moon());
        double[] moonCoordinates = {sky.moonPosition().x(), sky.moonPosition().y()};
        drawCelestialObject(moonCoordinates, moon, projection, planeToCanvas);
    }

    /*
    DRAW HORIZON
     */

    /**
     * draws horizon in red
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void drawHorizon(StereographicProjection projection, Transform planeToCanvas){
        gc.setStroke(Color.RED);
        gc.setLineWidth(2);
        CartesianCoordinates fromProjection = projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(0,0));
        System.out.println("from projection: " + fromProjection.toString());
        double projRadius = Math.abs(projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0,0)));
        Point2D horizonCenter = planeToCanvas.transform(fromProjection.x() , fromProjection.y());
        System.out.println("from projection transformed: " + horizonCenter.toString());
        double radius = planeToCanvas.deltaTransform(projRadius, 0).getX();
        gc.strokeOval(horizonCenter.getX() - radius, horizonCenter.getY() - radius ,radius * 2, radius * 2);

        gc.setFill(Color.RED);
        for (HorizontalCoordinates.OCTANT octant: HorizontalCoordinates.OCTANT.values()) {
            CartesianCoordinates octantCartesian = projection.apply(HorizontalCoordinates.ofDeg(octant.getOctantAngle(),-0.5));
            Point2D octantCoord = planeToCanvas.transform(octantCartesian.x(), octantCartesian.y());
            gc.fillText(octant.name(), octantCoord.getX(), octantCoord.getY());
        }
    }

    /**
     * draws all celestial objects in sky and the horizon
     * @param sky observed sky
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void drawAll(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){
        clear();
        drawStars(sky, projection, planeToCanvas);
        drawPlanets(sky, projection, planeToCanvas);
        drawSun(sky, projection, planeToCanvas);
        drawMoon(sky, projection, planeToCanvas);
        drawHorizon(projection, planeToCanvas);
    }

    /*
    PRIVATE UTILITY CLASSES
     */

    private void drawCenteredCiruclarBody(Point2D center, double diameter){
        gc.fillOval(center.getX()-diameter/2, center.getY()-diameter/2, diameter, diameter);
    }

    private double getCelestialObjectDiameter(CelestialObject celestialObject, StereographicProjection projection){
        final double mag = MAGNITUDE_INTERVAL.clip(celestialObject.magnitude());
        final double factor = (99-17*mag) / (140);
        return factor * projection.applyToAngle(Angle.ofDeg(0.5));
    }

    //TODO color should be set by parent class before method call
    private void drawCelestialObject(double[] objectCoordinates, List<CelestialObject> objects,
                                    StereographicProjection projection, Transform planeToCanvas) {
        int objectCount = objects.size();
        double[] transformedPoints = new double[objectCoordinates.length];
        planeToCanvas.transform2DPoints(objectCoordinates, 0, transformedPoints, 0, objectCount);

        for (int i = 0; i < objectCount; i++) {
            CelestialObject toDraw = objects.get(i);
            double diameter = planeToCanvas.deltaTransform(getCelestialObjectDiameter(toDraw, projection), 0).getX();

            if (toDraw instanceof Star) gc.setFill(BlackBodyColor.colorForTemperature(((Star) toDraw).colorTemperature()));
            else if (toDraw instanceof Planet) gc.setFill(Color.LIGHTGRAY);
            else if (toDraw instanceof Moon) gc.setFill(Color.WHITE); //TODO bad practice



            Point2D position = new Point2D(transformedPoints[2*i], transformedPoints[2*i + 1]);
            drawCenteredCiruclarBody(position, diameter); //TODO rewrite this method here internally, never used elsewhere
        }
    }


}
