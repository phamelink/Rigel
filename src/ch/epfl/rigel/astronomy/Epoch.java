package ch.epfl.rigel.astronomy;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;

public enum Epoch {

    J2000(LocalDate.of(2000,Month.JANUARY,1), LocalTime.of(12,0), ZoneOffset.UTC),
    J2010(LocalDate.of(2010,Month.JANUARY,1).minusDays(1), LocalTime.of(0,0), ZoneOffset.UTC);

    private LocalDate localDate;
    private LocalTime localTime;
    private ZoneOffset zoneOffset;

    private final double MILLI_SEC_IN_ONE_DAY = 1000*60*60*24;
    private final double DAYS_IN_ONE_JULIAN_CENTURY = 36525;


    private Epoch(LocalDate localDate, LocalTime localTime, ZoneOffset zoneOffset) {
        this.localDate = localDate;
        this.localTime = localTime;
        this.zoneOffset = zoneOffset;
    }

    /**
     * returns number of days from the chosen reference date (J200 or J2010) to the date given in parameters
     * @param when (ZonedDateTime): date and time to calculate number of days from
     * @return (double) number of days
     */
    public double daysUntil(ZonedDateTime when) {
        ZonedDateTime epoch = ZonedDateTime.of(this.localDate, this.localTime, this.zoneOffset.normalized());
        double milliSecUntil = epoch.until(when, ChronoUnit.MILLIS);
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
