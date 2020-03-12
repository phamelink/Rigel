package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import ch.epfl.rigel.math.Polynomial;
import ch.epfl.rigel.math.RightOpenInterval;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;

/**
 * Sidereal time class
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class SiderealTime {
    private static final Polynomial SIDEREAL_TIME_0 = Polynomial.of(0.000025862,2400.051336, 6.697374558);
    private static final Polynomial SIDEREAL_TIME_1 = Polynomial.of(1.002737909, 0);

    private SiderealTime() {}

    private static final double NANO_PER_HOUR = 3.6e+12;

    /**
     * returns sidereal time in Greenwich (in radians and within the interval [0,TAU[)
     * @param when
     *          the time and date in greenwich
     * @return (double) siderealtime in radians
     */
    public static double greenwich(ZonedDateTime when) {
        ZonedDateTime correctedOffset = when.withZoneSameInstant(ZoneId.of("UTC"));
        System.out.println(correctedOffset);

        ZonedDateTime truncatedDate = correctedOffset.truncatedTo(ChronoUnit.DAYS);
        double julianCenturiesDifference = Epoch.J2000.julianCenturiesUntil(truncatedDate);

        double hoursSinceBeginningOfDay = correctedOffset.getLong(ChronoField.NANO_OF_DAY) / NANO_PER_HOUR;
        double t = julianCenturiesDifference;
        System.out.println(t);
        double siderealTimeGreenwichHr =  SIDEREAL_TIME_0.at(julianCenturiesDifference) + SIDEREAL_TIME_1.at(hoursSinceBeginningOfDay);
        System.out.println("R: " + Angle.toHr(Angle.normalizePositive(Angle.ofHr(siderealTimeGreenwichHr))));
        return Angle.normalizePositive(Angle.ofHr(siderealTimeGreenwichHr));
    }

    /**
     * returns local sidereal time (in radians and within the interval [0,TAU[)
     * @param when (ZonedDateTime)
     *             local time and date
     * @param where (GeographicalCoordinates)
     *              longitude and latitude
     * @return (double) local sidereal time in radians
     */
    public static double local(ZonedDateTime when, GeographicCoordinates where) {
        return Angle.normalizePositive(greenwich(when) + where.lon());
    }
}
