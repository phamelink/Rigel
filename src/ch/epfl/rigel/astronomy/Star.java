package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

/**
 * Star
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class Star extends CelestialObject {
    private static final float STAR_ANGULAR_SIZE = 0f;
    private static final ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval.of(-0.5,5.5);

    private final int hipparcosId;
    private final int temperature;


    /**
     * Star constructor
     * constructs a star with the Hipparcos number, the name, the equatorial position, the magnitude and the color indice
     * @param hipparcosId Hipparcos number of the star
     * @param name name of star
     * @param equatorialPos equatorial position of star
     * @param magnitude magnitude of the star
     * @param colorIndex color index of the star
     * @throws IllegalArgumentException if Hipparcos is negative or the color indice
     *          is not contained in the interval [-0.5, 5.5]
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude,  float colorIndex) {
        super(name, equatorialPos, STAR_ANGULAR_SIZE, magnitude);
        Preconditions.checkArgument(hipparcosId >= 0);
        this.hipparcosId = hipparcosId;
        double colorCorrected = 0.92 * (float) Preconditions.checkInInterval(COLOR_INDEX_INTERVAL, colorIndex);
        this.temperature = (int) (4600d * (1 / (colorCorrected + 1.7) + 1 / (colorCorrected + 0.62)));
    }

    /**
     * Returns Hipparcos number
     * @return Hipparcos number
     */
    public int hipparcosId(){
        return hipparcosId;
    }

    /**
     * Returns color temperature of the star (in kelvin degrees, floored)
     * @return color temperature of the star (in kelvin degrees, floored)
     */
    public int colorTemperature(){
        return temperature;
    }
}
