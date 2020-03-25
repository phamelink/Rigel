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
    private final int hipparcosId;
    private final float colorIndex;

    private static final float STAR_ANGULAR_SIZE = 0f;
    private static final ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval.of(-0.5,5.5);


    /**
     * Star constructor
     * constructs a star with the Hipparcos number, the name, the equatorial position, the magnitude and the color indice
     * @param hipparcosId:
     *                   Hipparcos number
     * @param name:
     *            name of star
     * @param equatorialPos:
     *                     equatorial position of star
     * @param magnitude:
     *                 magnitude
     * @param colorIndex:
     *                  color index
     * @throws IllegalArgumentException if Hipparcos is negative or the color indice
     *          is not contained in the interval [-0.5, 5.5]
     */
    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude,  float colorIndex) {
        super(name, equatorialPos, STAR_ANGULAR_SIZE, magnitude);
        Preconditions.checkArgument(hipparcosId >= 0);
        this.hipparcosId = hipparcosId;
        this.colorIndex = (float) Preconditions.checkInInterval(COLOR_INDEX_INTERVAL, colorIndex);
    }

    /**
     * returns Hipparcos number
     * @return Hipparcos number
     */
    public int hipparcosId(){
        return hipparcosId;
    }

    /**
     * returns color temperature of the star (in kelvin degrees, floored)
     * @return color temperature of the star (in kelvin degrees, floored)
     */
    public int colorTemperature(){
        return (int) Math.floor(4600d * (1 / (0.92 * colorIndex + 1.7) + 1 / (0.92 * colorIndex + 0.62)));
    }
}
