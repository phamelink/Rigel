package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.function.Function;

/**
 * Equatorial to horizontal conversion
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double localSiderealTime;
    private final double sinLat;
    private final double cosLat;

    /**
     * constructs a change of coordinate system between quatorial coordinates
     * to horizontal coordinates for the couple (date/time) when and the place where
     * @param when:
     *            date/time to convert from
     * @param where:
     *            place to convert from
     */
    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        this.sinLat = Math.sin(where.lat());
        this.cosLat = Math.cos(where.lat());
        this.localSiderealTime = SiderealTime.local(when, where);


    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates eqc){
        double sinDelta = Math.sin(eqc.dec());
        double cosDelta = Math.cos(eqc.dec());
        double hourAngle = Angle.normalizePositive(localSiderealTime - eqc.ra());

        double sinAlt = sinDelta * sinLat + cosDelta * cosLat * Math.cos(hourAngle);
        double alt = Math.asin(sinAlt);
        double azimuth = Math.acos((sinDelta - sinLat * sinAlt) / (cosLat * Math.cos(alt)));

        if(Math.sin(hourAngle) > 0) azimuth = Angle.TAU - azimuth; //find correct quadrant

        return HorizontalCoordinates.of(azimuth, alt);
    }


    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }

}
