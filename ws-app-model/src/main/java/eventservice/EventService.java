package eventservice;

import attendance.Attendance;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import eventservice.exceptions.*;
import event.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    public Event addEvent(Event event) throws InputValidationException;

    public void cancelEvent(Long eventId)throws InstanceNotFoundException, EventAlreadyCanceledException, EventNotCancelableBecauseDateException;

    public Attendance addAttendance(String email, Long eventId, Boolean going) throws InputValidationException, InstanceNotFoundException, AttendanceOutOfTimeException, AlreadyAnsweredException, EventAlreadyCanceledException;

    public Event findEvent(Long eventId) throws InstanceNotFoundException;

    public List<Event> findEvents(LocalDateTime start, LocalDateTime finish, String keyword) throws InputValidationException; //Non hai InstanceNotFoundException, devolve []

    public List<Attendance> employeeAttendance(String email, Boolean justGoing) throws InputValidationException; //Non hai InstanceNotFoundException, devolve []


}
