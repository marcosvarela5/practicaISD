package es.udc.ws.app.restservice.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class RestEventDto {
    private Long eventId;
    private String name;
    private String description;
    private String celebrationDate;
    private int duration;
    private boolean activo;

    private int going;
    private int total;

    public RestEventDto(Long eventId, String name, String description, String celebrationDate, int duration,
                        boolean activo, int going, int total){
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.duration = duration;
        this.activo = activo;
        this.going = going;
        this.total = total;
    }

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCelebrationDate() { return celebrationDate; }
    public void setCelebrationDate(String celebrationDate) {this.celebrationDate = celebrationDate; }
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    public boolean getActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public int getGoing() {
        return going;
    }
    public void setGoing(int going) {
        this.going = going;
    }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }

    @Override
    public String toString() {
        return "RestEventDto{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", celebrationDate=" + celebrationDate +
                ", duration=" + duration +
                ", activo=" + activo +
                ", going=" + going +
                ", total=" + total +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestEventDto that = (RestEventDto) o;
        return duration == that.duration && activo == that.activo && going == that.going && total == that.total && Objects.equals(eventId, that.eventId) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(celebrationDate, that.celebrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, name, description, celebrationDate, duration, activo, going, total);
    }
}
