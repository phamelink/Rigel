package ch.epfl.rigel.astronomy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;

public enum Epoch {

    J2000(LocalDate.of(2000,Month.JANUARY,1), LocalTime.of(12,0), ZoneOffset.UTC),
    J2010(LocalDate.of(2010,Month.JANUARY,1).minusDays(1), LocalTime.of(0,0), ZoneOffset.UTC);

    private LocalDate localDate;
    private LocalTime localTime;
    private ZoneOffset zoneOffset;
    private final ZonedDateTime epoch;

    private static final double MILLI_SEC_IN_ONE_DAY = 1000*60*60*24;
    private static final double JD_PER_MILLI = 1.160576283970827623434167721946407810448055e-8;
    private static final int DAYS_IN_ONE_JULIAN_CENTURY = 36525;
    private static final double NANO_IN_A_DAY = 8.64e+13;

    private static final double JD_PER_DAY = 1.00273790935079506664712091176169634822711936939293;
    private static final double NANO_IN_A_JD = 86164090530832.88;


    private Epoch(LocalDate localDate, LocalTime localTime, ZoneOffset zoneOffset) {
        this.localDate = localDate;
        this.localTime = localTime;
        this.zoneOffset = zoneOffset;
        this.epoch = ZonedDateTime.of(this.localDate, this.localTime, this.zoneOffset.normalized());
    }

    /**
     * returns number of days from the chosen reference date (J200 or J2010) to the date given in parameters
     * @param when (ZonedDateTime): date and time to calculate number of days from
     * @return (double) number of days
     */
    public double daysUntil(ZonedDateTime when) {
        //Calculate separately for better precision
        double dayDelta;
        long intraDayDelta;
        ZonedDateTime truncatedDate = when.truncatedTo(ChronoUnit.DAYS);
        ZonedDateTime truncatedEpoch = epoch.truncatedTo(ChronoUnit.DAYS);
        dayDelta = truncatedEpoch.until(truncatedDate, ChronoUnit.DAYS);
        long nanoOfDay = when.getLong(ChronoField.NANO_OF_DAY);
        long nanoOfEpoch = epoch.getLong(ChronoField.NANO_OF_DAY);
        intraDayDelta = nanoOfDay - nanoOfEpoch;

        double milliSecUntil = epoch.until(when, ChronoUnit.MILLIS);
        System.out.println("->" + (milliSecUntil/MILLI_SEC_IN_ONE_DAY));
        //return milliSecUntil/MILLI_SEC_IN_ONE_DAY;

        return dayDelta + intraDayDelta / NANO_IN_A_DAY ;
        //return milliSecUntil * JD_PER_MILLI;
    }

    /**
     * returns number of julian centuries from the chosen reference date (J200 or J2010) to the date given in parameters
     * @param when (ZonedDateTime): date and time to calculate number of julian centuries from
     * @return (double) number of julian centuries
     */
    public double julianCenturiesUntil(ZonedDateTime when) { return daysUntil(when) / DAYS_IN_ONE_JULIAN_CENTURY; }

    public ZonedDateTime getZDT(){
        return epoch;
    }
}
