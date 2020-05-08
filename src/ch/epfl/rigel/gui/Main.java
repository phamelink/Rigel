package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public class Main extends Application {
    private static final double DEFAULT_OBSERVATION_AZIMUTH = 180.000000000001;
    private static final double DEFAULT_OBSERVATION_ALTITUDE = 15.0;
    private static final double DEFAULT_FIELD_OF_VIEW = 100.0;
    private static final double DEFAULT_LONGITUDE = 6.57;
    private static final double DEFAULT_LATITUDE = 46.52;
    private Font AWESOME_FONT;
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
        try (InputStream hs = getClass().getResourceAsStream(("/hygdata_v3.csv"))) {
            StarCatalogue catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .build();

            DateTimeBean dateTimeBean = new DateTimeBean();
            dateTimeBean.setZonedDateTime(ZonedDateTime.now());

            ObserverLocationBean observerLocationBean =
                    new ObserverLocationBean();
            observerLocationBean.setCoordinates(
                    GeographicCoordinates.ofDeg(DEFAULT_LONGITUDE, DEFAULT_LATITUDE));



            ViewingParametersBean viewingParametersBean =
                    new ViewingParametersBean();
            viewingParametersBean.setCenter(
                    HorizontalCoordinates.ofDeg(DEFAULT_OBSERVATION_AZIMUTH, DEFAULT_OBSERVATION_ALTITUDE));
            viewingParametersBean.setFieldOfViewDeg(DEFAULT_FIELD_OF_VIEW);

            SkyCanvasManager canvasManager = new SkyCanvasManager(
                    catalogue,
                    dateTimeBean,
                    observerLocationBean,
                    viewingParametersBean);

            Canvas sky = canvasManager.canvas();
            BorderPane root = new BorderPane();

            sky.widthProperty().bind(root.widthProperty());
            sky.heightProperty().bind(root.heightProperty());

            HBox primaryBox = controlBar(dateTimeBean, observerLocationBean, canvasManager.getTimeAnimator());
            Pane skyPane = new Pane(sky);
            HBox box = new HBox();
            Button test = new Button("random coord");
            test.setOnAction((e) -> {
                observerLocationBean.setLonDeg(Math.random()*90);
                observerLocationBean.setLatDeg(Math.random()*90);
            });
            box.getChildren().addAll(test);


            root.setTop(primaryBox);
            root.setCenter(skyPane);
            root.setBottom(box);

            primaryStage.setTitle("Rigel");
            primaryStage.setMinHeight(600);
            primaryStage.setMinWidth(800);
            primaryStage.setScene(new Scene(root));
            primaryStage.show();

            sky.requestFocus();

        }

    }

    private HBox controlBar(DateTimeBean dateTimeBean, ObserverLocationBean observerLocationBean, TimeAnimator timeAnimator) throws IOException {
        HBox controlBar = new HBox();
        HBox obsPosBox = obsPosBox(observerLocationBean);
        HBox obsInstBox = obsInstBox(dateTimeBean, timeAnimator);
        HBox accBox = accBox(dateTimeBean, timeAnimator);

        controlBar.getChildren().addAll(obsPosBox, new Separator(Orientation.VERTICAL), obsInstBox, new Separator(Orientation.VERTICAL), accBox);
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    private HBox obsPosBox(ObserverLocationBean observerLocationBean) {
        HBox observationPosBox = new HBox();

        Label lonLabel = new Label("Longitude (°) :");
        Label latLabel = new Label("Latitude (°) :");

        TextField lonTextField = new TextField();
        lonTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> lonTextFormatter = textFormatter("lon");
        lonTextField.setTextFormatter(lonTextFormatter);
        lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());


        TextField latTextField = new TextField();
        latTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> latTextFormatter = textFormatter("lat");
        latTextField.setTextFormatter(latTextFormatter);
        latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());


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

    private HBox obsInstBox(DateTimeBean dateTimeBean, TimeAnimator timeAnimator) {
        HBox obsIntBox = new HBox();

        Label dateLabel = new Label("Date :");

        DatePicker datePicker = new DatePicker();
        datePicker.setStyle("-fx-pref-width: 120;");
        datePicker.valueProperty().bindBidirectional(dateTimeBean.dateProperty());

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
        timeFormatter.valueProperty().bindBidirectional(dateTimeBean.timeProperty());


        ComboBox<ZoneId> zoneIdComboBox = new ComboBox<>();

        zoneIdComboBox.setItems(allSortedZoneIds());
        zoneIdComboBox.setValue(ZoneId.systemDefault());
        zoneIdComboBox.setStyle("-fx-pref-width: 180;");
        zoneIdComboBox.valueProperty().bindBidirectional(dateTimeBean.zoneIdProperty());

        obsIntBox.getChildren().addAll(dateLabel, datePicker, timeLabel, timeTextField, zoneIdComboBox);
        obsIntBox.disableProperty().bind(timeAnimator.getRunningProperty());
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

    private HBox accBox(DateTimeBean dateTimeBean, TimeAnimator timeAnimator) throws IOException {
        Font fontAwesome;

        try (InputStream fontStream = getClass()
                .getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")) {
            fontAwesome = Font.loadFont(fontStream, 15);
        } catch  (IOException e) {
            fontAwesome = Font.getDefault();
        }

        HBox accBox = new HBox();

        ChoiceBox<NamedTimeAccelerator> acceleratorChoiceBox = new ChoiceBox<>();
        ObservableList<NamedTimeAccelerator> accObsList = FXCollections.observableArrayList(NamedTimeAccelerator.values());
        acceleratorChoiceBox.setItems(accObsList);
        acceleratorChoiceBox.setValue(NamedTimeAccelerator.TIMES_300);
        acceleratorChoiceBox.setOnAction(event -> {
            timeAnimator.setAccelerator(acceleratorChoiceBox.getValue().getAccelerator());
        });

        String resetFont = "\uf0e2";
        String playFont = "\uf04b";
        String pauseFont = "\uf04c";

        Button resetButton = new Button(resetFont);
        Button pausePlayButton = new Button(playFont);

        BooleanProperty isPlaying = new SimpleBooleanProperty(false);
        isPlaying.bind(timeAnimator.getRunningProperty());
        timeAnimator.setAccelerator(NamedTimeAccelerator.TIMES_300.getAccelerator());
        pausePlayButton.setOnAction(event -> {
            if (isPlaying.get()) {
                timeAnimator.stop();
                pausePlayButton.setText(playFont);
            } else {
                timeAnimator.start();
                pausePlayButton.setText(pauseFont);
            }
        });

        acceleratorChoiceBox.disableProperty().bind(isPlaying);


        resetButton.setOnAction(event -> {
            if (!isPlaying.get())
                dateTimeBean.setZonedDateTime(ZonedDateTime.now());
        });
        resetButton.setFont(fontAwesome);
        pausePlayButton.setFont(fontAwesome);



        accBox.getChildren().addAll(acceleratorChoiceBox, resetButton, pausePlayButton);
        accBox.setStyle("-fx-spacing: inherit;");
        return accBox;

    }
}
