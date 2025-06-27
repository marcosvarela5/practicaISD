package es.udc.ws.app.client.service.exceptions;

public class ClientEventAlreadyCanceledException extends Exception{
    private Long eventId;

    public ClientEventAlreadyCanceledException(Long eventId) {
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
