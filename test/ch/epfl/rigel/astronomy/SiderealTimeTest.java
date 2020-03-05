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
        add(ZonedDateTime.of(2043, 7,11,23,6,32,0,ZoneOffset.of("-8")));
        add(ZonedDateTime.of(3145, 7,11,23,6,32,0,ZoneOffset.of("-8")));
       // add(ZonedDateTime.of(30000, 10,13,18,33,45,0, ZoneOffset.of("+10")));
    }};

    private static final ArrayList<Double> expectedSiderealTimeGreenwich = new ArrayList<>() {{
        add(Angle.ofDeg(221.0947222225));
        add(Angle.ofDeg(280.7464317977));
        add(Angle.ofDeg(156.0578849772));
        add(Angle.ofDeg(156.2166736126));
        add(Angle.ofDeg(129.9545712471));
    }};

    private static final ArrayList<Double> expectedSiderealTimeLocal = new ArrayList<>() {{
        add(Angle.ofDeg(297.0947222225));
        add(Angle.ofDeg(356.7464317977));
        add(Angle.ofDeg(232.0578849772));
        add(Angle.ofDeg(232.2166736126));
        add(Angle.ofDeg(205.9545712471));
    }};

    @Test
    void greenwich() {
        assertEquals(Angle.ofHr(4.668120), SiderealTime.greenwich(ZonedDateTime.of(1980, 4, 22, 14, 36, 51, 670000000, ZoneId.of("+0"))), 1e-4);
    }

    @Test
    void local() {
        assertEquals(Angle.ofDeg(146.02), SiderealTime.local(ZonedDateTime.of(1980, 4, 22, 14, 36, 51, 670000000, ZoneId.of("+0")), GeographicCoordinates.ofDeg(76,0)), 1e-4);
    }

    @Test
    void worksOnNonTrivialValues(){
        double initialPrecision = 1e-4;
        double precisionLossPerCentury = 1.04e-2;
        for (int i = 0; i < localTimeToConvert.size(); i++) {
            double deltaCenturies = Math.max(Math.abs(Epoch.J2000.julianCenturiesUntil(localTimeToConvert.get(i))), 1);
            double precision = initialPrecision * Math.pow (1 + precisionLossPerCentury, deltaCenturies);
            precision = Math.pow(10.0f, Math.floor(Math.log10(precision)));
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