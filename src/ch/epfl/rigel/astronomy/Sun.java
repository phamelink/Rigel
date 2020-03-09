package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

public final class Sun extends CelestialObject{
    private static final float SUN_MAGNITUDE = -26.7f;
    private static final String BODY_NAME = "Soleil";

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super(BODY_NAME, equatorialPos, angularSize, SUN_MAGNITUDE);
        Objects.requireNonNull(eclipticPos, "Ecliptic position is null");
        this.eclipticPos = eclipticPos;
        this.meanAnomaly = meanAnomaly;
    }

    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    public float meanAnomaly() {
        return meanAnomaly;
    }
}
