package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.SiderealTime;
import ch.epfl.rigel.math.Angle;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class EquatorialToHorizontalConversion implements Function<EquatorialCoordinates, HorizontalCoordinates> {

    private final double localSiderealTime;
    private final double sinLat;
    private final double cosLat;

    public EquatorialToHorizontalConversion(ZonedDateTime when, GeographicCoordinates where) {
        this.sinLat = Math.sin(where.lat());
        this.cosLat = Math.cos(where.lat());
        this.localSiderealTime = SiderealTime.local(when, where);


    }

    @Override
    public HorizontalCoordinates apply(EquatorialCoordinates eqc){
        double sinDelta = Math.sin(eqc.dec());
        double cosDelta = Math.cos(eqc.dec());
        System.out.println("lst " + Angle.toHr(localSiderealTime));
        double hourAngle = Angle.normalizePositive(localSiderealTime - eqc.ra());

        System.out.println(Angle.toHr(hourAngle));
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
