package ch.epfl.rigel.gui;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * DateTimeBean
 *
 * @author Philip Hamelink (311769)
 * @author Malo Ranzetti (296956)
 */
public final class DateTimeBean {
    private final ObjectProperty<LocalDate> date;
    private final ObjectProperty<LocalTime> time;
    private final ObjectProperty<ZoneId> zoneId;

    /**
     * DateTimeBean constructor, initializing all properties to null
     */
    public DateTimeBean () {
        this.date = new SimpleObjectProperty<>(null);
        this.time = new SimpleObjectProperty<>(null);
        this.zoneId = new SimpleObjectProperty<>(null);
    }

    /**
     * DateTimeBean parametrized instance constructor
     */
    public DateTimeBean (ZonedDateTime when) {
        this();
        setZonedDateTime(when);
    }

    /**
     * Setter for new ZonedDateTime
     * @param zonedDateTime new zoned date time to set
     */
    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        setDate(zonedDateTime.toLocalDate());
        setTime(zonedDateTime.toLocalTime());
        setZoneId(zonedDateTime.getZone());
    }

    /**
     * Getter for date property
     * @return date property
     */
    public ObjectProperty<LocalDate> dateProperty() { return date; }

    /**
     * Getter for date
     * @return date
     */
    public LocalDate getDate() { return date.get(); }

    /**
     * Setter for date with newDate
     * @param newDate new date to set
     */
    public void setDate(LocalDate newDate) { date.set(newDate);}

    /**
     * Getter for time property
     * @return time property
     */
    public ObjectProperty<LocalTime> timeProperty() { return time; }

    /**
     * Getter for time
     * @return time
     */
    public LocalTime getTime() { return time.get(); }

    /**
     * Setter for time with newTime
     * @param newTime new time to set
     */
    public void setTime(LocalTime newTime) { time.set(newTime);}

    /**
     * Getter for zone ID property
     * @return zone ID property
     */
    public ObjectProperty<ZoneId> zoneIdProperty() { return zoneId; }

    /**
     * Getter for zone ID
     * @return zone ID
     */
    public ZoneId getZoneId() { return zoneId.get(); }

    /**
     * Setter for zone ID with newZoneId
     * @param newZoneId new zone ID to set
     */
    public void setZoneId(ZoneId newZoneId) { zoneId.set(newZoneId);}

    /**
     * Getter for ZonedDateTime
     * @return ZonedDateTime
     */
    public ZonedDateTime getZonedDateTime() {
        return ZonedDateTime.of(getDate(), getTime(), getZoneId());
    }



}
