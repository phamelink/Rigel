package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

public final class SiderealTime {
    private static final Polynomial SIDEREAL_TIME_0 = Polynomial.of(0.000025862,2400.051336, 6.697374558);
    private static final Polynomial SIDEREAL_TIME_1 = Polynomial.of(1.002737909);

    private SiderealTime() {}

    /**
     * returns sidereal time in Greenwich (in radians and within the interval [0,TAU[)
     * @param when (ZonedDateTime) the time and date in greenwich
     * @return (double) siderealtime in radians
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime truncatedDate = when.truncatedTo(ChronoUnit.HOURS);
        double julianCenturiesDifference = Epoch.J2000.julianCenturiesUntil(truncatedDate);
        double hoursSinceBeginningOfDay = truncatedDate.until(when, ChronoUnit.MILLIS)/3600000;
        double siderealTimeGreenwichHr = SIDEREAL_TIME_0.at(julianCenturiesDifference) + SIDEREAL_TIME_1.at(hoursSinceBeginningOfDay);
        double siderealTimeGreenwichRad = Angle.ofHr(siderealTimeGreenwichHr);
        return Angle.normalizePositive(siderealTimeGreenwichRad);
    }

    /**
     * returns local sidereal time (in radians and within the interval [0,TAU[)
     * @param when (ZonedDateTime) local time and date
     * @param where (GeographicalCoordinates) longitude and latitude
     * @return (double) local sidereal time in radians
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        ZonedDateTime inGreenwichTime = when.withZoneSameInstant(ZoneId.of("Greenwich"));
        double localLongitude = Angle.toHr(where.lon());
        return Angle.normalizePositive(greenwich(inGreenwichTime) + localLongitude);
    }
}
