package event;


import java.time.LocalDateTime;
import java.util.Objects;


public class Event {

    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime celebrationDate;
    private int duration;
    private LocalDateTime creationDate;
    private boolean activo;
    private int going;
    private int notGoing;

    public Event(String name, String description, LocalDateTime celebrationDate, int duration){
        this.name = name;
        this.description = description;
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0) : null;
        this.duration = duration;
    }
    public Event(Long eventId, String name, String description, LocalDateTime celebrationDate, int duration){
        this(name, description, celebrationDate, duration);
        this.eventId = eventId;
    }
    public Event(Long eventId, String name, String description, LocalDateTime celebrationDate, int duration, LocalDateTime creationDate){
        this(eventId, name, description, celebrationDate, duration);
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
    }
    public Event(Long eventId, String name, String description, LocalDateTime celebrationDate, int duration,
                 LocalDateTime creationDate, boolean activo, int going, int notGoing) {
        this(eventId, name, description, celebrationDate, duration, creationDate);
        this.activo = activo;
        this.going = going;
        this.notGoing = notGoing;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public int getDuration() {
        return duration;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public Boolean getActivo() {
        return activo;
    }

    public int getGoing() {
        return going;
    }

    public int getNotGoing() {
        return notGoing;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCelebrationDate(LocalDateTime celebrationDate) {
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0): null;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public void setGoing(int going) {
        this.going = going;
    }

    public void setNotGoing(int notGoing) {
        this.notGoing = notGoing;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return Objects.equals(eventId, event.eventId) &&
                Objects.equals(name, event.name) &&
                Objects.equals(description, event.description) &&
                Objects.equals(celebrationDate, event.celebrationDate) &&
                Objects.equals(duration, event.duration) &&
                Objects.equals(creationDate, event.creationDate) &&
                Objects.equals(activo, event.activo) &&
                Objects.equals(going, event.going) &&
                Objects.equals(notGoing, event.notGoing);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, name, description, celebrationDate, duration, creationDate, activo, going, notGoing);
    }
}
