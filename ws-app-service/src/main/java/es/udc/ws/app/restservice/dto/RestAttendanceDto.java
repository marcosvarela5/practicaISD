package es.udc.ws.app.restservice.dto;

public class RestAttendanceDto {
    private Long attendanceId;
    private Long eventId;
    private String email;
    private boolean answer;

    public RestAttendanceDto(Long attendanceId, Long eventId, String email, boolean answer){
        this.attendanceId = attendanceId;
        this.eventId = eventId;
        this.email = email;
        this.answer = answer;
    }

    public Long getAttendanceId() { return attendanceId; }
    public void setAttendanceId(Long attendanceId) { this.attendanceId = attendanceId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean getAnswer() { return answer; }
    public void setAnswer(boolean answer) { this.answer = answer; }

}
