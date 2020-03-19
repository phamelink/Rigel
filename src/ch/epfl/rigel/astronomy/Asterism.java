package ch.epfl.rigel.astronomy;

import java.util.Collections;
import java.util.List;

/**
 * Asterism
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class Asterism {
    private final List<Star> stars;

    /**
     * Asterism constructor
     * Constructs an asterism composed of a list of given stars
     * @param stars
     *              given stars
     * @throws IllegalArgumentException if given list is empty
     */
    public Asterism(List<Star> stars) {
        this.stars = List.copyOf(stars);
    }

    /**
     * returns the list of stars
     * @return the list of stars
     */
    public List<Star> stars() {
        return Collections.unmodifiableList(this.stars);
    }
}
