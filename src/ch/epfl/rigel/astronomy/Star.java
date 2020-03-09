package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

public final class Star extends CelestialObject {
    private final int hipparcosId;
    private final float colorIndex;

    private static final float STAR_ANGULAR_SIZE = 0f;
    private static final ClosedInterval COLOR_INDEX_INTERVAL = ClosedInterval.of(-0.5,5.5);

    public Star(int hipparcosId, String name, EquatorialCoordinates equatorialPos, float magnitude,  float colorIndex) {
        super(name, equatorialPos, STAR_ANGULAR_SIZE, magnitude);
        Preconditions.checkArgument(hipparcosId >= 0);
        this.hipparcosId = hipparcosId;
        this.colorIndex = (float) Preconditions.checkInInterval(COLOR_INDEX_INTERVAL, colorIndex);
    }

    public int hipparcosId(){
        return hipparcosId;
    }

    public int colorTemperature(){
        return (int) Math.floor(4600d * (1 / (0.92 * colorIndex + 1.7) + 1 / (0.92 * colorIndex + 0.62)));
    }
}
