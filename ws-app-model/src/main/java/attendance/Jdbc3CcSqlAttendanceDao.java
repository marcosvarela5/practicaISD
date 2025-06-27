package attendance;

import es.udc.ws.util.exceptions.InputValidationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;

public class Jdbc3CcSqlAttendanceDao extends AbstractSqlAttendanceDao {

    @Override
    public Attendance create(Connection connection, Attendance attendance) {

        String query = "INSERT INTO Attendance"
                + " (eventId, email, answer,"
                + " checkInTime) VALUES (?, ?, ?, ?)";

        try(PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)){

            int field = 1;
            preparedStatement.setLong(field++, attendance.getEventId());
            preparedStatement.setString(field++, attendance.getEmail());
            preparedStatement.setBoolean(field++, attendance.getAnswer());
            preparedStatement.setTimestamp(field++, Timestamp.valueOf(attendance.getCheckInTime()));

            int modifiedRows = preparedStatement.executeUpdate();
            if (modifiedRows == 0) {
                throw new SQLException("No added rows");
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(!resultSet.next()){
                throw new SQLException("JDBC driver did not return generated key");
            }

            Long attendanceId = resultSet.getLong(1);
            return new Attendance(attendanceId, attendance.getEventId(), attendance.getEmail(), attendance.getAnswer(), attendance.getCheckInTime());

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }




}
