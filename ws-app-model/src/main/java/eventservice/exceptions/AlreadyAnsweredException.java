package eventservice.exceptions;

public class AlreadyAnsweredException extends Exception{

    private Long eventId;
    private String email;

    public AlreadyAnsweredException(String email, Long eventId){
        super("User with email=\"" + email + "\n has already answered to event with id=\"" + eventId + "\n.");
        this.email = email;
        this.eventId=eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
