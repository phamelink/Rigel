package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.security.SecurityPermission;
import java.util.function.UnaryOperator;

public class Main extends Application {
    public static void main(String[] args) { launch(args); }


    /**
     * The main entry point for all JavaFX applications.
     * The start method is called after the init method has returned,
     * and after the system is ready for the application to begin running.
     *
     * <p>
     * NOTE: This method is called on the JavaFX Application Thread.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which
     *                     the application scene can be set.
     *                     Applications may create other stages, if needed, but they will not be
     *                     primary stages.
     * @throws Exception if something goes wrong
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        BorderPane bp = new BorderPane();

        primaryStage.setTitle("Rigel");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);

        HBox primaryBox = controlBar();


        bp.setTop(primaryBox);

        primaryStage.setScene(new Scene(bp));
        primaryStage.show();
    }

    private HBox controlBar() {
        HBox controlBar = new HBox();
        Separator separator = new Separator(Orientation.VERTICAL);
        HBox obsPosBox = obsPosBox();




        controlBar.getChildren().addAll(obsPosBox, separator);
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    private HBox obsPosBox() {
        HBox observationPosBox = new HBox();

        Label lonLabel = new Label("Longitude (°) :");
        Label latLabel = new Label("Latitude (°) :");

        TextField textFieldLon = new TextField("6,57");
        textFieldLon.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> textFormatterLon = textFormatter("lon");
        textFieldLon.setTextFormatter(textFormatterLon);

        TextField textFieldLat = new TextField();
        textFieldLat.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> textFormatterLat = textFormatter("lat");
        textFieldLat.setTextFormatter(textFormatterLat);
        textFieldLat.setText("46,52");

        //To obtain value: textFormatterLat.getValue()

        observationPosBox.getChildren().addAll(lonLabel, textFieldLon, latLabel, textFieldLat);
        observationPosBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return observationPosBox;

    }

    private TextFormatter<Number> textFormatter(String lonOrLat) {
        NumberStringConverter stringConverter =
                new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> degFilter = (change -> {
            try {
                String newText =
                        change.getControlNewText();
                double newDeg =
                        stringConverter.fromString(newText).doubleValue();

                    return (GeographicCoordinates.isValidLonDeg(newDeg) && lonOrLat == "lon") ||
                            (GeographicCoordinates.isValidLatDeg(newDeg) && lonOrLat == "lat")
                                ? change
                                : null;
            } catch (Exception e) {
                return null;
            }
        });
        return new TextFormatter<>(stringConverter, 0, degFilter);
    }

    private HBox obsInstBox() {
        HBox obsIntBox = new HBox();

        Label dateLabel = new Label("Date :");

        obsIntBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return obsIntBox;
    }
}
