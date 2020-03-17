package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;

/**
 * Epoch class
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public enum Epoch {

    J2000(LocalDate.of(2000,Month.JANUARY,1), LocalTime.of(12,0), ZoneOffset.UTC),
    J2010(LocalDate.of(2010,Month.JANUARY,1).minusDays(1), LocalTime.of(0,0), ZoneOffset.UTC);

    public final ZonedDateTime epoch;

    private static final int DAYS_IN_ONE_JULIAN_CENTURY = 36525;
    private static final double NANO_IN_A_DAY = 8.64e+13;

    private Epoch(LocalDate localDate, LocalTime localTime, ZoneOffset zoneOffset) {
        this.epoch = ZonedDateTime.of(localDate, localTime, zoneOffset.normalized());
    }

    /**
     * returns number of days from the chosen reference date (J200 or J2010) to the date given in parameters
     * @param when
     *          date and time to calculate number of days from
     * @return number of days
     */
    public double daysUntil(ZonedDateTime when) {
        /*
        Calculating days until truncated date and adding nanoseconds until hour of day yields a more precise
        result over extended periods of time, compared to method given.
         */
        when = when.withZoneSameInstant(ZoneId.of("UTC"));
        double dayDelta;
        long intraDayDelta;
        ZonedDateTime truncatedDate = when.truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime truncatedEpoch = epoch.truncatedTo(ChronoUnit.DAYS);
        dayDelta = truncatedEpoch.until(truncatedDate, ChronoUnit.DAYS);
        long nanoOfDay = when.getLong(ChronoField.NANO_OF_DAY);
        long nanoOfEpoch = epoch.getLong(ChronoField.NANO_OF_DAY);
        intraDayDelta = nanoOfDay - nanoOfEpoch;

        return dayDelta + intraDayDelta / NANO_IN_A_DAY ;

    }

    /**
     * returns number of julian centuries from the chosen reference date (J200 or J2010) to the date given in parameters
     * @param when
     *          date and time to calculate number of julian centuries from
     * @return number of julian centuries
     */
    public double julianCenturiesUntil(ZonedDateTime when) { return daysUntil(when) / DAYS_IN_ONE_JULIAN_CENTURY; }

}
