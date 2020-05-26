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
        List<Star> stars = sky.stars();
        double[] stereoPoints = new double[sky.starCoordinates().length];
        planeToCanvas.transform2DPoints(sky.starCoordinates(), 0, stereoPoints, 0, stereoPoints.length);
        for (int i = 0; i < stars.size(); i++) {
            drawCelestialObject(new Point2D(stereoPoints[2 * i],stereoPoints[2 * i + 1]), planeToCanvas,
                    BlackBodyColor.colorForTemperature(stars.get(i).colorTemperature()),
                    getMagnitudeBasedCelestialObjectDiameter(stars.get(i), projection));
        }

    }

    private void drawAsterisms(ObservedSky sky, Transform planeToCanvas) {
        double[] starCoord = sky.starCoordinates();
        for (Asterism asterism : sky.asterisms()) {
            Bounds b = canvas.getBoundsInLocal();
            gc.setStroke(Color.BLUE);
            gc.setLineWidth(1);
            gc.beginPath();
            boolean previousInBound = true;
            CartesianCoordinates coord;
            for (Integer i : sky.asterismIndex(asterism)) {
                coord = CartesianCoordinates.of(starCoord[2 * i], starCoord[2 * i + 1]);
                Point2D point = planeToCanvas.transform(coord.x(), coord.y());
                double x = point.getX();
                double y = point.getY();
                //draws only a line if two consecutive stars not out of bound
                if (previousInBound || b.contains(x, y)) gc.lineTo(x, y);
                else gc.moveTo(x, y);
                previousInBound = b.contains(x, y);
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
        planeToCanvas.transform2DPoints(sky.planetCoordinates(), 0, stereoPoints, 0, stereoPoints.length);
        for (int i = 0; i < planets.size(); i++) {
            drawCelestialObject(new Point2D(stereoPoints[2 * i],stereoPoints[2 * i + 1]), planeToCanvas,
                    Color.LIGHTGRAY,
                    getMagnitudeBasedCelestialObjectDiameter(planets.get(i), projection));
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
        final double sunDiameter = projection.applyToAngle(Angle.ofDeg(0.5));
        CartesianCoordinates sunPosition = sky.sunPosition();
        Point2D sunPositionOnCanvas = planeToCanvas.transform(sunPosition.x(), sunPosition.y());

<<<<<<< HEAD
        gc.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.25));
        drawCenteredCircularBody(sunPositionOnCanvas, sunDiameter*2.2);

        gc.setFill(Color.YELLOW);
        drawCenteredCircularBody(sunPositionOnCanvas, sunDiameter + 2);

        gc.setFill(Color.WHITE);
        drawCenteredCircularBody(sunPositionOnCanvas, sunDiameter);
=======
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.YELLOW.deriveColor(1, 1, 1, 0.25),  sunDiameter*2.2 );
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.YELLOW,  sunDiameter + 2 );
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.WHITE,  sunDiameter );

>>>>>>> 0d882189d68ab7779302230deb52512d780959e6
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
        Moon moon = sky.moon();
        planeToCanvas
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
        System.out.println("painting");
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

<<<<<<< HEAD
    private void drawCenteredCircularBody(Point2D center, double diameter){
        gc.fillOval(center.getX()-diameter/2, center.getY()-diameter/2, diameter, diameter);
    }

    private double getCelestialObjectDiameter(CelestialObject celestialObject, StereographicProjection projection){
=======
    private double getMagnitudeBasedCelestialObjectDiameter(CelestialObject celestialObject, StereographicProjection projection){
>>>>>>> 0d882189d68ab7779302230deb52512d780959e6
        final double mag = MAGNITUDE_INTERVAL.clip(celestialObject.magnitude());
        final double factor = (99-17*mag) / (140);
        return factor * projection.applyToAngle(Angle.ofDeg(0.5));
    }

    //TODO color should be set by parent class before method call
<<<<<<< HEAD
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
            drawCenteredCircularBody(position, diameter); //TODO rewrite this method here internally, never used elsewhere
=======
    private void drawCelestialObject(Point2D planeCoordinates, Transform planeToCanvas, Color color,
                                     double diameterToTransformInPlane) {
        double diameter = planeToCanvas.deltaTransform(diameterToTransformInPlane, 0).getX();
        gc.setFill(color);
        gc.fillOval(planeCoordinates.getX()-diameter/2, planeCoordinates.getY()-diameter/2, diameter, diameter);
>>>>>>> 0d882189d68ab7779302230deb52512d780959e6
        }
    }


}
