package ch.epfl.rigel.gui;

import java.time.Duration;

/**
 * Named Time Accelerator enum
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public enum NamedTimeAccelerator {
    TIMES_1("1x", TimeAccelerator.continuous(1)),
    TIMES_30("30x", TimeAccelerator.continuous(30)),
    TIMES_300("300x", TimeAccelerator.continuous(300)),
    TIMES_3000("3000x", TimeAccelerator.continuous(3000)),
    DAY("jour", TimeAccelerator.discrete(60, Duration.ofDays(1))),
    SIDEREAL_DAY("jour sidéral", TimeAccelerator.discrete(60, Duration.ZERO.plusHours(23).plusMinutes(56).plusSeconds(4)));

    private final String name;
    private final TimeAccelerator accelerator;

    NamedTimeAccelerator(String name, TimeAccelerator accelerator) {
        this.name = name;
        this.accelerator = accelerator;
    }

    /**
     * Getter for the name of the NamedTimeAccelerator
     * @return the name of the NamedTimeAccelerator
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the TimeAccelerator of the NamedTimeAccelerator
     * @return the TimeAccelerator of the NamedTimeAccelerator
     */
    public TimeAccelerator getAccelerator() {
        return accelerator;
    }

    @Override
    public String toString() {
        return name;
    }

}
