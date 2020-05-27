package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.AsterismLoader;
import ch.epfl.rigel.astronomy.HygDatabaseLoader;
import ch.epfl.rigel.astronomy.StarCatalogue;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static javafx.beans.binding.Bindings.format;
import static javafx.beans.binding.Bindings.when;

public class Main extends Application {
    private static final NamedTimeAccelerator DEFAULT_ACCELERATOR = NamedTimeAccelerator.TIMES_300;
    private static final double DEFAULT_OBSERVATION_AZIMUTH = 180.000000000001;
    private static final double DEFAULT_OBSERVATION_ALTITUDE = 15.0;
    private static final double DEFAULT_FIELD_OF_VIEW = 100.0;
    private static final double DEFAULT_LONGITUDE = 6.57;
    private static final double DEFAULT_LATITUDE = 46.52;
    private static String RESET_CHAR = "\uf0e2";
    private static String PLAY_CHAR = "\uf04b";
    private static String PAUSE_CHAR = "\uf04c";

    private static Font fontAwesome;

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
     */
    @Override
    public void start(Stage primaryStage) {

        //Load catalogue
        StarCatalogue catalogue;
        try (InputStream hs = getClass().getResourceAsStream(("/hygdata_v3.csv"));
             InputStream ast = getClass().getResourceAsStream("/asterisms.txt");){

             catalogue = new StarCatalogue.Builder()
                    .loadFrom(hs, HygDatabaseLoader.INSTANCE)
                    .loadFrom(ast, AsterismLoader.INSTANCE)
                    .build();
        }catch (Exception e) {
            System.err.println("Error loading star data");
            catalogue = new StarCatalogue.Builder().build();
        }

        //Load font
        try (InputStream fontStream = getClass()
                .getResourceAsStream("/Font Awesome 5 Free-Solid-900.otf")) {
            fontAwesome = Font.loadFont(fontStream, 15);
        } catch (IOException e){
            System.err.println("Error loading font data");
            fontAwesome = Font.getDefault();
        }


        //Parameter instantiation
        DateTimeBean dateTimeBean = new DateTimeBean(ZonedDateTime.now());
        ObserverLocationBean observerLocationBean = new ObserverLocationBean( GeographicCoordinates.ofDeg(DEFAULT_LONGITUDE, DEFAULT_LATITUDE));
        ViewingParametersBean viewingParametersBean =
                new ViewingParametersBean(DEFAULT_FIELD_OF_VIEW, HorizontalCoordinates.ofDeg(DEFAULT_OBSERVATION_AZIMUTH, DEFAULT_OBSERVATION_ALTITUDE));

        //Graphics engine instantiation
        SkyCanvasManager canvasManager = new SkyCanvasManager(catalogue, dateTimeBean, observerLocationBean,
                viewingParametersBean);
        Canvas sky = canvasManager.canvas(); //Extract canvas from graphics engine

        /*
                                                        GUI

                                ¦--  TOP   -- primaryBox <= controlBar()    Parameter controls
                                ¦
         primaryStage---root----¦-- CENTER -- skyPane    <= sky             Pane containing rendered graphics
                                ¦
                                ¦-- BOTTOM -- infoBar    <= infoBar()       Information display

         */

        BorderPane root = new BorderPane();



        HBox primaryBox = controlBar(dateTimeBean, observerLocationBean, canvasManager);
        Pane skyPane = new Pane(sky);
        BorderPane infoBar = infoBar(viewingParametersBean, canvasManager);

        root.setTop(primaryBox);
        root.setCenter(skyPane);
        root.setBottom(infoBar);

        sky.widthProperty().bind(skyPane.widthProperty());
        sky.heightProperty().bind(skyPane.heightProperty());

        //Window parameters
        primaryStage.setTitle("Rigel");
        primaryStage.setMinHeight(600);
        primaryStage.setMinWidth(800);
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("file:icon.png"));

