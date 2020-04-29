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
    //Epoch from 01/01/2000 UTC
    J2000(LocalDate.of(2000,Month.JANUARY,1), LocalTime.NOON, ZoneOffset.UTC),

    //Epoch from 01/01/2010 UTC
    J2010(LocalDate.of(2010,Month.JANUARY,1).minusDays(1), LocalTime.MIDNIGHT, ZoneOffset.UTC);

    private static final int DAYS_IN_ONE_JULIAN_CENTURY = 36525;
    private static final double MILLIS_IN_A_DAY = 1000*60*60*24;

    private final ZonedDateTime epoch;

    Epoch(LocalDate localDate, LocalTime localTime, ZoneOffset zoneOffset) {
        this.epoch = ZonedDateTime.of(localDate, localTime, zoneOffset);
    }

    /**
     * returns number of days from the chosen reference date (J2000 or J2010) to the date given in parameters
     * @param when
     *          date and time to calculate number of days from
     * @return number of days
     */
    public double daysUntil(ZonedDateTime when) {
        /*
        Calculating days until truncated date and adding nanoseconds until hour of day yields a more accurate
        result over extended periods of time.
         */
        when = when.withZoneSameInstant(ZoneId.of("UTC"));
        return epoch.until(when, ChronoUnit.MILLIS) / MILLIS_IN_A_DAY ;

    }

    /**
     * returns number of julian centuries from the chosen reference date (J2000 or J2010) to the date given in parameters
     * @param when
     *          date and time to calculate number of julian centuries from
     * @return number of julian centuries
     */
    public double julianCenturiesUntil(ZonedDateTime when) { return daysUntil(when) / DAYS_IN_ONE_JULIAN_CENTURY; }

}
