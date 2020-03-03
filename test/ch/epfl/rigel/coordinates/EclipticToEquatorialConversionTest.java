package ch.epfl.rigel.coordinates;

import ch.epfl.rigel.astronomy.Epoch;
import ch.epfl.rigel.math.Angle;
import org.junit.jupiter.api.Test;

import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EclipticToEquatorialConversionTest {


    private static final ArrayList<EclipticCoordinates> toConvert = new ArrayList<>() {{
        add(EclipticCoordinates.ofDeg(10, 40));
        add(EclipticCoordinates.ofDeg(0, 0));
        add(EclipticCoordinates.ofDeg(359.999999, 0));
        add(EclipticCoordinates.ofDeg(10, -90));
        add(EclipticCoordinates.ofDeg(10, 90));
    }};

    private static final ArrayList<EquatorialCoordinates> expectedResults = new ArrayList<>() {{
        add(EquatorialCoordinates.ofDeg(349.95444, 39.9904));
        add(EquatorialCoordinates.ofDeg(0, 0));
        add(EquatorialCoordinates.ofDeg(359.999999, 0.0000004));
        add(EquatorialCoordinates.ofDeg(90, -66.5607));
        add(EquatorialCoordinates.ofDeg(270, 66.5607088));
    }};


    @Test
    void functionCorrectlyConvertsKnownAndNonTrivialValues() {

        EclipticToEquatorialConversion conv = new EclipticToEquatorialConversion(ZonedDateTime.of(2001, 1,1,0,0,0,0, ZoneOffset.UTC));
         EclipticCoordinates ecl = EclipticCoordinates.of(Angle.ofDMS(9,0 , 0), Angle.ofDMS(42, 2,0));

        EquatorialCoordinates eq = conv.apply(ecl);

        for (int i = 0; i < toConvert.size(); ++i) {
            EquatorialCoordinates result = conv.apply(toConvert.get(i));
            assertEquals(expectedResults.get(i).lon(), result.lon(), 1e-5);
            assertEquals(expectedResults.get(i).lat(), result.lat(), 1e-5);
        }

    }
}