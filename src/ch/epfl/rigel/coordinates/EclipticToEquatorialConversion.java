package ch.epfl.rigel.coordinates;

import java.time.ZonedDateTime;
import java.util.function.Function;

public class EclipticToEquatorialConversion implements Function<EclipticCoordinates, EquatorialCoordinates> {

    public EclipticToEquatorialConversion(ZonedDateTime when) {

        //Generate a model mapping ecl to eqc at given time

    }

    @Override
    public EquatorialCoordinates apply(EclipticCoordinates ecl) {
        EquatorialCoordinates eqc = EquatorialCoordinates.of(0,0);
        return eqc;
    }

    @Override
    final public int hashCode() {
        throw new UnsupportedOperationException();
    }

    @Override
    final public boolean equals(Object obj) {
        throw new UnsupportedOperationException();
    }
}
