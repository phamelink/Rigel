package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;

import java.util.Objects;

/**
 * Sun
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class Sun extends CelestialObject{
    private static final float SUN_MAGNITUDE = -26.7f;
    private static final String BODY_NAME = "Soleil";

    private final EclipticCoordinates eclipticPos;
    private final float meanAnomaly;

    /**
     * constructs the sun
     * @param eclipticPos
     *          ecliptic position of the sun
     * @param equatorialPos
     *          equatorial position of the sun
     * @param angularSize
     *          angular size of the sun
     * @param meanAnomaly
     *          mean anomaly of the sun
     * @throws NullPointerException if ecliptic position is null
     */
    public Sun(EclipticCoordinates eclipticPos, EquatorialCoordinates equatorialPos, float angularSize, float meanAnomaly) {
        super(BODY_NAME, equatorialPos, angularSize, SUN_MAGNITUDE);
        Objects.requireNonNull(eclipticPos, "Ecliptic position is null");
        this.eclipticPos = eclipticPos;
        this.meanAnomaly = meanAnomaly;
    }

    /**
     * returns ecliptic coordinates of the sun
     * @return ecliptic coordinates of the sun
     */
    public EclipticCoordinates eclipticPos() {
        return eclipticPos;
    }

    /**
     * returns mean anomaly of the sun
     * @return mean anomaly of the sun
     */
    public float meanAnomaly() {
        return meanAnomaly;
    }
}
