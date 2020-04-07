package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.coordinates.EclipticCoordinates;
import ch.epfl.rigel.coordinates.EclipticToEquatorialConversion;
import ch.epfl.rigel.coordinates.EquatorialCoordinates;
import ch.epfl.rigel.math.Angle;

/**
 * Class which builds a Moon object at a given time
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public enum MoonModel implements CelestialObjectModel<Moon> {
    MOON(Angle.ofDeg(91.929336), Angle.ofDeg(130.143076), Angle.ofDeg(291.682547), Angle.ofDeg(5.145396), 0.0549);

    //Constants
    private final double meanLon;
    private final double meanPeriLon;
    private final double ascNodeLon;
    private final double orbitIncl;
    private final double exc;

    MoonModel(double meanLon, double meanPeriLon, double ascNodeLon, double orbitIncl, double exc) {
        this.meanLon = meanLon;
        this.meanPeriLon = meanPeriLon;
        this.ascNodeLon = ascNodeLon;
        this.orbitIncl = orbitIncl;
        this.exc = exc;
    }

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        //To return
        final EquatorialCoordinates moonEqCoord;
        final float moonAngularSize;
        final float moonMagnitude = 0;
        final float moonPhase;

        //Build Sun
        final Sun currentSun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
        final double sunMeanAnomaly = currentSun.meanAnomaly();
        final double sunGeocentricEclipticLon = currentSun.eclipticPos().lon();

        //Orbital longitude
        final double meanOrbitalLongitude = Angle.ofDeg(13.1763966) * daysSinceJ2010 + meanLon;
        final double meanAnomaly = meanOrbitalLongitude - Angle.ofDeg(0.1114041) * daysSinceJ2010 - meanPeriLon;

        final double evection = Angle.ofDeg(1.2739) * Math.sin(2 * (meanOrbitalLongitude - sunGeocentricEclipticLon) - meanAnomaly);
        final double annualCorrection = Angle.ofDeg(0.1858) * Math.sin(sunMeanAnomaly);
        final double correction3 = Angle.ofDeg(0.37) * Math.sin(sunMeanAnomaly);
        final double correctedAnomaly = meanAnomaly + evection - annualCorrection - correction3;

        final double centerCorrection = Angle.ofDeg(6.2886) * Math.sin(correctedAnomaly);
        final double correction4 = Angle.ofDeg(0.214) * Math.sin(2 * correctedAnomaly);
        final double correctedOrbitalLon = meanOrbitalLongitude + evection + centerCorrection - annualCorrection + correction4;

        final double variation = Angle.ofDeg(0.6583) * Math.sin(2 * (correctedOrbitalLon - sunGeocentricEclipticLon));
        final double trueOrbitalLon = correctedOrbitalLon + variation;

        //Ecliptic position
        final double meanAscNodeLon = ascNodeLon - Angle.ofDeg(0.0529539) * daysSinceJ2010;
        final double correctedMeanAscNodeLon = meanAscNodeLon - Angle.ofDeg(0.16) * Math.sin(sunMeanAnomaly);

        final double exp1 = Math.sin(trueOrbitalLon-correctedMeanAscNodeLon);
        final double moonEclLon = Math.atan2((exp1 * Math.cos(orbitIncl)) , (Math.cos(trueOrbitalLon - correctedMeanAscNodeLon))) + correctedMeanAscNodeLon;
        final double moonEclLat = Math.asin(exp1 * Math.sin(orbitIncl));
        final EclipticCoordinates eclipticPos = EclipticCoordinates.of(Angle.normalizePositive(moonEclLon), moonEclLat);
        moonEqCoord = eclipticToEquatorialConversion.apply(eclipticPos);

        //Moon phase
        moonPhase = (float) ((1- Math.cos(trueOrbitalLon-sunGeocentricEclipticLon)) / 2);

        //Moon angular size
        final double rho = (1 - exc * exc) / (1 + exc * Math.cos(correctedAnomaly + centerCorrection));
        moonAngularSize = (float) (Angle.ofDeg(0.5181) / rho);

        return new Moon(moonEqCoord, moonAngularSize, moonMagnitude, moonPhase);
    }


}
