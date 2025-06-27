package eventservice.exceptions;

public class EventAlreadyCanceledException extends Exception{
    private Long eventId;

    public EventAlreadyCanceledException(Long eventId) {
        super("Event with id = " + eventId + " has already been canceled.");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

}
