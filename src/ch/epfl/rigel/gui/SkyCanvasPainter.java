package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.Asterism;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.Star;
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

public class SkyCanvasPainter {
    private Canvas canvas;
    private GraphicsContext gc;

    private static final ClosedInterval MAGNITUDE_INTERVAL = ClosedInterval.of(-2, 5);
    private static final double HORIZON_DISTANC_STEPS = 10;

    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();



    }


    public void clear() {

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /*
    DRAW STARS
     */

    public void drawStars (ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        //Transform t = Transform.scale(1300, -1300);
        //t.createConcatenation(Transform.translate(canvas.getWidth()/2, canvas.getHeight()/2));

        drawAsterisms(sky, projection, planeToCanvas);
        int starCount = sky.starCoordinates().length /2;
        double[] transformedPoints = new double[starCount * 2];
        planeToCanvas.transform2DPoints(sky.starCoordinates(),0,transformedPoints,0, starCount );

        for (int i = 0; i < starCount; i++) {
            Star toDraw = sky.stars().get(i);
            double starDiameter = planeToCanvas.deltaTransform(getStarDiameter(toDraw), 0).getX();
            gc.setFill(BlackBodyColor.colorForTemperature(toDraw.colorTemperature()));
            double starRadius = starDiameter / 2;
            gc.fillOval(transformedPoints[2*i] - starRadius, transformedPoints[2*i + 1] - starRadius, starDiameter, starDiameter);
        }


    }

    private void drawAsterisms(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        double[] starCoord =sky.starCoordinates();
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

    private double getStarDiameter(Star star){

        final double mag = MAGNITUDE_INTERVAL.clip(star.magnitude());
        final double factor = (99-17*mag) / (140);
        return 2 * factor * Math.tan(Angle.ofDeg(0.5) / 4);
    }

    /*
    DRAW PLANETS
     */

    public void drawPlanets(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){

    }

    /*
    DRAW SUN
     */

    public void drawSun(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){

        final double sunDiameter = planeToCanvas.deltaTransform (effectiveSize(Angle.ofDeg(0.5)), 0).getX();
        CartesianCoordinates sunPosition = sky.sunPosition();
        Point2D sunPositionOnCanvas = planeToCanvas.transform(sunPosition.x(), sunPosition.y());
        gc.setFill(Color.YELLOW.deriveColor(1, 1, 1, 0.25));
        drawCenteredCircularBody(sunPositionOnCanvas, sunDiameter + 2.2);
        gc.setFill(Color.YELLOW);
        drawCenteredCircularBody(sunPositionOnCanvas, sunDiameter + 2);
        gc.setFill(Color.WHITE);
        drawCenteredCircularBody(sunPositionOnCanvas, sunDiameter);





    }

    /*
    DRAW MOON
     */

    public void drawMoon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){

    }

    /*
    DRAW HORIZON
     */

    public void drawHorizon(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas, HorizontalCoordinates projCenter){
        gc.setStroke(Color.RED);
        CartesianCoordinates fromProjection = projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(0,0));
        double projRadius = Math.abs(projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0,0)));
        Point2D horizonCenter = planeToCanvas.transform(fromProjection.x() , fromProjection.y());
        double radius = planeToCanvas.deltaTransform(projRadius, 0).getX();
        System.out.println(radius);
        System.out.println(horizonCenter);
        gc.strokeOval(horizonCenter.getX() - radius, horizonCenter.getY() - radius ,radius * 2, radius * 2);

        gc.setFill(Color.RED);
        for (HorizontalCoordinates.OCTANT octant: HorizontalCoordinates.OCTANT.values()) {
            CartesianCoordinates octantCartesian = projection.apply(HorizontalCoordinates.ofDeg(octant.getOctantAngle(),-0.5));
           Point2D octantCoord = planeToCanvas.transform(octantCartesian.x(), octantCartesian.y());
           gc.fillText(octant.name(), octantCoord.getX(), octantCoord.getY());

        }

    }

    public void drawAll(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas, HorizontalCoordinates projCenter){
        drawStars(sky, projection, planeToCanvas);
        drawPlanets(sky, projection, planeToCanvas);
        drawSun(sky, projection, planeToCanvas);
        drawMoon(sky, projection, planeToCanvas);
        drawHorizon(sky, projection, planeToCanvas, projCenter);
    }

    /*
    PRIVATE UTILITY CLASSES
     */

    private double effectiveSize(double angularSize){
        return 2*Math.atan(angularSize/4);
    }

    private void drawCenteredCircularBody(Point2D center, double diameter){
        gc.fillOval(center.getX()-diameter/2, center.getY()-diameter/2, diameter, diameter);
    }


}
