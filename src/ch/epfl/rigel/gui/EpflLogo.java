package ch.epfl.rigel.gui;


import javafx.application.Application;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Shear;
import javafx.stage.Stage;
import javafx.scene.transform.Transform;

import java.awt.*;


public class EpflLogo extends Application {
    public static void main(String[] args) { launch(args); }

    @Override
    public void start(Stage primaryStage) {
        Canvas canvas = new Canvas(800, 300);
        GraphicsContext ctx = canvas.getGraphicsContext2D();

        // Fond blanc
        ctx.setFill(Color.YELLOW);
        ctx.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

       /* // Texte EPFL rouge
        ctx.setFont(Font.font("Helvetica", 300));
        ctx.setFill(Color.RED);
        ctx.setTextAlign(TextAlignment.CENTER);
        ctx.setTextBaseline(VPos.BASELINE);
        ctx.fillText("EPFL", 400, 250);

        // Trous dans le E et le F
        ctx.setFill(Color.BLACK);
        ctx.fillRect(50, 126, 30, 26);
        ctx.fillRect(450, 126, 30, 26);*/

        ctx.setStroke(Color.GREEN);
        ctx.setFill(Color.RED);
        ctx.beginPath();
        ctx.lineTo(50,50);
        ctx.lineTo(100,150);
        ctx.stroke();

        primaryStage.setScene(new Scene(new BorderPane(canvas)));
        primaryStage.show();
    }
}