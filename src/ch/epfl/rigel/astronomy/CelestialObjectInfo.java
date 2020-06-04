package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.math.ClosedInterval;
import ch.epfl.rigel.math.Interval;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.util.NoSuchElementException;
import java.util.Optional;

public enum CelestialObjectInfo {
    SUN("Sun", "bodies/sun.jpg", "The Sun—the heart of our solar system—is a yellow dwarf star, a hot ball of glowing gases. Its gravity holds the solar system together, keeping everything from the biggest planets to the smallest particles of debris in its orbit. Electric currents in the Sun generate a magnetic field that is carried out through the solar system by the solar wind—a stream of electrically charged gas blowing outward from the Sun in all directions."),
    MOON("Moon", "bodies/moon.jpg", "The brightest and largest object in our night sky, the Moon makes Earth a more livable planet by moderating our home planet's wobble on its axis, leading to a relatively stable climate. It also causes tides, creating a rhythm that has guided humans for thousands of years. The Moon was likely formed after a Mars-sized body collided with Earth."),
    MERCURY("Mercury", "bodies/mercury.jpg", "The smallest planet in our solar system and nearest to the Sun, Mercury is only slightly larger than Earth's Moon. From the surface of Mercury, the Sun would appear more than three times as large as it does when viewed from Earth, and the sunlight would be as much as seven times brighter. Despite its proximity to the Sun, Mercury is not the hottest planet in our solar system – that title belongs to nearby Venus, thanks to its dense atmosphere."),
    VENUS("Venus", "bodies/venus.jpg", "Second planet from the Sun and our closest planetary neighbor, Venus is similar in structure and size to Earth, but it is now a very different world. Venus spins slowly in the opposite direction most planets do. Its thick atmosphere traps heat in a runaway greenhouse effect, making it the hottest planet in our solar system—with surface temperatures hot enough to melt lead. Glimpses below the clouds reveal volcanoes and deformed mountains."),
    MARS("Mars", "bodies/mars.jpg", "The fourth planet from the Sun, Mars is a dusty, cold, desert world with a very thin atmosphere. This dynamic planet has seasons, polar ice caps and weather and canyons and extinct volcanoes, evidence of an even more active past."),
    JUPITER("Jupiter", "bodies/jupiter.jpg", "Jupiter has a long history surprising scientists—all the way back to 1610 when Galileo Galilei found the first moons beyond Earth. That discovery changed the way we see the universe. Fifth in line from the Sun, Jupiter is, by far, the largest planet in the solar system – more than twice as massive as all the other planets combined. Jupiter's familiar stripes and swirls are actually cold, windy clouds of ammonia and water, floating in an atmosphere of hydrogen and helium. Jupiter’s iconic Great Red Spot is a giant storm bigger than Earth that has raged for hundreds of years."),
    SATURN("Saturn", "bodies/saturn.png", "Saturn is the sixth planet from the Sun and the second largest planet in our solar system. Adorned with thousands of beautiful ringlets, Saturn is unique among the planets. It is not the only planet to have rings—made of chunks of ice and rock—but none are as spectacular or as complicated as Saturn's. Like fellow gas giant Jupiter, Saturn is a massive ball made mostly of hydrogen and helium."),
    URANUS("Uranus", "bodies/uranus.jpg", "The first planet found with the aid of a telescope, Uranus was discovered in 1781 by astronomer William Herschel, although he originally thought it was either a comet or a star. It was two years later that the object was universally accepted as a new planet, in part because of observations by astronomer Johann Elert Bode. Herschel tried unsuccessfully to name his discovery Georgium Sidus after King George III. Instead the scientific community accepted Bode's suggestion to name it Uranus, the Greek god of the sky, as suggested by Bode."),
    NEPTUNE("Neptune", "bodies/neptune.jpg", "Dark, cold and whipped by supersonic winds, ice giant Neptune is the eighth and most distant planet in our solar system. More than 30 times as far from the Sun as Earth, Neptune is the only planet in our solar system not visible to the naked eye and the first predicted by mathematics before its discovery. In 2011 Neptune completed its first 165-year orbit since its discovery in 1846."),
    STAR("Star", "bodies/star.jpg", "One of the many stars in our sky"),
    NONE("Default", "bodies/none.jpg", "");


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

    public String getDescription(CelestialObject obj) {
        return description;

    }



    public static CelestialObjectInfo getInfoOf(String name){
        for(CelestialObjectInfo info : CelestialObjectInfo.values()){
            if(info.name.equals(name)) return info;
        }

        return CelestialObjectInfo.NONE;
    }

    public enum StarType {
        WHITE_DWARF("White dwarf", ClosedInterval.of(10, 15), ClosedInterval.of(5200, 7500), Color.ANTIQUEWHITE),
        SUB_GIANT("Sub giant", ClosedInterval.of(1, 3), ClosedInterval.of(5200, 6000), Color.ANTIQUEWHITE),
        BLUE_SUB_GIANT("Blue sub giant", ClosedInterval.of(-5, 0), ClosedInterval.of(6000, 40000), Color.LIGHTBLUE),
        RED_GIANT("Red giant", ClosedInterval.of(-1.5, 1.5), ClosedInterval.of(1000, 5000), Color.RED),
        BlUE_GIANT("Blue giant", ClosedInterval.of(-1.5, 1.5), ClosedInterval.of(8000, 50000), Color.BLUE),
        GIANT("Giant", ClosedInterval.of(-1.5, 1.5), ClosedInterval.of(1000, 50000), Color.YELLOW),
        RED_BRIGHT("Red bright giant", ClosedInterval.of(-5, -1.5), ClosedInterval.of(1000, 5000), Color.RED),
        BlUE_BRIGHT("Blue bright Giant", ClosedInterval.of(-5, -1.5), ClosedInterval.of(8000, 50000), Color.BLUE),
        BRIGHT("Bright giant", ClosedInterval.of(-5, -1.5), ClosedInterval.of(1000, 50000), Color.YELLOW),
        RED_SUPER("Red supergiant", ClosedInterval.of(-20, -5), ClosedInterval.of(1000, 5000), Color.RED),
        BlUE_SUPER("Blue supergiant", ClosedInterval.of(-20, -5), ClosedInterval.of(8000, 50000), Color.BLUE),
        SUPER("Supergiant", ClosedInterval.of(-20, 1-5), ClosedInterval.of(1000, 50000), Color.YELLOW),
        SUB_DWARF("Sub dwarf", ClosedInterval.of(5, 15), ClosedInterval.of(2000, 5000), Color.ORANGERED),
        DWARF("Dwarf", ClosedInterval.of(-5, 7), ClosedInterval.of(4000, 40000), Color.LIGHTBLUE),
        UNCLASSIFIED("Main sequence", ClosedInterval.of(-20, 20), ClosedInterval.of(0, 40000), Color.BLACK);

        private final String type;
        private final Interval magnitude;
        private final Interval temp;
        private final Color color;

        StarType(String type, Interval magnitude, Interval temp, Color color) {
            this.type = type;
            this.magnitude = magnitude;
            this.temp = temp;
            this.color = color;
        }

        public static StarType getStarType(Star star){
            for(StarType type : StarType.values()){
                if(type.temp.contains(star.colorTemperature()) && type.magnitude.contains(star.magnitude())) return type;
            }
            throw new NoSuchElementException("No type found");
        }

        public String getType() {
            return type;
        }

        public Interval getMagnitude() {
            return magnitude;
        }

        public Interval getTemp() {
            return temp;
        }

        public Color getColor() {
            return color;
        }
    }

}
