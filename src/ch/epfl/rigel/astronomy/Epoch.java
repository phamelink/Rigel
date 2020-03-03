package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;

public enum Epoch {

    J2000(LocalDate.of(2000,Month.JANUARY,1), LocalTime.of(12,0), ZoneOffset.UTC),
    J2010(LocalDate.of(2010,Month.JANUARY,1).minusDays(1), LocalTime.of(0,0), ZoneOffset.UTC);


    private static final double MILLI_SEC_IN_ONE_DAY = 1000*60*60*24;
    private static final double DAYS_IN_ONE_JULIAN_CENTURY = 36625;
    private final ZonedDateTime epochTimeStamp;


    private Epoch(LocalDate localDate, LocalTime localTime, ZoneOffset zoneOffset) {
        this.epochTimeStamp = ZonedDateTime.of(localDate, localTime, zoneOffset.normalized());
    }

    public ZonedDateTime getEpochTimeStamp(){
        return epochTimeStamp;
    }

    /**
     * returns number of days from the chosen reference date (J200 or J2010) to the date given in parameters
     * @param when (ZonedDateTime): date and time to calculate number of days from
     * @return (double) number of days
     */
    public double daysUntil(ZonedDateTime when) {
        double milliSecUntil = this.epochTimeStamp.until(when, ChronoUnit.MILLIS);
        return milliSecUntil/MILLI_SEC_IN_ONE_DAY;
    }

    /**
     * returns number of julian centuries from the chosen reference date (J200 or J2010) to the date given in parameters
     * @param when (ZonedDateTime): date and time to calculate number of julian centuries from
     * @return (double) number of julian centuries
     */
    public double julianCenturiesUntil(ZonedDateTime when) {
        return daysUntil(when)/DAYS_IN_ONE_JULIAN_CENTURY;
    }
}
