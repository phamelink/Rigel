package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.CartesianCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.RightOpenInterval;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.*;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Font;
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
    public static final int DIAMETER_EXTENSION_FOR_INDICATOR = 20;

    private final Canvas canvas;
    private final GraphicsContext gc;
    private final BooleanProperty starsEnabled;
    private final BooleanProperty asterismsEnabled;
    private final BooleanProperty sunEnabled;
    private final BooleanProperty moonEnabled;
    private final BooleanProperty planetsEnabled;
    private final BooleanProperty realisticSkyEnabled;
    private final BooleanProperty realisticSunEnabled;
    private final BooleanProperty altitudeLinesEnabled;
    private final BooleanProperty indicatorIsOn;
    private final StringProperty indicatedObjectName;
    private double dayLightFactor;
    private double skyBrightnessFactor;


    /**
     * Attaches a SkyCanvasPainter to a canvas
     * @param canvas canvas on which the sky will be painted
     */
    public SkyCanvasPainter(Canvas canvas) {
        this.canvas = canvas;
        gc = canvas.getGraphicsContext2D();
        starsEnabled = new SimpleBooleanProperty(true);
        asterismsEnabled = new SimpleBooleanProperty(true);
        sunEnabled = new SimpleBooleanProperty(true);
        moonEnabled = new SimpleBooleanProperty(true);
        planetsEnabled = new SimpleBooleanProperty(true);
        realisticSkyEnabled = new SimpleBooleanProperty(false);
        realisticSunEnabled = new SimpleBooleanProperty(false);
        altitudeLinesEnabled = new SimpleBooleanProperty(false);
        indicatorIsOn = new SimpleBooleanProperty(false);
        indicatedObjectName = new SimpleStringProperty();
    }

    /**
     * clears the canvas to a black canvas
     */
    public void clear() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * draws all stars and asterisms
     * @param sky observed sky
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void drawStars (ObservedSky sky, StereographicProjection projection, Transform planeToCanvas) {
        List<Star> stars = sky.stars();
        Bounds safeDisplayBounds = new BoundingBox(-200,-200, canvas.getWidth()+400, canvas.getHeight()+400);
        double[] stereoPoints = new double[sky.starCoordinates().length];
        planeToCanvas.transform2DPoints(sky.starCoordinates(), 0, stereoPoints, 0, stars.size());

        if(asterismsEnabled.get()) drawAsterisms(sky, stereoPoints);

        for (int i = 0; i < stars.size(); i++) {
            Point2D p = new Point2D(stereoPoints[2 * i], stereoPoints[2 * i + 1]);
            double diameter = getMagnitudeBasedCelestialObjectDiameter(stars.get(i), projection, planeToCanvas);
            if(!safeDisplayBounds.contains(p)) continue;
            Color starColor = BlackBodyColor.colorForTemperature(stars.get(i).colorTemperature());

            if(realisticSkyEnabled.get()) {

                /*
                BONUS
                 */

                //Create a radial gradient halo from star info
                double haloDiameter = diameter * 5;
                RadialGradient starGradient = new RadialGradient(
                        0,
                        0,
                        p.getX(),
                        p.getY(),
                        haloDiameter,
                        false,
                        CycleMethod.NO_CYCLE,
                        new Stop(0, starColor.deriveColor(1, 1.5, 1, 1)),
                        new Stop(0.2, starColor.deriveColor(1, 1.1, 0.1, 0.9)),
                        new Stop(0.4, starColor.deriveColor(1, 0, 0, 0))
                );
                gc.setFill(starGradient);
                gc.fillOval(p.getX()-haloDiameter/2, p.getY()-haloDiameter/2, haloDiameter, haloDiameter);
            }else {
                //Default rendering option
                drawCelestialObject(new Point2D(stereoPoints[2 * i],stereoPoints[2 * i + 1]), planeToCanvas, BlackBodyColor.colorForTemperature(stars.get(i).colorTemperature()), getMagnitudeBasedCelestialObjectDiameter(stars.get(i), projection, planeToCanvas));
            }
            if (indicatedObjectNameProperty().get().equals(stars.get(i).name()) && indicatorIsOnProperty().get())
                drawIndicator(p, diameter);
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
                previousInBound = thisInBound;
            }
            gc.stroke();
        }
    }

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
            Point2D coord = new Point2D(stereoPoints[2 * i],stereoPoints[2 * i + 1]);
            double diameter = getMagnitudeBasedCelestialObjectDiameter(planets.get(i), projection, planeToCanvas);
            drawCelestialObject(coord, planeToCanvas, Color.LIGHTGRAY, diameter);
            if (indicatorIsOn.get() && indicatedObjectName.get().equals(planets.get(i).name())) {
                drawIndicator(coord, diameter);
            }
        }
    }

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
        double haloDiameter = sunDiameter * 3000;
        Color inside = BlackBodyColor.colorForTemperature(dayLightFactor > 0.05 ?  (int) (20000 * dayLightFactor) : (int) (20000 * 0.05));
        Color outside = BlackBodyColor.colorForTemperature(dayLightFactor > 0.05 ?  (int) (40000 * dayLightFactor) : (int) (40000 * 0.05));

        /*
        BONUS
         */

        if(isRealisticSunEnabled()){
            //Sun light halo that depends on the time of day
            RadialGradient sunGradient = new RadialGradient(
                    0,
                    0,
                    sunPositionOnCanvas.getX(),
                    sunPositionOnCanvas.getY(),
                    haloDiameter,
                    false,
                    CycleMethod.NO_CYCLE,
                    new Stop(0, inside.deriveColor(1,0.7,5 * skyBrightnessFactor, skyBrightnessFactor)),
                    new Stop( 0.03, outside.deriveColor(1,0.5,1,skyBrightnessFactor*0.01)),
                    new Stop(1, Color.DEEPSKYBLUE.deriveColor(1, 0, 0, 0))
            );
            gc.setFill(sunGradient);
            gc.fillOval(sunPositionOnCanvas.getX()-haloDiameter/2, sunPositionOnCanvas.getY()-haloDiameter/2, haloDiameter, haloDiameter);
        }

        //Default rendering options with some extra color depending on time of day
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,outside.deriveColor(1,1,1,0.25),  sunDiameter*2.2 );
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.YELLOW,   sunDiameter + 2 );
        drawCelestialObject(sunPositionOnCanvas, planeToCanvas,Color.WHITE,  sunDiameter );
        if (indicatorIsOn.get() && indicatedObjectName.get().equals(sky.sun().name())) {
            drawIndicator(sunPositionOnCanvas, sunDiameter);
        }
    }

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
        if (indicatorIsOn.get() && indicatedObjectName.get().equals(sky.moon().name())) {
            drawIndicator(planePoint, moonDiameter);
        }
    }

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
            gc.fillText(octant.name(), octantCoord.getX(), octantCoord.getY() + Font.getDefault().getSize());
        }

        /*
        BONUS
         */

        if(altitudeLinesEnabled.get()) {
            //Draw guides for altitude
            double xAltPos = canvas.getWidth() / 2;
            gc.setStroke(Color.PALEGREEN.deriveColor(1, 1, 1, 0.5));
            gc.setFill(Color.PALEGREEN.deriveColor(1, 2, 1, 0.8));
            gc.setLineWidth(1);
            gc.fillText("0°", xAltPos, planeToCanvas.transform(0, projection.apply(HorizontalCoordinates.of(projection.getCenter().az(), Angle.ofDeg(0.5))).y()).getY());
            for (int alt = -50; alt < 90; alt = alt + 10) {
                if (alt == 0) continue; //Horizon is a special case
                gc.fillText(alt + "°", xAltPos, planeToCanvas.transform(0, projection.apply(HorizontalCoordinates.of(projection.getCenter().az(), Angle.ofDeg(alt + 0.5))).y()).getY());
                CartesianCoordinates circCenter = projection.circleCenterForParallel(HorizontalCoordinates.ofDeg(0, alt));
                double circRadius = Math.abs(projection.circleRadiusForParallel(HorizontalCoordinates.ofDeg(0, alt)));
                Point2D altCenter = planeToCanvas.transform(circCenter.x(), circCenter.y());
                double altRadius = planeToCanvas.deltaTransform(circRadius, 0).getX();
                gc.strokeOval(altCenter.getX() - altRadius, altCenter.getY() - altRadius, altRadius * 2, altRadius * 2);
            }
        }

    }

    private void drawIndicator(Point2D planePoint, double diameter) {
        gc.setStroke(Color.LIGHTGREEN);
        gc.strokeOval(planePoint.getX()-(diameter+ DIAMETER_EXTENSION_FOR_INDICATOR)/2, planePoint.getY()-(diameter+DIAMETER_EXTENSION_FOR_INDICATOR)/2, diameter+DIAMETER_EXTENSION_FOR_INDICATOR, diameter+DIAMETER_EXTENSION_FOR_INDICATOR);
    }

    /**
     * draws all enabled celestial objects in sky and the horizon
     * @param sky observed sky
     * @param projection stereographic projection used
     * @param planeToCanvas Transform used
     */
    public void draw(ObservedSky sky, StereographicProjection projection, Transform planeToCanvas){

        /*
        BONUS
         */

        //Calculate time of day factors depending on the altitude of the sun in the sky
        dayLightFactor = Math.max(Math.PI / 4  * projection.inverseApply(sky.sunPosition()).alt(), 0);
        if (projection.inverseApply(sky.sunPosition()).alt() > 0.1) skyBrightnessFactor = 1;
        else if (projection.inverseApply(sky.sunPosition()).alt() <= 0.1 && projection.inverseApply(sky.sunPosition()).alt() >= -0.3) skyBrightnessFactor = (projection.inverseApply(sky.sunPosition()).alt() + 0.3) * 2.5;
        else skyBrightnessFactor = 0;

        clear();
        //Only draw enabled objects
        if(starsEnabled.get()) drawStars(sky, projection, planeToCanvas);
        if(planetsEnabled.get()) drawPlanets(sky, projection, planeToCanvas);

        if (isRealisticSunEnabled()) {
            //Simulate sky color (over stars and planets, but with a variable opacity)
            gc.setFill(BlackBodyColor.colorForTemperature(dayLightFactor > 0.03 ?  (int) (40000 * dayLightFactor) : (int) (40000 * 0.03)).deriveColor(1, 1.1, skyBrightnessFactor, skyBrightnessFactor));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }

        if(sunEnabled.get()) drawSun(sky, projection, planeToCanvas);
        if(moonEnabled.get()) drawMoon(sky, projection, planeToCanvas);
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

    /*
    Getters/setters for properties
     */

    /**
     * Checks if stars are enabled
     * @return true if stars are enabled
     */
    public boolean isStarsEnabled() {
        return starsEnabled.get();
    }

    /**
     * Returns property concerning stars enabled
     * @return stars enabled property
     */
    public BooleanProperty starsEnabledProperty() {
        return starsEnabled;
    }

    /**
     * Set stars enabled
     * @param starsEnabled new value
     */
    public void setStarsEnabled(boolean starsEnabled) {
        this.starsEnabled.set(starsEnabled);
    }

    /**
     * Checks if asterisms are enabled
     * @return true if asterisms are enabled
     */
    public boolean isAsterismsEnabled() {
        return asterismsEnabled.get();
    }

    /**
     * Returns property concerning asterisms enabled
     * @return asterisms enabled property
     */
    public BooleanProperty asterismsEnabledProperty() {
        return asterismsEnabled;
    }

    /**
     * Set asterisms enabled
     * @param asterismsEnabled new value
     */
    public void setAsterismsEnabled(boolean asterismsEnabled) {
        this.asterismsEnabled.set(asterismsEnabled);
    }

    /**
     * Checks if sun is enabled
     * @return true if sun is enabled
     */
    public boolean isSunEnabled() {
        return sunEnabled.get();
    }

    /**
     * Returns property concerning sun enabled
     * @return sun enabled property
     */
    public BooleanProperty sunEnabledProperty() {
        return sunEnabled;
    }

    /**
     * Set sun enabled
     * @param sunEnabled new value
     */
    public void setSunEnabled(boolean sunEnabled) {
        this.sunEnabled.set(sunEnabled);
    }

    /**
     * Checks if moon is enabled
     * @return true if moon is enabled
     */
    public boolean isMoonEnabled() {
        return moonEnabled.get();
    }

    /**
     * Returns property concerning moon enabled
     * @return moon enabled property
     */
    public BooleanProperty moonEnabledProperty() {
        return moonEnabled;
    }

    /**
     * Set moon enabled
     * @param moonEnabled new value
     */
    public void setMoonEnabled(boolean moonEnabled) {
        this.moonEnabled.set(moonEnabled);
    }

    /**
     * Checks if planets are enabled
     * @return true if planets are enabled
     */
    public boolean isPlanetsEnabled() {
        return planetsEnabled.get();
    }

    /**
     * Returns property concerning planets enabled
     * @return planets enabled property
     */
    public BooleanProperty planetsEnabledProperty() {
        return planetsEnabled;
    }

    /**
     * Set planets enabled
     * @param planetsEnabled new value
     */
    public void setPlanetsEnabled(boolean planetsEnabled) {
        this.planetsEnabled.set(planetsEnabled);
    }

    /**
     * Checks if realistic sky is enabled
     * @return true if realistic sky is enabled
     */
    public boolean isRealisticSkyEnabled() {
        return realisticSkyEnabled.get();
    }

    /**
     * Returns property concerning realistic sky
     * @return realistic sky enabled property
     */
    public BooleanProperty realisticSkyEnabledProperty() {
        return realisticSkyEnabled;
    }

    /**
     * Checks if realisitc sun is enabled
     * @return true if realistic sun is enabled
     */
    public boolean isRealisticSunEnabled() {
        return realisticSunEnabled.get();
    }

    /**
     * Returns property concerning realistic sun
     * @return realistic sun enabled property
     */
    public BooleanProperty realisticSunEnabledProperty() {
        return realisticSunEnabled;
    }

    /**
     * Checks if alt lines are enabled
     * @return true if alt lines are enabled
     */
    public boolean isAltitudeLinesEnabled() {
        return altitudeLinesEnabled.get();
    }

    /**
     * Returns property concerning alt lines
     * @return alt lines enabled property
     */
    public BooleanProperty altitudeLinesEnabledProperty() {
        return altitudeLinesEnabled;
    }

    /**
     * Returns all rendering properties
     * @return list of boolean rendering properties
     */
    public List<Property<Boolean>> getRenderingProperties(){
        return List.of(starsEnabled, asterismsEnabled, sunEnabled, moonEnabled, planetsEnabled,
                realisticSkyEnabled, realisticSunEnabled, altitudeLinesEnabled, indicatorIsOn);
    }

    /**
     * Getter for indicatorIsOn boolean value
     * @return indicatorIsOn boolean value
     */
    public boolean getIndicatorIsOn() { return indicatorIsOn.get(); }

    /**
     * Returns indicatorIsOn boolean property
     * @return indicatorIsOn boolean property
     */
    public BooleanProperty indicatorIsOnProperty() { return indicatorIsOn; }

    /**
     * Getter for indicatedObjectName string
     * @return indicatedObjectName string
     */
    public String getIndicatedObjectName() { return indicatedObjectName.get(); }

    /**
     * Returns indicationObjectName property
     * @return indicationObjectName property
     */
    public StringProperty indicatedObjectNameProperty() { return indicatedObjectName; }

}
