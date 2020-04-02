package ch.epfl.rigel.astronomy;

import ch.epfl.rigel.Preconditions;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Star Catalogue
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class StarCatalogue {
    private final List<Star> stars;

    //Map of asterism:
    // key->Asterism
    // value->List of integer that represent the indices of the stars composing them.
    private final Map<Asterism, List<Integer>> asterismMap;


    /**
     * Constructor for the StarCatalogue
     * @param stars list of stars of the catalogue
     * @param asterisms list of asterisms of the catalogue
     * @throws IllegalArgumentException if one of the asterisms contains a star not included is stars
     */
    public StarCatalogue(List<Star> stars, List<Asterism> asterisms) throws IllegalArgumentException{
        Map<Asterism, List<Integer>> map = new HashMap<>();
        for (Asterism asterism : asterisms) {
            List<Integer> indices = new ArrayList<>();
            for (Star star : asterism.stars()) {
                Preconditions.checkArgument(stars.contains(star));
                indices.add(stars.indexOf(star));
            }
            map.put(asterism, indices);
        }
        this.stars = List.copyOf(stars);
        this.asterismMap = Map.copyOf(map);
    }

    /**
     * returns list of stars of the catalogue
     * @return list of stars of the catalogue
     */
    public List<Star> stars() {
        return Collections.unmodifiableList(stars);
    }

    /**
     * returns set of asterisms of the catalogue
     * @return set of asterisms of the catalogue
     */
    public Set<Asterism> asterisms() {
        return Set.copyOf(asterismMap.keySet());
    }

    /**
     * retruns list of indices in the catalogue of the stars composing the given asterism
     * @param asterism asterism to use to get indices of its stars
     * @return list of indices of stars in asterism
     * @throws IllegalArgumentException if the asterism is not in the catalogue
     */
    public List<Integer> asterismIndices(Asterism asterism) {
        Preconditions.checkArgument(asterismMap.containsKey(asterism));
        return asterismMap.get(asterism);

    }

    /**
     * StarCatalogue.Builder
     */
    public final static class Builder {
        List<Star> stars;
        List<Asterism> asterisms;
        Map<Integer, Star> hipparcosIdMap;

        /**
         * Default Builder constructor
         */
        public Builder() {
            stars = new ArrayList<>();
            asterisms = new ArrayList<>();
            hipparcosIdMap = new HashMap<>();
        }


        /**
         * adds a star to the list of stars in future catalogue
         * returns builder with a catalogue of updated star list
         *
         * @param star star to be added
         * @return builder with a catalogue of updated star list
         */
        public Builder addStar(Star star) {
            stars.add(star);
            if (star.hipparcosId() != 0) hipparcosIdMap.put(star.hipparcosId(), star);
            return this;
        }

        /**
         * retruns an unmodifiable vue of the stars of the catalogue being built
         *
         * @return an unmodifiable vue of the stars of the catalogue being built
         */
        public List<Star> stars() {
            return Collections.unmodifiableList(stars);
        }

        /**
         * adds a asterism to the list of asterisms in future catalogue
         * returns builder with a catalogue of updated asterism list
         *
         * @param asterism asterism to be added
         * @return builder with a catalogue of updated asterism list
         */
        public Builder addAsterism(Asterism asterism) {
            asterisms.add(asterism);
            return this;
        }

        /**
         * retruns an unmodifiable vue of the asterisms of the catalogue being built
         *
         * @return an unmodifiable vue of the asterisms of the catalogue being built
         */
        public List<Asterism> asterisms() {
            return Collections.unmodifiableList(asterisms);
        }

        /**
         * asks the loader to add the stars/asterisms found in the inputStream to the catalogue
         *
         * @param inputStream inputstream containing new stars/asterisms
         * @param loader      loader to add stars/asterisms found in the inputstream
         * @return the builder with updated star/asterism lists
         * @throws IOException in case of error with Input/Output
         */
        public Builder loadFrom(InputStream inputStream, Loader loader) throws IOException {
            try (InputStream input = inputStream) {
                loader.load(inputStream, this);
                return this;
            }
        }

        /**
         * returns starCatalogue with stars and asterisms updated through builder
         *
         * @return starCatalogue with stars and asterisms updated through builder
         */
        public StarCatalogue build() {
            return new StarCatalogue(stars, asterisms);
        }

        /**
         * Get a Star from the catalog build by its hipparcos id
         *
         * @param id : (int) hipparcos id
         * @return (Star) corresponding star
         */

        public Star getStarById(int id) {
            if (!hipparcosIdMap.containsKey(id)) return null;
            return hipparcosIdMap.get(id);

        }

        /**
         * Builds a StarCatalogue given 2 filepaths for stars and asterisms.
         * @param starsFile : (String) Path of CSV star information
         * @param asterismsFile : (String) Path of asterism text file
         * @return (StarCatalogue) Catlogue build from the given files
         */
        public StarCatalogue buildFromFiles(String starsFile, String asterismsFile) {
            try (InputStream starStream = getClass().getResourceAsStream(starsFile);
                 InputStream asterismStream = getClass().getResourceAsStream(asterismsFile)) {

                return this.loadFrom(starStream, HygDatabaseLoader.INSTANCE)
                        .loadFrom(asterismStream, AsterismLoader.INSTANCE).build();

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }

    /**
     * loads stars/asterisms from inputStream and adds them to the star catalogue being built by builder
     */
    public interface Loader {
        public abstract void load(InputStream inputStream, Builder builder) throws IOException;
    }

}
