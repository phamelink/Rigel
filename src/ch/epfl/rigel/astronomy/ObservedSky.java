package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;
import ch.epfl.rigel.coordinates.*;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;

public class ObservedSky {

    private final Sun sunAtTime;
    private final CartesianCoordinates sunPosition;
    private final Moon moonAtTime;
    private final CartesianCoordinates moonPosition;
    private final List<Planet> planetsAtTime;
    private final List<Double> planetCoordinates; //TODO if possible switch to double primitive type to accelerate animations
    private final List<Star> starsAtTime;
    private final List<Double> starCoordinates;

    private final HashMap<CelestialObject, CartesianCoordinates> positionMap;
    private final TreeMap<Double, CelestialObject> distanceMap;

    private final StarCatalogue catalogue;

    public ObservedSky(ZonedDateTime when, GeographicCoordinates where,
                       StereographicProjection projection, StarCatalogue catalogue) {

        this.catalogue = catalogue;
        double daysUntil = Epoch.J2010.daysUntil(when);

        EclipticToEquatorialConversion eclConv = new EclipticToEquatorialConversion(when);
        EquatorialToHorizontalConversion eqConv = new EquatorialToHorizontalConversion(when, where);
        Function<EquatorialCoordinates, CartesianCoordinates> toCartesian = eqConv.andThen(projection);

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
        this.starCoordinates = new ArrayList<>();
        for(Star star : catalogue.stars()){
            starsAtTime.add(star);
            CartesianCoordinates coordinates = toCartesian.apply(star.equatorialPos());
            starCoordinates.add(coordinates.x());
            starCoordinates.add(coordinates.y());
            registerObject(star, coordinates);
        }


    }

    //Private functions
    private void registerObject(CelestialObject obj, CartesianCoordinates cord){
        positionMap.put(obj, cord);
        distanceMap.put(cord.norm(), obj);
    }


    //Public functions

    public Sun sun(){return sunAtTime;}

    public CartesianCoordinates sunPosition(){return sunPosition;}

    public Moon moon(){return moonAtTime;}

    public CartesianCoordinates moonPosition(){return moonPosition;}

    public List<Planet> planets(){return Collections.unmodifiableList(planetsAtTime);}

    public List<Double> planetCoordinates(){return Collections.unmodifiableList(planetCoordinates);}

    public List<Star> stars(){return Collections.unmodifiableList(starsAtTime);}

    public List<Double> starCoordinates(){return Collections.unmodifiableList(starCoordinates);}

    public Set<Asterism> asterisms(){return catalogue.asterisms();}

    public List<Integer> asterismIndex(Asterism asterism){return catalogue.asterismIndices(asterism);}

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
