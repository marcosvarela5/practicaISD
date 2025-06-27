package attendance;


import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlAttendanceDao {
    public Attendance create(Connection connection, Attendance attendance);

    public List<Attendance> employeeAttendance(Connection connection, String email, boolean justGoing) throws InputValidationException;

    public boolean userHasAnswered(Connection connection, Long eventId, String email);

    Attendance find(Connection connection, Long attendanceId) throws InstanceNotFoundException;

    void remove(Connection connection, Long attendanceId) throws InstanceNotFoundException;
}
