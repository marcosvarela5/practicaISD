package attendance;

import es.udc.ws.util.exceptions.InstanceNotFoundException;
import event.Event;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlAttendanceDao implements SqlAttendanceDao{

    protected AbstractSqlAttendanceDao() {
    }

    @Override
    public boolean userHasAnswered(Connection connection, Long eventId, String email) {
        String query = "SELECT attendanceId FROM Attendance WHERE eventId = ? AND email = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setLong(1, eventId);
            preparedStatement.setString(2, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public List<Attendance> employeeAttendance(Connection connection, String email, boolean justGoing){
        String query = "SELECT attendanceId,eventId, email, answer, checkInTime" + " FROM Attendance " + "WHERE email= ?";
        if(justGoing){
            query += "AND answer = (1)";
        }
        query += "ORDER BY checkInTime";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            List<Attendance> userAnswers =new ArrayList<Attendance>();
            preparedStatement.setString(1, email);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {

                int j = 1;
                Long attendanceId = resultSet.getLong(j++);
                Long eventId = resultSet.getLong(j++);
                String userEmail = resultSet.getString(j++);
                boolean answer = resultSet.getBoolean(j++);
                Timestamp checkInTimeDateAsTimestamp = resultSet.getTimestamp(j++);
                LocalDateTime checkInTime = checkInTimeDateAsTimestamp.toLocalDateTime().withNano(0);

                userAnswers.add(new Attendance(attendanceId, eventId, userEmail, answer, checkInTime));

            }

            return userAnswers;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public Attendance find(Connection connection, Long attendanceId) throws InstanceNotFoundException{
        /* Crea el "queryString" */
        String queryString = "SELECT attendanceId,eventId, email, answer, checkInTime"
                + " FROM Attendance WHERE" + " attendanceId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            /* Rellena el "preparedStatement" */
            int i=1;
            preparedStatement.setLong(i++, attendanceId.longValue());
            /* Ejecuta la query */
            ResultSet resultSet = preparedStatement.executeQuery();
            if(!resultSet.next()){
                throw new InstanceNotFoundException(attendanceId, Attendance.class.getName());
            }

            Long eventId = resultSet.getLong(i++);
            String userEmail = resultSet.getString(i++);
            boolean answer = resultSet.getBoolean(i++);
            Timestamp checkInTimeDateAsTimestamp = resultSet.getTimestamp(i++);
            LocalDateTime checkInTime = checkInTimeDateAsTimestamp.toLocalDateTime().withNano(0);
            /*Devuelve el attend*/
            return new Attendance(attendanceId, eventId, userEmail, answer, checkInTime);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    public void remove(Connection connection, Long attendanceId)throws InstanceNotFoundException {
        /* Crea el "queryString" */
        String queryString = "DELETE FROM Attendance WHERE" + " attendanceId = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            /* Rellena el "preparedStatement" */
            int i = 1;
            preparedStatement.setLong(i++, attendanceId);
            /* Ejecuta la query */
            int removedRows = preparedStatement.executeUpdate();
            if (removedRows == 0){
                throw new InstanceNotFoundException(attendanceId, Event.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
