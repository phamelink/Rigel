package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.GeographicCoordinates;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class SiderealTimeTest {

    private static final GeographicCoordinates refTest =
            GeographicCoordinates.ofDeg(76.0,0);

    private static final ArrayList<ZonedDateTime> localTimeToConvert = new ArrayList<>() {{
        add(ZonedDateTime.of(2050, 5,3,0,0,0,0, ZoneOffset.of("+0")));
        add(ZonedDateTime.of(1, 1,1,0,0,0,0, ZoneOffset.of("+12")));
        add(ZonedDateTime.of(30000, 10,13,18,33,45,0, ZoneOffset.of("+10")));
    }};

    private static final ArrayList<Double> expectedSiderealTimeGreenwich = new ArrayList<>() {{
        add(Angle.ofDeg(221.094722));
        add(Angle.ofDeg(280.7509));
        add(Angle.ofDeg(129.95385));
    }};

    private static final ArrayList<Double> expectedSiderealTimeLocal = new ArrayList<>() {{
        add(Angle.ofDeg(297.0978));
        add(Angle.ofDeg(356.7509));
        add(Angle.ofDeg(205.95385));
    }};

    @Test
    void greenwich() {
        assertEquals(Angle.ofHr(4.668120), SiderealTime.greenwich(ZonedDateTime.of(1980, 4, 22, 14, 36, 52, 0, ZoneId.of("+0"))), 1e-4);
    }

    @Test
    void local() {
        assertEquals(Angle.ofDeg(146.02), SiderealTime.local(ZonedDateTime.of(1980, 4, 22, 14, 36, 52, 0, ZoneId.of("+0")), GeographicCoordinates.ofDeg(76,0)), 1e-4);
    }

    @Test
    void worksOnNonTrivialValues(){
        double initialPrecision = 1e-4;
        double precisionLossPerCentury = 1.7e-2;
        for (int i = 0; i < localTimeToConvert.size(); i++) {
            double deltaCenturies = Math.max(Math.abs(Epoch.J2000.julianCenturiesUntil(localTimeToConvert.get(i))), 1);
            double precision = initialPrecision * Math.pow (1 + precisionLossPerCentury, deltaCenturies);
            double resultG = SiderealTime.greenwich(localTimeToConvert.get(i));
            double resultLocal = SiderealTime.local(localTimeToConvert.get(i), refTest);
            System.out.println("Test " + i + " precision (centDelta: " + deltaCenturies + " expPrec: " + precision
                    + "): " + (resultG-expectedSiderealTimeGreenwich.get(i))
                    + " | " + (resultLocal-expectedSiderealTimeLocal.get(i)));

            assertEquals(expectedSiderealTimeGreenwich.get(i), resultG, precision);
            assertEquals(expectedSiderealTimeLocal.get(i), resultLocal, precision);

        }
    }
}