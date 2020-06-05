package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.*;
import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.coordinates.HorizontalCoordinates;
import ch.epfl.rigel.math.Angle;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableObjectValue;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.LocalTimeStringConverter;
import javafx.util.converter.NumberStringConverter;
import java.io.*;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    private static Font fontAwesome;

    public static void main(String[] args) { launch(args); }

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
        } catch (Exception e){
            System.err.println("Error loading font data");
            fontAwesome = Font.getDefault();
        }

        //Parameter instantiation
        DateTimeBean dateTimeBean = new DateTimeBean(ZonedDateTime.now());
        ObserverLocationBean observerLocationBean =
                new ObserverLocationBean(GeographicCoordinates.ofDeg(DEFAULT_LONGITUDE, DEFAULT_LATITUDE));
        ViewingParametersBean viewingParametersBean =
                new ViewingParametersBean(DEFAULT_FIELD_OF_VIEW,
                        HorizontalCoordinates.ofDeg(DEFAULT_OBSERVATION_AZIMUTH, DEFAULT_OBSERVATION_ALTITUDE));

        //Graphics engine instantiation
        SkyCanvasManager canvasManager = new SkyCanvasManager(catalogue, dateTimeBean, observerLocationBean,
                viewingParametersBean);
        Canvas sky = canvasManager.getCanvas(); //Extract canvas from graphics engine

        /*
                                                        GUI

                                ¦--  TOP   -- primaryBox <= controlBar()    Parameter controls
                                ¦
         primaryStage---root----¦-- CENTER -- skyPane    <= sky             Pane containing rendered graphics
                                ¦
                                ¦-- BOTTOM -- infoBar    <= infoBar()       Information display
                                ¦
                                ¦-- SIDEBAR-- sideBar    <= sideBar()       More information and controls

         */

        BorderPane root = new BorderPane();

        HBox primaryBox = controlBar(dateTimeBean, observerLocationBean, canvasManager, viewingParametersBean, primaryStage);
        Pane skyPane = new Pane(sky);
        BorderPane infoBar = infoBar(viewingParametersBean, canvasManager);
        BorderPane sideBar = sideBar(canvasManager, primaryStage);

        root.setTop(primaryBox);
        root.setCenter(skyPane);
        root.setBottom(infoBar);
        root.setRight(sideBar);

        sky.widthProperty().bind(skyPane.widthProperty());
        sky.heightProperty().bind(skyPane.heightProperty());

        //Window parameters
        primaryStage.setTitle("Rigel");
        primaryStage.setMinHeight(900);
        primaryStage.setMinWidth(1600);
        primaryStage.setScene(new Scene(root));
        primaryStage.getIcons().add(new Image("file:resources/icon.png"));
        primaryStage.show();
        sky.requestFocus();
    }

    private BorderPane sideBar(SkyCanvasManager canvasManager, Stage stage) {

        /*
        BONUS
         */

        //Inspected object information
        ObservableStringValue inspectedObjectName = Bindings.createStringBinding(()->{
            Optional<CelestialObject> obj = canvasManager.getLastObjectInspected();
            return obj.isPresent() ? obj.get().name() : "No object selected";
        }, canvasManager.lastObjectInspectedProperty());

        ObservableObjectValue<Image> inspectedObjectImage = Bindings.createObjectBinding(()->{
            Optional<CelestialObject> obj = canvasManager.getLastObjectInspected();
            if (obj.isPresent()) {
                return obj.get() instanceof Star ?
                        CelestialObjectInfo.getInfoOf("Star" ).getImage()
                        : CelestialObjectInfo.getInfoOf(obj.get().name()).getImage();
            }else return CelestialObjectInfo.NONE.getImage();
        }, canvasManager.lastObjectInspectedProperty());

        ObservableObjectValue<String> inspectedObjectDesc = Bindings.createObjectBinding(()->{
            Optional<CelestialObject> obj = canvasManager.getLastObjectInspected();
            if (obj.isPresent()) {
                return obj.get() instanceof Star ?
                        CelestialObjectInfo.getInfoOf("Star" ).getDescription(obj.get())
                        : CelestialObjectInfo.getInfoOf(obj.get().name()).getDescription(obj.get());
            }else return CelestialObjectInfo.NONE.getDescription(null);
        }, canvasManager.lastObjectInspectedProperty());

        Label objectLabel = new Label();
        objectLabel.setStyle("-fx-font-weight: bold;");
        ImageView objectImage = new ImageView();
        objectImage.setFitWidth(180);
        objectImage.setFitHeight(180);

        Text description = new Text("");
        description.setStyle("-fx-pref-width: 130; -fx-alignment: baseline-left; ");
        description.minHeight(180);
        objectLabel.textProperty().bind(inspectedObjectName);
        objectImage.imageProperty().bind(inspectedObjectImage);
        description.textProperty().bind(inspectedObjectDesc);
        description.setWrappingWidth(180);


        //Rendering options
        Label graphicsLabel = new Label("Rendering parameters");
        graphicsLabel.setStyle("-fx-font-weight: bold;");
        CheckBox stars = new CheckBox("Stars");
        stars.setSelected(true);
        CheckBox asterisms = new CheckBox("\u2ba1  Asterisms");
        asterisms.setSelected(false);
        CheckBox realism = new CheckBox("\u2ba1  Realistic starry sky");
        asterisms.setSelected(false);
        CheckBox planets = new CheckBox("Planets");
        planets.setSelected(true);
        CheckBox sun = new CheckBox("Sun");
        sun.setSelected(true);
        CheckBox sunlight = new CheckBox("\u2ba1  Generate sunlight");
        sun.setSelected(true);
        CheckBox moon = new CheckBox("Moon");
        moon.setSelected(true);
        CheckBox alt = new CheckBox("Altitude guides");
        alt.setSelected(false);
        Button fullScreen = new Button();

        //Rendering bindings
        canvasManager.getSkyCanvasPainter().starsEnabledProperty().bindBidirectional(stars.selectedProperty());
        canvasManager.getSkyCanvasPainter().asterismsEnabledProperty().bindBidirectional(asterisms.selectedProperty());
        canvasManager.getSkyCanvasPainter().realisticSkyEnabledProperty().bindBidirectional(realism.selectedProperty());
        canvasManager.getSkyCanvasPainter().planetsEnabledProperty().bindBidirectional(planets.selectedProperty());
        canvasManager.getSkyCanvasPainter().sunEnabledProperty().bindBidirectional(sun.selectedProperty());
        canvasManager.getSkyCanvasPainter().moonEnabledProperty().bindBidirectional(moon.selectedProperty());
        canvasManager.getSkyCanvasPainter().realisticSunEnabledProperty().bindBidirectional(sunlight.selectedProperty());
        canvasManager.getSkyCanvasPainter().altitudeLinesEnabledProperty().bindBidirectional(alt.selectedProperty());

        stars.selectedProperty().addListener((p,o,n) ->{if(n){
            asterisms.setDisable(false);
            asterisms.setSelected(false);
            realism.setDisable(false);
            realism.setSelected(false);
        }else{
            realism.setDisable(true);
            realism.setSelected(false);
            asterisms.setDisable(true);
            asterisms.setSelected(false);
        }});
        sun.selectedProperty().addListener((p,o,n) -> {
            if(n){
                sunlight.setDisable(false);
                sunlight.setSelected(false);
            }else{
                sunlight.setDisable(true);
                sunlight.setSelected(false);
            }
        });

        SimpleBooleanProperty isFullScreen = new SimpleBooleanProperty(false);
        fullScreen.textProperty().bind(
                when(isFullScreen)
                .then("Exit fullscreen")
                .otherwise("Fullscreen view")
        );

        fullScreen.setOnAction(((e)->{
            stage.setFullScreen(!isFullScreen.get());
            isFullScreen.set(!isFullScreen.get());
        }));


        GridPane infoBox = new GridPane();
        infoBox.setGridLinesVisible(false);
        infoBox.setStyle("-fx-pref-width: 200; -fr-pref-height: 300;  -fx-alignment: baseline-left;" +
                "-fx-background-color: white;");
        infoBox.setHgap(10);
        infoBox.setVgap(10);
        infoBox.setPadding(new Insets(10, 10, 10, 10));
        

        GridPane graphicsBox = new GridPane();
        graphicsBox.setGridLinesVisible(false);
        graphicsBox.setStyle("-fx-pref-width: 200;-fx-alignment: baseline-left;" +
                "-fx-background-color: white;");
        graphicsBox.setHgap(10);
        graphicsBox.setVgap(10);
        graphicsBox.setPadding(new Insets(10, 10, 10, 10));


        Label classification = new Label("Star type: ");
        Label type = new Label();
        classification.setStyle("-fx-font-weight:bold;");
        type.setStyle("-fx-font-weight:bold; -fx-background-color: grey;");

        //Only show star information if inspected object is a star
        canvasManager.lastObjectInspectedProperty().addListener((p,o,n) -> {
            if(n.isPresent() && n.get() instanceof Star) {
                infoBox.getChildren().removeAll(classification, type);
                CelestialObjectInfo.StarType st = CelestialObjectInfo.StarType.getStarType((Star) n.get());
                type.setText(st.getType());
                type.setTextFill(st.getColor());
                infoBox.addColumn(1, classification, type);
            }else{
                infoBox.getChildren().removeAll(classification, type);
            }
        });


        infoBox.addColumn(1, objectLabel, objectImage, description);
        graphicsBox.addColumn(1, graphicsLabel ,stars, asterisms, realism, planets, sun, sunlight, moon, alt, fullScreen);
        BorderPane constructed = new BorderPane();
        constructed.setTop(infoBox);
        constructed.setBottom(graphicsBox);
        constructed.setStyle("-fx-background-color:white;");
        return constructed;
    }

    private HBox controlBar(DateTimeBean dateTimeBean, ObserverLocationBean observerLocationBean, SkyCanvasManager canvasManager, ViewingParametersBean viewingParameters, Stage primaryStage) {

        HBox controlBar = new HBox(
                obsPosBox(observerLocationBean),
                new Separator(Orientation.VERTICAL),
                obsInstBox(dateTimeBean, canvasManager),
                new Separator(Orientation.VERTICAL),
                accBox(dateTimeBean, canvasManager),
                new Separator(Orientation.VERTICAL),
                saveBox(dateTimeBean, observerLocationBean, viewingParameters, primaryStage)
        );
        controlBar.setStyle("-fx-spacing: 4; -fx-padding: 4;");
        return controlBar;
    }

    private HBox saveBox(DateTimeBean dateTimeBean, ObserverLocationBean observerLocationBean, ViewingParametersBean viewingParameters, Stage primaryStage) {

        /*
        BONUS
         */

        //Save / Open buttons
        Button save = new Button("Save view");
        Button open = new Button("Open view...");
        save.setStyle("-fx-pref-width: 80; -fx-alignment: baseline-center;");
        open.setStyle("-fx-pref-width: 80; -fx-alignment: baseline-center;");

        save.setOnAction((e) ->{
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Rigel View files (*.rgvf", "*.rgvf");
            fc.getExtensionFilters().add(extensionFilter);

            File toSave = fc.showSaveDialog(primaryStage);
            if(toSave != null) {
                try {
                    //Write current parameters to file
                    BufferedWriter fw = new BufferedWriter(new FileWriter(toSave));
                    fw.write(Double.toString(viewingParameters.getCenter().az()));
                    fw.newLine();
                    fw.write(Double.toString(viewingParameters.getCenter().alt()));
                    fw.newLine();
                    fw.write(Double.toString(viewingParameters.getFieldOfViewDeg()));
                    fw.newLine();
                    fw.write(dateTimeBean.getZonedDateTime().toString());
                    fw.newLine();
                    fw.write(Double.toString(observerLocationBean.getCoordinates().lon()));
                    fw.newLine();
                    fw.write(Double.toString(observerLocationBean.getCoordinates().lat()));
                    fw.close();
                } catch (IOException ex) {
                    System.err.println("Could not save file!");
                }
            }
        });

        open.setOnAction((e) ->{
            FileChooser fc = new FileChooser();
            FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("Rigel View files (*.rgvf", "*.rgvf");
            fc.getExtensionFilters().add(extensionFilter);

            File toOpen = fc.showOpenDialog(primaryStage);
            if(toOpen != null) {
                try {
                    //Set parameters to parameters read from file
                    BufferedReader fr = new BufferedReader(new FileReader(toOpen));
                    viewingParameters.setCenter(HorizontalCoordinates.of(Double.parseDouble(fr.readLine()), Double.parseDouble(fr.readLine())));
                    viewingParameters.setFieldOfViewDeg(Double.parseDouble(fr.readLine()));
                    dateTimeBean.setZonedDateTime(ZonedDateTime.parse(fr.readLine()));
                    observerLocationBean.setCoordinates(GeographicCoordinates.ofDeg(Angle.toDeg(Double.parseDouble(fr.readLine())), Angle.toDeg(Double.parseDouble(fr.readLine()))));

                    fr.close();
                } catch (IOException ex) {
                    System.err.println("Could not open file!");
                }
            }

        });
        
        HBox toReturn = new HBox(save, open);
        toReturn.setStyle("-fx-spacing: inherit; -fx-alignment: baseline-left;");

        return toReturn;
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

        Label timeLabel = new Label("Time :");
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

        String resetChar = "\uf0e2";
        String pauseChar = "\uf04c";
        String playChar = "\uf04b";

        Button resetButton = new Button(resetChar);
        Button pausePlayButton = new Button(playChar);
        pausePlayButton.textProperty().bind(
                when(canvasManager.timeAnimatorRunningProperty())
                .then(pauseChar)
                .otherwise(playChar));

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
        fov.textProperty().bind(format("FOV : %.1f°", viewingParametersBean.fieldOfViewDegProperty()));

        StringBinding objectName;
        objectName = Bindings.createStringBinding(() -> {
            Optional<CelestialObject> object = canvasManager.getObjectUnderMouse();
            if (object.isEmpty() ||
                    (!canvasManager.getSkyCanvasPainter().isMoonEnabled() && object.get() instanceof Moon) ||
                    (!canvasManager.getSkyCanvasPainter().isPlanetsEnabled() && object.get() instanceof Planet) ||
                    (!canvasManager.getSkyCanvasPainter().isSunEnabled() && object.get() instanceof Sun) ||
                    (!canvasManager.getSkyCanvasPainter().isStarsEnabled() && object.get() instanceof Star)
            ) return "";
            else return object.get().info();
            }, canvasManager.objectUnderMouseProperty());

        Text objectUnderMouse = new Text();
        objectUnderMouse.textProperty().bind(objectName);

        Text mousePosCoord = new Text();
        mousePosCoord.textProperty().bind(format("Azimuth : %.2f°, altitude : %.2f°", canvasManager.mouseAzDegProperty(),
                canvasManager.mouseAltDegProperty()));

        infoPane.setLeft(fov);
        infoPane.setCenter(objectUnderMouse);
        infoPane.setRight(mousePosCoord);

        infoPane.setStyle("-fx-padding: 4; -fx-background-color: white;");
        return infoPane;
    }
}
