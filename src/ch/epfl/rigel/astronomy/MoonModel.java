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
    MOON();

    //Constants
    private static final double meanLon = Angle.ofDeg(91.929336);
    private static final double meanPeriLon = Angle.ofDeg(130.143076);
    private static final double ascNodeLon = Angle.ofDeg(291.682547);
    private static final double sinIncl = Math.sin(Angle.ofDeg(5.145396));
    private static final double cosIncl = Math.cos(Angle.ofDeg(5.145396));
    private static final double exc = 0.0549;
    private static final double theta0 = Angle.ofDeg(0.5181);

    @Override
    public Moon at(double daysSinceJ2010, EclipticToEquatorialConversion eclipticToEquatorialConversion) {
        //To return
         EquatorialCoordinates moonEqCoord;
         float moonAngularSize;
         float moonMagnitude = 0;
         float moonPhase;

        //Build Sun
         Sun currentSun = SunModel.SUN.at(daysSinceJ2010, eclipticToEquatorialConversion);
         double sunMeanAnomaly = currentSun.meanAnomaly();
         double sunGeocentricEclipticLon = currentSun.eclipticPos().lon();

        //Orbital longitude
         double meanOrbitalLongitude = Angle.ofDeg(13.1763966) * daysSinceJ2010 + meanLon;
         double meanAnomaly = meanOrbitalLongitude - Angle.ofDeg(0.1114041) * daysSinceJ2010 - meanPeriLon;

         double evection = Angle.ofDeg(1.2739) * Math.sin(2 * (meanOrbitalLongitude - sunGeocentricEclipticLon) - meanAnomaly);
         double sinAnomaly = Math.sin(sunMeanAnomaly);
         double annualCorrection = Angle.ofDeg(0.1858) * sinAnomaly;
         double correction3 = Angle.ofDeg(0.37) * sinAnomaly;
         double correctedAnomaly = meanAnomaly + evection - annualCorrection - correction3;

         double centerCorrection = Angle.ofDeg(6.2886) * Math.sin(correctedAnomaly);
         double correction4 = Angle.ofDeg(0.214) * Math.sin(2 * correctedAnomaly);
         double correctedOrbitalLon = meanOrbitalLongitude + evection + centerCorrection - annualCorrection + correction4;

         double variation = Angle.ofDeg(0.6583) * Math.sin(2 * (correctedOrbitalLon - sunGeocentricEclipticLon));
         double trueOrbitalLon = correctedOrbitalLon + variation;

        //Ecliptic position
         double meanAscNodeLon = ascNodeLon - Angle.ofDeg(0.0529539) * daysSinceJ2010;
         double correctedMeanAscNodeLon = meanAscNodeLon - Angle.ofDeg(0.16) * Math.sin(sunMeanAnomaly);

         double exp1 = Math.sin(trueOrbitalLon-correctedMeanAscNodeLon);
         double moonEclLon = Math.atan2((exp1 * cosIncl) , (Math.cos(trueOrbitalLon - correctedMeanAscNodeLon))) + correctedMeanAscNodeLon;
         double moonEclLat = Math.asin(exp1 * sinIncl);
         EclipticCoordinates eclipticPos = EclipticCoordinates.of(Angle.normalizePositive(moonEclLon), moonEclLat);
        moonEqCoord = eclipticToEquatorialConversion.apply(eclipticPos);

        //Moon phase
        moonPhase = (float) ((1- Math.cos(trueOrbitalLon-sunGeocentricEclipticLon)) / 2);

        //Moon angular size
         double rho = (1 - exc * exc) / (1 + exc * Math.cos(correctedAnomaly + centerCorrection));
        moonAngularSize = (float) (theta0 / rho);

        return new Moon(moonEqCoord, moonAngularSize, moonMagnitude, moonPhase);
    }


}
