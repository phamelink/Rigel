package ch.epfl.rigel.astronomy;

import org.junit.jupiter.api.Test;

import java.time.*;

import static org.junit.jupiter.api.Assertions.*;

class EpochTest {

    @Test
    void daysUntil() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2000, Month.JANUARY, 3),
                LocalTime.of(18,0),
                ZoneOffset.UTC);
        assertEquals(2.25, Epoch.J2000.daysUntil(d), 1e-4);

    }

    @Test
    void julianCenturiesUntil() {
        ZonedDateTime d = ZonedDateTime.of(
                LocalDate.of(2020, Month.FEBRUARY, 28),
                LocalTime.of(18,0),
                ZoneOffset.UTC);
        assertEquals(0.20159, Epoch.J2000.julianCenturiesUntil(d), 1e-4);

    }
}