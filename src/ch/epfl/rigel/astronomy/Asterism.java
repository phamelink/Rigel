package ch.epfl.rigel.astronomy;

import java.util.Collections;
import java.util.List;

public final class Asterism {
    private final List<Star> stars;

    public Asterism(List<Star> stars) {
        this.stars = List.copyOf(stars);
    }

    public List<Star> stars() {
        return Collections.unmodifiableList(this.stars);
    }
}
