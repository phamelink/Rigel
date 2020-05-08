package ch.epfl.rigel.gui;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecurityPermission;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    public void start(Stage primaryStage) throws IOException {
        BorderPane bp = new BorderPane();

        primaryStage.setTitle("Rigel");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);

        HBox primaryBox = controlBar();


        bp.setTop(primaryBox);

        primaryStage.setScene(new Scene(bp));
        primaryStage.show();
    }

    private HBox controlBar() throws IOException {
        HBox controlBar = new HBox();
        Separator separator = new Separator(Orientation.VERTICAL);
        HBox obsPosBox = obsPosBox();
        HBox obsInstBox = obsInstBox();
        HBox accBox = accBox();




        controlBar.getChildren().addAll(obsPosBox, new Separator(Orientation.VERTICAL), obsInstBox, new Separator(Orientation.VERTICAL), accBox);
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    private HBox obsPosBox() {
        HBox observationPosBox = new HBox();

        Label lonLabel = new Label("Longitude (°) :");
        Label latLabel = new Label("Latitude (°) :");

        TextField lonTextField = new TextField();
        lonTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> lonTextFormatter = textFormatter("lon");
        lonTextField.setTextFormatter(lonTextFormatter);
        lonTextField.setText("6,57");

        TextField latTextField = new TextField();
        latTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> latTextFormatter = textFormatter("lat");
        latTextField.setTextFormatter(latTextFormatter);
        latTextField.setText("46,52");

        //To obtain value: latTextFormatter.getValue()

        observationPosBox.getChildren().addAll(lonLabel, lonTextField, latLabel, latTextField);
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

        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setStyle("-fx-pref-width: 120;");

        Label timeLabel = new Label("Heure :");

        TextField timeTextField = new TextField();
        timeTextField.setStyle("-fx-pref-width: 75; -fx-alignment: baseline-right;");

        //time formatter
        DateTimeFormatter hmsFormatter =
                DateTimeFormatter.ofPattern("HH:mm:ss");
        LocalTimeStringConverter stringConverter =
                new LocalTimeStringConverter(hmsFormatter, hmsFormatter);
        TextFormatter<LocalTime> timeFormatter =
                new TextFormatter<>(stringConverter);

        timeTextField.setTextFormatter(timeFormatter);
        //to get local time update: timeFormatter.getValue()

        ComboBox<ZoneId> zoneIdComboBox = new ComboBox<>();

        zoneIdComboBox.setItems(allSortedZoneIds());
        zoneIdComboBox.setId(ZoneId.systemDefault().getId());
        zoneIdComboBox.setStyle("-fx-pref-width: 180;");

        obsIntBox.getChildren().addAll(dateLabel, datePicker, timeLabel, timeTextField, zoneIdComboBox);
        obsIntBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return obsIntBox;
    }

    private ObservableList<ZoneId> allSortedZoneIds() {
        ObservableList<ZoneId> zoneIds = FXCollections.observableArrayList();
        List<String> zoneIdsString = new ArrayList<>(ZoneId.getAvailableZoneIds());
        zoneIdsString.sort(String::compareTo);
        for (String s : zoneIdsString) {
            zoneIds.add(ZoneId.of(s));
        }
        return zoneIds;
    }

    private HBox accBox() throws IOException {
        HBox accBox = new HBox();

        ChoiceBox<NamedTimeAccelerator> acceleratorChoiceBox = new ChoiceBox<>();
        ObservableList<NamedTimeAccelerator> accObsList = FXCollections.observableArrayList(NamedTimeAccelerator.values());
        acceleratorChoiceBox.setItems(accObsList);

        String resetFont = "\uf0e2";
        String playFont = "\uf04b";
        String pauseFont = "\uf04c";

        Button resetButton = new Button(resetFont);
        Button pausePlayButton = new Button(playFont);

        try (InputStream fontStream = getClass()
                .getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")) {
            Font fontAwesome = Font.loadFont(fontStream, 15);


            resetButton.setFont(fontAwesome);
            pausePlayButton.setFont(fontAwesome);

            fontStream.close();
        } catch  (IOException e) {
            resetButton.setFont(Font.getDefault());
            pausePlayButton.setFont(Font.getDefault());
        }


        accBox.getChildren().addAll(acceleratorChoiceBox, resetButton, pausePlayButton);
        accBox.setStyle("-fx-spacing: inherit;");
        return accBox;

    }
}
