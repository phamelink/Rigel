package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PlanetModel implements CelestialObjectModel<Planet>{
    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42, false),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40, false),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0, false),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52, true),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40, true),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88, true),
    URANUS("Uranus", 84.039492, 271.063148, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19, true),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87, true);

    private final String name;
    private final double revTime;
    private final double jLon;
    private final double periLon;
    private final double exc;
    private final double axis;
    private final double incl;
    private final double ascNodeLon;
    private final double theta0;
    private final double magnitude;
    private final boolean isOuterPlanet;

    public static final List<PlanetModel> ALL = List.of(PlanetModel.values());

    PlanetModel(String name, double revTime, double jLon, double periLon, double exc, double axis, double incl, double ascNodeLon,
                double theta0, double magnitude, boolean isOuterPlanet) {
        this.name = name;
        this.revTime = revTime;
        this.jLon = Angle.ofDeg(jLon);
        this.periLon = Angle.ofDeg(periLon);
        this.exc = exc;
        this.axis = axis;
        this.incl = Angle.ofDeg(incl);
        this.ascNodeLon = Angle.ofDeg(ascNodeLon);
        this.theta0 = theta0;
        this.magnitude = magnitude;
        this.isOuterPlanet = isOuterPlanet;
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {

        DatedPlanetInfo planetInfo = this.getDatedPlanetInfo(daysSinceJ2010);
        DatedPlanetInfo earthInfo = EARTH.getDatedPlanetInfo(daysSinceJ2010);
        final double R = earthInfo.distanceFromSun;
        final double L = earthInfo.helioLon;

        final double r = planetInfo.distanceFromSun;
        final double l = planetInfo.helioLon;
        final double rP = planetInfo.eclipticRadius;
        final double lP = planetInfo.heliocentric.lon();
        final double helioLat = planetInfo.heliocentric.lat();

        final EquatorialCoordinates geocentricCoord;
        if(isOuterPlanet){
            //Outer Planets

            final double lambda = lP + Math.atan(R * Math.sin(lP - L) / (rP - R * Math.cos(L - lP)));
            final double beta = Math.atan(rP * Math.atan(helioLat) * Math.sin(lambda - lP) /
                    R * Math.sin(lP - L));
            geocentricCoord = EquatorialCoordinates.of(lambda, beta);

        }else{
            //Inner Planets

            final double lambda = Math.PI + L + Math.atan(rP * Math.sin(L - lP) / (R - rP * Math.cos(L - lP)));
            final double beta = Math.atan(rP * Math.atan(helioLat) * Math.sin(lambda - lP) /
                    R * Math.sin(lP - L));
            geocentricCoord = EquatorialCoordinates.of(lambda, beta);

        }

        final double rho = Math.sqrt(Math.abs(R*R + r*r - 2 * R * Math.cos(l - L) * Math.cos(helioLat)));
        final double angularSize = theta0 / rho;

        final double phase = (1 + Math.cos(planetInfo.heliocentric.lon() - l) / 2);
        final double apparentMagnitude = magnitude + 5 * Math.log10(r*rho / Math.sqrt(phase));

        return new Planet(name, geocentricCoord, (float) angularSize, (float) apparentMagnitude);
    }

    private DatedPlanetInfo getDatedPlanetInfo(double daysSinceJ2010){
        final double meanAnom = (Angle.TAU * daysSinceJ2010) / 365.242191 + jLon - periLon;
        final double trueAnom = meanAnom + 2 * exc * Math.sin(meanAnom);

        final double radius = axis * (1-exc*exc) / (1 + exc * Math.cos(trueAnom));
        final double helioLon = trueAnom + periLon;
        final double helioLat = Math.asin(Math.sin(helioLon - ascNodeLon) * Math.sin(incl));

        final double radiusProj = radius * Math.cos(helioLat);
        final double lonProj = Math.atan((Math.sin(helioLon - ascNodeLon) / Math.cos(helioLon-ascNodeLon))) + ascNodeLon;

        final EclipticCoordinates helioCoords = EclipticCoordinates.of(lonProj, helioLat);
        return new DatedPlanetInfo(daysSinceJ2010, radius, helioLon, radiusProj, helioCoords);
    }

    private static final class DatedPlanetInfo{
        private final double daysSinceJ2010;
        private final double distanceFromSun;
        private final double helioLon;
        private final double eclipticRadius;
        private final EclipticCoordinates heliocentric;

        public DatedPlanetInfo(double daysSinceJ2010, double distanceFromSun, double helioLon, double eclipticRadius, EclipticCoordinates heliocentric) {
            this.daysSinceJ2010 = daysSinceJ2010;
            this.distanceFromSun = distanceFromSun;
            this.helioLon = helioLon;
            this.eclipticRadius = eclipticRadius;
            this.heliocentric = heliocentric;
        }

        public double getDaysSinceJ2010() {
            return daysSinceJ2010;
        }

        public double getDistanceFromSun() {
            return distanceFromSun;
        }

        public double getHelioLon() {
            return helioLon;
        }

        public EclipticCoordinates getHeliocentricCoordinates() {
            return heliocentric;
        }

        public double getEclipticRadius() {
            return eclipticRadius;
        }
    }

}
