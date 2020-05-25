package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.ObservedSky;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.coordinates.StereographicProjection;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;

public final class DrawSky extends Application {

    private static final double REFRESH_RATE =50;
    private static final double CONTROL_FACTOR = 0.0015;

    public static void main(String[] args) { launch(args); }

    private InputStream resourceStream(String resourceName) {
        return getClass().getResourceAsStream(resourceName);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        try (InputStream hs = resourceStream("/hygdata_v3.csv");
        InputStream ast = resourceStream("/asterisms.txt")){
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE).loadFrom(ast, AsterismLoader.INSTANCE)
                    .build();

            ZonedDateTime when =
                    ZonedDateTime.parse("2020-02-17T12:15:00+01:00");
            final GeographicCoordinates[] where = {GeographicCoordinates.ofDeg(6.57, 46.52)};
            final HorizontalCoordinates[] projCenter = {HorizontalCoordinates.ofDeg(90, 10)};



            Canvas canvas =
                    new Canvas(1200, 900);
            Transform planeToCanvas =
                    Transform.affine(1300, 0, 0, -1300, 600, 450);
            SkyCanvasPainter painter =
                    new SkyCanvasPainter(canvas);

            final long[] time = {0};
            final Point2D[] lastPoint = {Point2D.ZERO};
            Timeline refresh = new Timeline(new KeyFrame(Duration.millis(REFRESH_RATE), actionEvent -> {
                StereographicProjection projection =
                        new StereographicProjection(projCenter[0]);
                ZonedDateTime current = when.plusMinutes((time[0]));
                ObservedSky sky =
                        new ObservedSky(current, where[0], projection, catalogue);

                painter.clear();
                painter.drawAll(sky, projection, planeToCanvas);
                ++time[0];
            }));



            refresh.setCycleCount(Animation.INDEFINITE); // loop forever
            refresh.play();

            primaryStage.setScene(new Scene(new BorderPane(canvas)));
            primaryStage.show();



            primaryStage.addEventFilter(MouseEvent.MOUSE_DRAGGED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {

                        Point2D nextPoint = new Point2D(mouseEvent.getX(), mouseEvent.getY());
                        projCenter[0] = projCenter[0].delta(-(nextPoint.getX()-lastPoint[0].getX())*CONTROL_FACTOR, (nextPoint.getY()-lastPoint[0].getY())*CONTROL_FACTOR);
                        lastPoint[0] = nextPoint;


                }
            });
            primaryStage.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    lastPoint[0] = new Point2D(mouseEvent.getX(), mouseEvent.getY());
                }
            });

            WritableImage fxImage =
                    canvas.snapshot(null, null);
            BufferedImage swingImage =
                    SwingFXUtils.fromFXImage(fxImage, null);
            ImageIO.write(swingImage, "png", new File("sky.png"));
        }

    }



}