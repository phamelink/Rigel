package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.ClosedInterval;

import java.text.Format;
import java.util.Locale;

public final class Moon extends CelestialObject {

    private static final ClosedInterval PHASE_INTERVAL = ClosedInterval.of(0, 1d);
    private static final String BODY_NAME = "Lune";
    private final float phase;


    public Moon(EquatorialCoordinates equatorialPos, float angularSize, float magnitude, float phase) {
        super(BODY_NAME, equatorialPos, angularSize, magnitude);
        Preconditions.checkInInterval(PHASE_INTERVAL, phase);
        this.phase = phase;
    }

    @Override
    public String info() {
        return String.format(Locale.ROOT, "%s (%.1f%%)", name(), phase * 100);
    }
}
