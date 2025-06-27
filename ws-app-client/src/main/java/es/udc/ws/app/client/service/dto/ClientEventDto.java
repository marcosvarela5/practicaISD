package es.udc.ws.app.client.service.dto;

import java.time.LocalDateTime;

public class ClientEventDto {
    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime celebrationDate;
    private LocalDateTime endDate;
    private boolean activo;
    private int going;
    private int total;

    public ClientEventDto(Long eventId, String name, String description, LocalDateTime celebrationDate, LocalDateTime endDate){
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.endDate = endDate;
    }

    public ClientEventDto(Long eventId, String name, String description, LocalDateTime celebrationDate, LocalDateTime endDate, boolean activo, int going, int total) {
        this(eventId, name, description, celebrationDate, endDate);
        this.activo = activo;
        this.going = going;
        this.total = total;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(LocalDateTime celebrationDate) {
        this.celebrationDate = celebrationDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getGoing() {
        return going;
    }

    public void setGoing(int going) {
        this.going = going;
    }

    public int getTotal(){return total;}

    public void setTotal(int total){this.total = total;}

    @Override
    public String toString() {
        return "{" +
                "eventId=" + eventId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", celebrationDate='" + celebrationDate + '\'' +
                ", endDate=" + endDate +
                ", activo=" + activo +
                ", going=" + going +
                ", total=" + total +
                '}';
    }
}
