package attendance;

import java.time.LocalDateTime;
import java.util.Objects;

public class Attendance {

    private Long attendanceId;

    private Long eventId;

    private String email;

    private boolean answer;

   private LocalDateTime checkInTime;


    public Attendance(Long eventId, String email, boolean answer) {
        this.eventId = eventId;
        this.email = email;
        this.answer = answer;

    }
    public Attendance(Long attendanceId, Long eventId, String email, boolean answer){
        this(eventId, email, answer);
        this.attendanceId = attendanceId;
    }
    public Attendance(Long eventId, String email, boolean answer, LocalDateTime checkInTime){
        this(eventId, email, answer);
        this.checkInTime = checkInTime;
    }
    public Attendance(Long attendanceId, Long eventId, String email, boolean answer, LocalDateTime checkInTime){
        this(eventId, email, answer, checkInTime);
        this.attendanceId = attendanceId;
    }

    public Long getAttendanceId() {
        return attendanceId;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEmail() {
        return email;
    }

    public boolean getAnswer() {
        return answer;
    }

    public LocalDateTime getCheckInTime() {
        return checkInTime;
    }

    public void setAttendanceId(Long attendanceId) {
        this.attendanceId = attendanceId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }

    public void setCheckInTime(LocalDateTime checkInTime) {
        this.checkInTime = (checkInTime != null) ? checkInTime.withNano(0): null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Attendance that = (Attendance) o;
        return Objects.equals(attendanceId, that.attendanceId) && Objects.equals(eventId, that.eventId) && Objects.equals(email, that.email) && Objects.equals(answer, that.answer) && Objects.equals(checkInTime, that.checkInTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(attendanceId, eventId, email, answer, checkInTime);
    }


}
