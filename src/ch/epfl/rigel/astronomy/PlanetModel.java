package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * Planet Model enum
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
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

    /**
     * Planet model enum constructor
     * @param name name
     * @param revTime revolution time (in tropical years)
     * @param jLon longitude at J2010
     * @param periLon longitude at perigee
     * @param exc orbital excentricity
     * @param axis semi major axis of the orbit
     * @param incl inclination of the oribit at the ecliptic
     * @param ascNodeLon longitude of the ascending node
     * @param theta0 angular size in AU
     * @param magnitude magnitude
     * @param isOuterPlanet true if the planet oribits further away around the sun than the earth,
     *                      false if it orbits at same distance as or less than the earth around the sun
     */
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
        final double R = earthInfo.getDistanceFromSun();
        final double L = earthInfo.getHelioLon();

        final double r = planetInfo.getDistanceFromSun();
        final double l = planetInfo.getHelioLon();
        final double rP = planetInfo.getEclipticRadius();
        final double lP = planetInfo.getHeliocentricCoordinates().lon();
        final double helioLat = planetInfo.getHeliocentricCoordinates().lat();

        /*
        A verifier si ça doit être des coordonnées écliptiques transformées en equatorial
        pour la mettre dans new planet ou laisser comme t'as fait
         */
        final EclipticCoordinates geocentricCoord;

        final double lambda;
        final double expression1 = R * Math.sin(lP - L);
        if(isOuterPlanet){
            //Outer Planets
            lambda = lP + Math.atan2(expression1 , (rP - R * Math.cos(lP - L)));
        }else{
            //Inner Planets
            lambda = Math.PI + L + Math.atan2(rP * Math.sin(L - lP) , (R - rP * Math.cos(L - lP)));
        }

        final double beta = Math.atan(rP * Math.tan(helioLat) * Math.sin(lambda - lP) / expression1);

        System.out.println("lambda : " + Angle.toDeg(Angle.normalizePositive(lambda)));
        System.out.println("lambda : " + Angle.toDeg(beta));

        geocentricCoord = EclipticCoordinates.of(Angle.normalizePositive(lambda), beta);

        final double rho = Math.sqrt(Math.abs(R*R + r*r - 2 * R * r * Math.cos(l - L) * Math.cos(helioLat)));
        final double angularSize = theta0 / rho;

        final double phase = ((1 + Math.cos(planetInfo.getHeliocentricCoordinates().lon() - l)) / 2);
        final double apparentMagnitude = magnitude + 5 * Math.log10(r*rho / Math.sqrt(phase));
        return new Planet(name, eclipticToEquatorialConversion.apply(geocentricCoord), (float) angularSize, (float) apparentMagnitude);
    }

    //MEMO : WARNING, epoch is J2010, not J2000!
    public DatedPlanetInfo getDatedPlanetInfo(double daysSinceJ2010){
        double meanAnom = (Angle.TAU * daysSinceJ2010) / (365.242191 * revTime) + jLon - periLon;
        meanAnom = Angle.normalizePositive(meanAnom);
        System.out.println("Mp : " + Angle.toDeg(meanAnom));
        double trueAnom = meanAnom + 2 * exc * Math.sin(meanAnom);
        trueAnom = Angle.normalizePositive(trueAnom);
        System.out.println("vp : " + Angle.toDeg(trueAnom));

        final double radius = axis * (1-exc*exc) / (1 + exc * Math.cos(trueAnom));
        double helioLon = trueAnom + periLon;
        helioLon = Angle.normalizePositive(helioLon);
        System.out.println("l : " + Angle.toDeg(helioLon));
        System.out.println("r : " + radius);

        final double helioLat = Math.asin(Math.sin(helioLon - ascNodeLon) * Math.sin(incl));

        System.out.println("trident : " + Angle.toDeg(helioLat));

        final double radiusProj = radius * Math.cos(helioLat);
        final double lonProj = Math.atan2(Math.sin(helioLon - ascNodeLon) * Math.cos(incl), Math.cos(helioLon-ascNodeLon)) + ascNodeLon;

        System.out.println("rp : " + radiusProj);
        System.out.println("lp : " + Angle.toDeg(lonProj));

        final EclipticCoordinates helioCoords = EclipticCoordinates.of(Angle.normalizePositive(lonProj), helioLat);
        return new DatedPlanetInfo(daysSinceJ2010, radius, helioLon, radiusProj, helioCoords);
    }

    public static final class DatedPlanetInfo{
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