        primaryStage.show();
        sky.requestFocus();

    }

    private HBox controlBar(DateTimeBean dateTimeBean, ObserverLocationBean observerLocationBean, SkyCanvasManager canvasManager) {

        HBox controlBar = new HBox(
                obsPosBox(observerLocationBean),
                new Separator(Orientation.VERTICAL),
                obsInstBox(dateTimeBean, canvasManager),
                new Separator(Orientation.VERTICAL),
                accBox(dateTimeBean, canvasManager)
        );
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    private HBox obsPosBox(ObserverLocationBean observerLocationBean) {
        Label lonLabel = new Label("Longitude (°) :");
        TextField lonTextField = new TextField();
        lonTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> lonTextFormatter = textFormatter(GeographicCoordinates::isValidLonDeg);
        lonTextField.setTextFormatter(lonTextFormatter);
        lonTextFormatter.valueProperty().bindBidirectional(observerLocationBean.lonDegProperty());

        Label latLabel = new Label("Latitude (°) :");
        TextField latTextField = new TextField();
        latTextField.setStyle("-fx-pref-width: 60; -fx-alignment: baseline-right;");
        TextFormatter<Number> latTextFormatter = textFormatter(GeographicCoordinates::isValidLatDeg);
        latTextField.setTextFormatter(latTextFormatter);
        latTextFormatter.valueProperty().bindBidirectional(observerLocationBean.latDegProperty());

        HBox observationPosBox = new HBox(lonLabel, lonTextField, latLabel, latTextField);
        observationPosBox.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");
        return observationPosBox;
    }

    /**
     * Creates a TextFormatter based on a logical predicate. This text formatter will disallow modifications if the
     * predicate is not respected
     * @param predicate : logical condition based on a Double
     * @return created TextFormatter
     */
    private TextFormatter<Number> textFormatter(Predicate<Double> predicate) {
        NumberStringConverter stringConverter = new NumberStringConverter("#0.00");

        UnaryOperator<TextFormatter.Change> validityFilter = (change -> {
            try {
                String newText =
                        change.getControlNewText();
                double newDeg =
                        stringConverter.fromString(newText).doubleValue();

                    return predicate.test(newDeg)
                                ? change
                                : null;
            } catch (Exception e) {
                return null;
            }
        });
        return new TextFormatter<>(stringConverter, 0, validityFilter);
    }

    private HBox obsInstBox(DateTimeBean dateTimeBean, SkyCanvasManager canvasManager) {
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

        HBox obsIntBox = new HBox(dateLabel, datePicker, timeLabel, timeTextField, zoneIdComboBox);
        obsIntBox.disableProperty().bind(canvasManager.timeAnimatorRunningProperty());
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

    private HBox accBox(DateTimeBean dateTimeBean, SkyCanvasManager canvasManager) {
        ChoiceBox<NamedTimeAccelerator> acceleratorChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(NamedTimeAccelerator.values()));
        acceleratorChoiceBox.setValue(DEFAULT_ACCELERATOR);
        canvasManager.timeAccProperty().bind(Bindings.select(acceleratorChoiceBox.valueProperty(), "accelerator"));

        Button resetButton = new Button(RESET_CHAR);
        Button pausePlayButton = new Button(PLAY_CHAR);

        pausePlayButton.textProperty().bind(
                when(canvasManager.timeAnimatorRunningProperty())
                .then(PAUSE_CHAR)
                .otherwise(PLAY_CHAR));

        pausePlayButton.setOnAction(event -> {
            if (canvasManager.getTimeAnimatorRunning()) {
                canvasManager.getTimeAnimator().stop();
            } else {
                canvasManager.getTimeAnimator().start();
            }
        });

        acceleratorChoiceBox.disableProperty().bind(canvasManager.timeAnimatorRunningProperty());

        resetButton.setOnAction(event -> dateTimeBean.setZonedDateTime(ZonedDateTime.now()));
        resetButton.disableProperty().bind(canvasManager.timeAnimatorRunningProperty());
        resetButton.setFont(fontAwesome);
        pausePlayButton.setFont(fontAwesome);

        HBox accBox = new HBox(acceleratorChoiceBox, resetButton, pausePlayButton);
        accBox.setStyle("-fx-spacing: inherit;");
        return accBox;

    }

    private BorderPane infoBar(ViewingParametersBean viewingParametersBean, SkyCanvasManager canvasManager) {
        BorderPane infoPane = new BorderPane();

        Text fov = new Text();
        fov.textProperty().bind(format("Champ de vue : %.1f°", viewingParametersBean.fieldOfViewDegProperty()));

        StringBinding objectName;
        objectName = Bindings.createStringBinding(() -> canvasManager.objectUnderMouse.get().isPresent() ? canvasManager.getObjectUnderMouse().get().info() : "", canvasManager.objectUnderMouse);

        Text objectUnderMouse = new Text();
        objectUnderMouse.textProperty().bind(objectName);

        Text mousePosCoord = new Text();
        mousePosCoord.textProperty().bind(format("Azimut : %.2f°, hauteur : %.2f°", canvasManager.mouseAzDegProperty(),
                canvasManager.mouseAltDegProperty()));

        infoPane.setLeft(fov);
        infoPane.setCenter(objectUnderMouse);
        infoPane.setRight(mousePosCoord);

        infoPane.setStyle("-fx-padding: 4; -fx-background-color: white;");
        return infoPane;
    }
}
