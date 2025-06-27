package es.udc.ws.app.client.service.exceptions;

import java.time.LocalDateTime;

public class ClientAttendanceOutOfTimeException extends Exception{
    private Long eventId;
    private LocalDateTime celebrationDate;

    public ClientAttendanceOutOfTimeException(Long eventId, LocalDateTime celebrationDate) {
        super("Event with id=\"" + eventId + "\n is celebrating on\"" + celebrationDate + "\n. Less than 24 hours, you can not register for it.");
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
