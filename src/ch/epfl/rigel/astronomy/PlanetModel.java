package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.math.Angle;

import java.util.List;


/**
 * Planet Model enum
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public enum PlanetModel implements CelestialObjectModel<Planet>{
    MERCURY("Mercure", 0.24085, 75.5671, 77.612, 0.205627,
            0.387098, 7.0051, 48.449, 6.74, -0.42),
    VENUS("Vénus", 0.615207, 272.30044, 131.54, 0.006812,
            0.723329, 3.3947, 76.769, 16.92, -4.40),
    EARTH("Terre", 0.999996, 99.556772, 103.2055, 0.016671,
            0.999985, 0, 0, 0, 0),
    MARS("Mars", 1.880765, 109.09646, 336.217, 0.093348,
            1.523689, 1.8497, 49.632, 9.36, -1.52),
    JUPITER("Jupiter", 11.857911, 337.917132, 14.6633, 0.048907,
            5.20278, 1.3035, 100.595, 196.74, -9.40),
    SATURN("Saturne", 29.310579, 172.398316, 89.567, 0.053853,
            9.51134, 2.4873, 113.752, 165.60, -8.88),
    URANUS("Uranus", 84.039492, 356.135400, 172.884833, 0.046321,
            19.21814, 0.773059, 73.926961, 65.80, -7.19),
    NEPTUNE("Neptune", 165.84539, 326.895127, 23.07, 0.010483,
            30.1985, 1.7673, 131.879, 62.20, -6.87);

    private final String name;
    private final double jLon;
    private final double periLon;
    private final double exc;
    private final double axis;
    private final double incl;
    private final double ascNodeLon;
    private final double theta0;
    private final double magnitude;
    private final double revTimeCorrected;
    private final double sinIncl;
    private final double cosIncl;

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
     *
     */
    PlanetModel(String name, double revTime, double jLon, double periLon, double exc, double axis, double incl, double ascNodeLon,
                double theta0, double magnitude) {
        this.name = name;
        this.jLon = Angle.ofDeg(jLon);
        this.periLon = Angle.ofDeg(periLon);
        this.exc = exc;
        this.axis = axis;
        this.incl = Angle.ofDeg(incl);
        this.ascNodeLon = Angle.ofDeg(ascNodeLon);
        this.theta0 = theta0;
        this.magnitude = magnitude;
        this.revTimeCorrected = Angle.TAU / (365.242191 * revTime);
        this.sinIncl = Math.sin(incl);
        this.cosIncl = Math.cos(incl);
    }

    @Override
    public Planet at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        final double R = EARTH.getRadiusFromSunAt(daysSinceJ2010);
        final double L = EARTH.getHelioLonAt(daysSinceJ2010);

        final double r = this.getRadiusFromSunAt(daysSinceJ2010);
        final double l = this.getHelioLonAt(daysSinceJ2010);
        final double lP = this.getHelioCoordsAt(daysSinceJ2010).lon();
        final double helioLat = this.getHelioCoordsAt(daysSinceJ2010).lat();
        final double rP = r * Math.cos(helioLat);
        System.out.println(r + " ¦ " +l + " ¦ " +rP + " ¦ " +lP + " ¦ " +helioLat + " ¦ " +R + " ¦ " +L + " ¦ " );
        final EclipticCoordinates geocentricCoord;

        final double lambda;
        final double expression1 = R * Math.sin(lP - L);
        if(axis >= 1){
            //Outer Planets
            lambda = lP + Math.atan2(expression1 , (rP - R * Math.cos(lP - L)));
        }else{
            //Inner Planets
            lambda = Math.PI + L + Math.atan2(rP * Math.sin(L - lP) , (R - rP * Math.cos(L - lP)));
        }

        final double beta = Math.atan(rP * Math.tan(helioLat) * Math.sin(lambda - lP) / expression1);

        geocentricCoord = EclipticCoordinates.of(Angle.normalizePositive(lambda), beta);

        final double rho = Math.sqrt(Math.abs(R*R + r*r - 2 * R * r * Math.cos(l - L) * Math.cos(helioLat)));

        final double angularSize = theta0 / rho;

        final double phase = ((1 + Math.cos(Angle.normalizePositive(lambda) - l)) / 2);
        final double apparentMagnitude = magnitude + 5 * Math.log10(r*rho / Math.sqrt(phase));
        return new Planet(name, eclipticToEquatorialConversion.apply(geocentricCoord), (float) Angle.ofArcsec(angularSize), (float) apparentMagnitude);
    }

    /**
     * Get the radius from the sun to body at time
     * @param daysSinceJ2010 time since J2010
     * @return the radius
     */
    private double getRadiusFromSunAt(double daysSinceJ2010){
        return axis * (1-exc*exc) / (1 + exc * Math.cos(getTrueAnomalyAt(daysSinceJ2010)));
    }

    /**
     * Get the heliocentric longitude from the sun to body at time
     * @param daysSinceJ2010 time since J2010
     * @return heliocentric longitude
     */
    private double getHelioLonAt(double daysSinceJ2010){
        return Angle.normalizePositive(getTrueAnomalyAt(daysSinceJ2010) + periLon);
    }

    /**
     * Get the true anomaly of the body at time
     * @param daysSinceJ2010 time since J2010
     * @return true anomaly
     */
    private double getTrueAnomalyAt(double daysSinceJ2010){
        double meanAnom = revTimeCorrected * daysSinceJ2010 + jLon - periLon;
        meanAnom = Angle.normalizePositive(meanAnom);
        System.out.println( meanAnom);
        double trueAnom = meanAnom + 2 * exc * Math.sin(meanAnom);
        return Angle.normalizePositive(trueAnom);
    }


    /**
     * A method used to generate useful information about a planet and its heliocentric position in the solar system
     *
     * @param  daysSinceJ2010 : (double) amount of days elapsed since J2010 epoch
     * @return : (DatedPlanetInfo) Data package containing useful planet information for a given time.
     */
    private EclipticCoordinates getHelioCoordsAt(double daysSinceJ2010){

        double helioLon = getHelioLonAt(daysSinceJ2010);

        final double helioLat = Math.asin(Math.sin(helioLon - ascNodeLon) * sinIncl);

        final double lonProj = Math.atan2(Math.sin(helioLon - ascNodeLon) * cosIncl, Math.cos(helioLon-ascNodeLon)) + ascNodeLon;

        return EclipticCoordinates.of(Angle.normalizePositive(lonProj), helioLat);
    }



}
