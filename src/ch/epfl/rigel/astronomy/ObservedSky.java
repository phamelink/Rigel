package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

/**
 * Observed Sky
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public class ObservedSky {

    private final Sun sunAtTime;
    private final CartesianCoordinates sunPosition;
    private final Moon moonAtTime;
    private final CartesianCoordinates moonPosition;
    private final List<Planet> planetsAtTime;
    private final List<Double> planetCoordinates; //TODO if possible switch to double primitive type to accelerate animations
    private final List<Star> starsAtTime;
    private final double[] starCoordinates;

    private final TreeMap<Double, CelestialObject> xMap;
    private final TreeMap<Double, CelestialObject> yMap;
    private final HashMap<CelestialObject, CartesianCoordinates> positionMap;
    private final TreeMap<Double, CelestialObject> distanceMap;

    private final StarCatalogue catalogue;

    /**
     * Observed sky constructor
     * @param when the time of observation
     * @param where the position of observation
     * @param projection the projection to use
     * @param catalogue the catalogue of stars and asterisms
     */
    public ObservedSky(ZonedDateTime when, GeographicCoordinates where,
                       StereographicProjection projection, StarCatalogue catalogue) {

        this.catalogue = catalogue;
        double daysUntil = Epoch.J2010.daysUntil(when);

        EclipticToEquatorialConversion eclConv = new EclipticToEquatorialConversion(when);
        EquatorialToHorizontalConversion eqConv = new EquatorialToHorizontalConversion(when, where);
        Function<EquatorialCoordinates, CartesianCoordinates> toCartesian = eqConv.andThen(projection);

        this.xMap = new TreeMap<>();
        this.yMap = new TreeMap<>();
        this.positionMap = new HashMap<>();
        this.distanceMap = new TreeMap<>();

        this.sunAtTime = SunModel.SUN.at(daysUntil,eclConv);
        this.moonAtTime = MoonModel.MOON.at(daysUntil, eclConv);
        this.sunPosition = toCartesian.apply(sunAtTime.equatorialPos());
        this.moonPosition = toCartesian.apply(moonAtTime.equatorialPos());
        registerObject(sunAtTime, sunPosition);
        registerObject(moonAtTime, moonPosition);

        this.planetsAtTime = new ArrayList<>();
        this.planetCoordinates = new ArrayList<>();
        for(PlanetModel planetModel : PlanetModel.values()){
            if(planetModel.equals(PlanetModel.EARTH)) continue;
            Planet planet = planetModel.at(daysUntil, eclConv);
            planetsAtTime.add(planet);
            CartesianCoordinates coordinates = toCartesian.apply(planet.equatorialPos());
            planetCoordinates.add(coordinates.x());
            planetCoordinates.add(coordinates.y());
            registerObject(planet, coordinates);
        }

        this.starsAtTime = new ArrayList<>();
        this.starCoordinates = new double[catalogue.stars().size() * 2];
        int index = 0;
        for(Star star : catalogue.stars()){
            starsAtTime.add(star);
            CartesianCoordinates coordinates = toCartesian.apply(star.equatorialPos());
            starCoordinates[index] = coordinates.x();
            starCoordinates[index + 1] = coordinates.y();
            index = index + 2;
            registerObject(star, coordinates);
        }




    }

    //Private functions
    private void registerObject(CelestialObject obj, CartesianCoordinates cord){
        positionMap.put(obj, cord);
        distanceMap.put(cord.norm(), obj);
    }


    //Public functions

    /**
     * returns sun at time
     * @return sun at time
     */
    public Sun sun(){return sunAtTime;}

    /**
     * returns the sun's position in cartesian coordinates
     * @return the sun's position in cartesian coordinates
     */
    public CartesianCoordinates sunPosition(){return sunPosition;}

    /**
     * returns moon at time
     * @return moon at time
     */
    public Moon moon(){return moonAtTime;}

    /**
     * returns the moon's position in cartesian coordinates
     * @return the moon's position in cartesian coordinates
     */
    public CartesianCoordinates moonPosition(){return moonPosition;}

    /**
     * returns a list of all the planets at time
     * @return a list of all the planets at time
     */
    public List<Planet> planets(){return Collections.unmodifiableList(planetsAtTime);}

    /**
     * returns a list of the planets' coordinates (position 0 and 1 represent the cartesian coordinates x and y of first planet, 2 and 3 the x and y of second planet, etc...)
     * @return a list of the planets' coordinates
     */
    public List<Double> planetCoordinates(){return Collections.unmodifiableList(planetCoordinates);}

    /**
     * returns a list of all the stars at time
     * @return a list of all the stars at time
     */
    public List<Star> stars(){return Collections.unmodifiableList(starsAtTime);}

    /**
     * returns a list of the stars' coordinates (position 0 and 1 represent the cartesian coordinates x and y of first star, 2 and 3 the x and y of second star, etc...)
     * @return a list of the stars' coordinates
     */

    //TODO copy or not copy?
    public double[] starCoordinates(){
        return starCoordinates;}

    /**
     * returns a set of all asterisms
     * @return a set of all asterisms
     */
    public Set<Asterism> asterisms(){return catalogue.asterisms();}

    /**
     * returns list of indices of the stars composing the given asterism
     * @param asterism asterism to use to get indices of its stars
     * @return list of indices of the stars composing the given asterism
     */
    public List<Integer> asterismIndex(Asterism asterism){return catalogue.asterismIndices(asterism);}

    /**
     * returns optional which is either empty or containing nearest celestial object from given point in a given radius
     * @param point point in cartesian coordinates to obtain nearest object from
     * @param distance maximal distance from point in which to search for nearest object
     * @return empty Optional if no objects within radius or an optional containing nearest object from point
     */
    public Optional<CelestialObject> objectClosestTo(CartesianCoordinates point, double distance){

        /*Filter out any celestial object which could not be near the point by creating submap from all object coordinates
        which are present in the intersection of a ball of radius ||point||-distance and a ball of radius
         ||point||+distance around the origin. Filter operation in O(1), and greatly reduces possible candidates.

         Then, keep only element with smallest distance from point that is smaller than or equal to distance.
         */
        double minimalDistance = distance;
        CelestialObject toReturn = null;
        for(CelestialObject object : this.distanceMap.subMap(point.norm()-distance, point.norm()+distance).values()){

            CartesianCoordinates coord = positionMap.get(object);
            double d = Math.sqrt(Math.pow(coord.x()-point.x(), 2) + Math.pow(coord.y()-point.y(), 2));
            if(d <= minimalDistance){
                minimalDistance = d;
                toReturn = object;
            }
        }

        if(toReturn == null) {
            return Optional.empty();
        }else{
            return Optional.of(toReturn);
        }

    }

}