package eventservice.exceptions;

import event.Event;
import eventservice.EventService;

import java.time.LocalDateTime;

public class EventNotCancelableBecauseDateException extends Exception{
    private Long eventId;
    private LocalDateTime celebrationDate;

    public EventNotCancelableBecauseDateException(Long eventId, LocalDateTime celebrationDate) {
        super("Event with id = " + eventId + " can not be canceled, because celebration date has expired");
        this.eventId = eventId;
        this.celebrationDate = celebrationDate;
    }

    public Long getEventId() {
        return eventId;
    }


    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(LocalDateTime celebrationDate) {
        this.celebrationDate = celebrationDate;
    }
}
