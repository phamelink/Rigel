package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

import java.time.ZonedDateTime;

/**
 * Time animator
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class TimeAnimator extends AnimationTimer {
    private final DateTimeBean bean;
    private final SimpleBooleanProperty running;
    private ZonedDateTime initialDateTime;
    private TimeAccelerator accelerator;
    private long initialTime;

    /**
     * TimeAnimator contructor
     * @param bean DateTimeBean whose value the time animator will control to adjust the observation instant
     */
    public TimeAnimator(DateTimeBean bean) {
        this.bean = bean;
        running = new SimpleBooleanProperty();
    }


    /**
     * It is going to be called in every frame while the TimeAnimator is active.
     *
     * @param now The timestamp of the current frame given in nanoseconds. This
     *            value will be the same for all {@code AnimationTimers} called
     *            during one frame.
     */
    @Override
    public void handle(long now) {
        ZonedDateTime newTime = accelerator.adjust(initialDateTime, now - initialTime);
        bean.setZonedDateTime(newTime);
    }

    /**
     * Starts the TimeAnimator. Once it is started, the handle(long) method of this TimeAnimator will be called
     * in every frame. The TimeAnimator can be stopped by calling stop().
     */
    @Override
    public void start() {
        super.start();
        initialDateTime = bean.getZonedDateTime();
        initialTime = System.nanoTime();
        setRunning(true);
    }

    /**
     * Stops the TimeAnimator. It can be activated again by calling start().
     */
    @Override
    public void stop() {
        super.stop();
        setRunning(false);
    }

    /**
     * Sets accelerator to use
     * @param accelerator
     */
    public void setAccelerator(TimeAccelerator accelerator) { this.accelerator = accelerator; }

    /**
     * Return a ReadOnlyBooleanProperty of running
     * @return
     */
    public ReadOnlyBooleanProperty getRunningProperty() {
        return running;
    }

    /**
     * Returns the boolean value of running
     * @return the boolean value of running
     */
    public boolean getRunning() { return running.get(); }

    private void setRunning(boolean newRunning) {
        running.set(newRunning);
    }
}
