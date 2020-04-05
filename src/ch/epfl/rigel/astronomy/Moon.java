package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.util.Locale;

/**
 * Moon
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class Moon extends CelestialObject {

    private static final ClosedInterval PHASE_INTERVAL = ClosedInterval.of(0, 1d);
    private static final String BODY_NAME = "Lune";
    private final float phase;

    /**
     * constructs the moon
     * @param equatorialPos
     *          equatorial coordinates of moon
     * @param angularSize
     *          angular size of moon
     * @param magnitude
     *          magnitude of moon
     * @param phase phase of the moon
     * @throws IllegalArgumentException if phase is not in interval [0,1]
     */
    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super(BODY_NAME, equatorialPos, angularSize, magnitude);

        this.phase = (float) Preconditions.checkInInterval(PHASE_INTERVAL, phase);
    }

    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%%)", name(), phase * 100);
    }
}
