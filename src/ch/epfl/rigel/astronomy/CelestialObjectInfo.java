package ch.epfl.rigel.astronomy;

import javafx.scene.image.Image;

import java.util.Optional;

public enum CelestialObjectInfo {
    SUN("Sun", "bodies/sun.jpg", "The star of our solar system");

    private final String name;
    private final Image image;
    private final String description;
    CelestialObjectInfo(String name, String imageLocation, String description){
        this.name = name;
        this.image = new Image(imageLocation);
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public Image getImage() {
        return image;
    }

    public String getDescription() {
        return description;
    }

    public Optional<CelestialObjectInfo> getInfoOf(String name){
        for(CelestialObjectInfo info : CelestialObjectInfo.values()){
            if(info.name.equals(name)) return Optional.of(info);
        }
        return Optional.empty();
    }
}
