package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientAttendanceDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientAttendanceOutOfTimeException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCanceledException;
import es.udc.ws.app.client.service.exceptions.ClientEventNotCancelableBecauseDateException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDateTime;
import java.util.List;

public interface ClientEventService {
    public Long addEvent(ClientEventDto event) throws InputValidationException;

    public void cancelEvent(Long eventId)throws InstanceNotFoundException, ClientEventAlreadyCanceledException, ClientEventNotCancelableBecauseDateException;

    public Long addAttendance(String email, Long eventId, Boolean going) throws InputValidationException, InstanceNotFoundException, ClientAttendanceOutOfTimeException, ClientAlreadyAnsweredException, ClientEventAlreadyCanceledException;

    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException;

    public List<ClientEventDto> findEvents(LocalDateTime finish, String keyword) throws InputValidationException; //Non hai InstanceNotFoundException, devolve []

    public List<ClientAttendanceDto> employeeAttendance(String email, Boolean justGoing) throws InputValidationException; //Non hai InstanceNotFoundException, devolve []

}
