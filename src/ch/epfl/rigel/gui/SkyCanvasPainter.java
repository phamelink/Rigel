package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;

import java.util.*;
import java.util.function.Function;

/**
 * SkyCanvasPainter
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class SkyCanvasPainter {

    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);

    private Canvas canvas;
    private GraphicsContext gc;

    /**
     * Attaches a SkyCanvasPainter to a canvas
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
        List<Star> stars = sky.stars();
        double[] stereoPoints = new double[sky.starCoordinates().length];
        planeToCanvas.transform2DPoints(sky.starCoordinates(), 0, stereoPoints, 0, stars.size());
        drawAsterisms(sky, stereoPoints);
        for (int i = 0; i < stars.size(); i++) {
            drawCelestialObject(new Point2D(stereoPoints[2 * i],stereoPoints[2 * i + 1]), planeToCanvas,
                    BlackBodyColor.colorForTemperature(stars.get(i).colorTemperature()),
                    getMagnitudeBasedCelestialObjectDiameter(stars.get(i), projection, planeToCanvas));
        }
    }

    private void drawAsterisms(ObservedSky sky, double[] transformedPoints) {
        Bounds b = canvas.getBoundsInLocal();
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(1);
        for (Asterism asterism : sky.asterisms()) {
            gc.beginPath();
            boolean previousInBound = true;
            for (Integer i : sky.asterismIndex(asterism)) {
                Point2D point = new Point2D(transformedPoints[2 * i], transformedPoints[2 * i + 1]);
                //draws only a line if two consecutive stars not out of bound
                boolean thisInBound = b.contains(point);
                if (previousInBound || thisInBound) gc.lineTo(point.getX(), point.getY());
                else gc.moveTo(point.getX(), point.getY());
                previousInBound =thisInBound;
            }
            gc.stroke();
        }
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
        List<Planet> planets = sky.planets();
        double[] stereoPoints = new double[sky.planetCoordinates().length];
        planeToCanvas.transform2DPoints(sky.planetCoordinates(), 0, stereoPoints, 0, planets.size());
        for (int i = 0; i < planets.size(); i++) {
            drawCelestialObject(new Point2D(stereoPoints[2 * i],stereoPoints[2 * i + 1]), planeToCanvas,
                    Color.LIGHTGRAY,
                    getMagnitudeBasedCelestialObjectDiameter(planets.get(i), projection, planeToCanvas));
        }
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
        final double sunDiameter = deltaTransform(planeToCanvas, projection.applyToAngle(sky.sun().angularSize()));
        CartesianCoordinates sunPosition = sky.sunPosition();
        Point2D sunPositionOnCanvas = planeToCanvas.transform(sunPosition.x(), sunPosition.y());

        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.YELLOW.deriveColor(1, 1, 1, 0.25),  sunDiameter*2.2 );
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.YELLOW,   sunDiameter + 2 );
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.WHITE,  sunDiameter );
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
        final double moonDiameter = deltaTransform(planeToCanvas, projection.applyToAngle(sky.moon().angularSize()));
        Point2D moonCoordinates = new Point2D(sky.moonPosition().x(), sky.moonPosition().y());
        Point2D planePoint = planeToCanvas.transform(moonCoordinates);
        drawCelestialObject(planePoint, planeToCanvas, Color.WHITE,
                moonDiameter);
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
        double projRadius = Math.abs(projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0,0)));
        Point2D horizonCenter = planeToCanvas.transform(fromProjection.x() , fromProjection.y());
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


    private double getMagnitudeBasedCelestialObjectDiameter(CelestialObject celestialObject, StereographicProjection projection, Transform planeToCanvas){
        final double mag = MAGNITUDE_INTERVAL.clip(celestialObject.magnitude());
        final double factor = (99-17*mag) / (140);
        return deltaTransform(planeToCanvas, factor * projection.applyToAngle(Angle.ofDeg(0.5)));
    }

    private void drawCelestialObject(Point2D planeCoordinates, Transform planeToCanvas, Color color,
                                     double diameter) {
        gc.setFill(color);
        gc.fillOval(planeCoordinates.getX()-diameter/2, planeCoordinates.getY()-diameter/2, diameter, diameter);
    }

    private double deltaTransform(Transform planeToCanvas, double diameterToTransformInPlane){
        return planeToCanvas.deltaTransform(diameterToTransformInPlane, 0).getX();
    }
}
