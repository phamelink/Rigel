package ch.epfl.rigel.gui;

import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;

import java.time.ZonedDateTime;

public final class TimeAnimator extends AnimationTimer {
    private final DateTimeBean bean;
    private final ZonedDateTime initialDateTime;
    private TimeAccelerator accelerator;
    private final SimpleBooleanProperty running;
    private long initialTime;

    public TimeAnimator(DateTimeBean bean) {
        this.bean = bean;
        initialDateTime = bean.getZonedDateTime();
        running = new SimpleBooleanProperty();
        initialTime = 0;
    }


    /**
     * This method needs to be overridden by extending classes. It is going to
     * be called in every frame while the {@code AnimationTimer} is active.
     *
     * @param now The timestamp of the current frame given in nanoseconds. This
     *            value will be the same for all {@code AnimationTimers} called
     *            during one frame.
     */
    @Override
    public void handle(long now) {
        if (initialTime == 0) {
            initialTime = now;
        } else {
            ZonedDateTime newTime = accelerator.adjust(initialDateTime, Math.abs(now - initialTime));
            bean.setZonedDateTime(newTime);
            System.out.println(newTime);
        }
    }

    @Override
    public void start() {
        super.start();
        setRunning(true);
    }

    @Override
    public void stop() {
        super.stop();
        setRunning(false);
    }

    public void setAccelerator(TimeAccelerator accelerator) { this.accelerator = accelerator; }

    public ReadOnlyBooleanProperty getRunningProperty() {
        return running;
    }

    public boolean getRunning() { return running.get(); }

    private void setRunning(boolean newRunning) {
        running.set(newRunning);
    }






}
