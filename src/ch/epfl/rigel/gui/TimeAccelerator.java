package ch.epfl.rigel.gui;

import ch.epfl.rigel.astronomy.SiderealTime;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAmount;
import java.util.concurrent.TimeUnit;

/**
 * Time accelerator interface
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
@FunctionalInterface
public interface TimeAccelerator {

    /**
     * Returns simulated time
     * @param initialSimulatedTime initial simulated time
     * @param realTimeElapsed real time elapsed since the beginning of the simulation
     *                       which is the value of the difference of the real actual time and real initial time
     *                       (t - t0), given in nanoseconds
     * @return simulated time
     */
    ZonedDateTime adjust(ZonedDateTime initialSimulatedTime, long realTimeElapsed);

    /**
     * returns a continuous time accelerator according to the acceleration given
     * @param acceleration acceleration
     * @return a continuous time accelerator according to the acceleration given
     */
    static TimeAccelerator continuous(long acceleration) {
        return (initialSimulatedTime, realTimeElapsed) ->
                initialSimulatedTime.plusNanos(acceleration * realTimeElapsed);
    }

    /**
     * returns a discrete time acceleration according to the advancement frequency and the discrete step of simulated time
     * @param v advancement frequency of simulated time
     * @param step discrete step of simulated time
     * @return a discrete time acceleration according to the advancement frequency and the discrete step of simulated time
     */
    static TimeAccelerator discrete(int v, Duration step) {
        return (initialSimulatedTime, realTimeElapsed) ->
                initialSimulatedTime.plus(step.multipliedBy(Math.round(v * realTimeElapsed)));
    }
}
