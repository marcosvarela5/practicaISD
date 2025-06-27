package es.udc.ws.app.restservice.servlets;

import attendance.Attendance;
import es.udc.ws.app.restservice.dto.AttendanceToRestAttendanceDtoConversor;
import es.udc.ws.app.restservice.dto.RestAttendanceDto;
import es.udc.ws.app.restservice.json.JsonToRestAttendanceDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ExceptionToJsonConversor;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import eventservice.EventServiceFactory;
import eventservice.exceptions.AlreadyAnsweredException;
import eventservice.exceptions.AttendanceOutOfTimeException;
import eventservice.exceptions.EventAlreadyCanceledException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendancesServlet extends RestHttpServletTemplate {
    protected void processGet(HttpServletRequest req, HttpServletResponse resp) throws IOException,
            InputValidationException {
        String email = ServletUtils.getMandatoryParameter(req, "email");
        String justGoing = ServletUtils.getMandatoryParameter(req, "justGoing");
        List<Attendance> attendances;
        attendances = EventServiceFactory.getService().employeeAttendance(email, Boolean.parseBoolean(justGoing));

        List<RestAttendanceDto> attendanceDtos = AttendanceToRestAttendanceDtoConversor.toRestAttendanceDto(attendances);
        ServletUtils.writeServiceResponse(resp, HttpServletResponse.SC_OK,
                JsonToRestAttendanceDtoConversor.toArrayNode(attendanceDtos), null);
    }

    @Override
    protected void processPost(HttpServletRequest request, HttpServletResponse response) throws InputValidationException, IOException {
        ServletUtils.checkEmptyPath(request);

        Long eventId = ServletUtils.getMandatoryParameterAsLong(request, "eventId");
        String email = ServletUtils.getMandatoryParameter(request, "email");
        Boolean going = Boolean.parseBoolean(ServletUtils.getMandatoryParameter(request, "going"));

        Attendance attendance;

        try {
            attendance = EventServiceFactory.getService().addAttendance(email, eventId, going);
        } catch (AttendanceOutOfTimeException | AlreadyAnsweredException | InstanceNotFoundException
                 | EventAlreadyCanceledException exception) {
            throw new RuntimeException(exception);
        }

        RestAttendanceDto attendanceDto = AttendanceToRestAttendanceDtoConversor.toRestAttendanceDto(attendance);
        String resURL = ServletUtils.normalizePath(request.getRequestURL().toString() + "/" + attendance.getAttendanceId().toString());
        Map<String, String> headers = new HashMap<>(1);
        headers.put("Location", resURL);
        ServletUtils.writeServiceResponse
                (response, HttpServletResponse.SC_CREATED, JsonToRestAttendanceDtoConversor.toObjectNode(attendanceDto), headers);
    }
}
