package event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public abstract class AbstractSqlEventDao implements SqlEventDao{

    protected AbstractSqlEventDao(){ }

    @Override
    public void update(Connection connection, Event event) throws InstanceNotFoundException {
        /* Crea el "queryString" */
        String queryString = "UPDATE Event"
                + " SET name = ?, description = ?, celebrationDate = ?, duration = ?, creationDate = ?, "
                + "activo = ?, going = ?, notGoing = ? WHERE eventId = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            /* Rellena el "preparedStatement" */
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setInt(i++, event.getDuration());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate()));
            preparedStatement.setBoolean(i++, event.getActivo());
            preparedStatement.setInt(i++, event.getGoing());
            preparedStatement.setInt(i++, event.getNotGoing());
            preparedStatement.setLong(i++, event.getEventId());
            /* Ejecuta la query */
            int updateRows = preparedStatement.executeUpdate();
            if(updateRows == 0){
                throw new InstanceNotFoundException(event.getEventId(), Event.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Event findEvent(Connection connection, Long eventId) throws InstanceNotFoundException {

            String query = "SELECT name, description, celebrationDate, duration, creationDate, activo, going, notGoing " +
                    "FROM Event" + " WHERE eventId=?";

            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                int field=1;
                preparedStatement.setLong(field++, eventId);

                ResultSet resultSet = preparedStatement.executeQuery();

                if(!resultSet.next()) throw new InstanceNotFoundException(eventId, Event.class.getName());

                //Get data
                field = 1;
                String name = resultSet.getString(field++);
                String description = resultSet.getString(field++);
                Timestamp timestamp = resultSet.getTimestamp(field++);
                LocalDateTime celebrationDate = timestamp.toLocalDateTime();
                int duration = resultSet.getInt(field++);
                Timestamp timestamp2 = resultSet.getTimestamp(field++);
                LocalDateTime creationDate = timestamp2.toLocalDateTime();
                boolean activo = resultSet.getBoolean(field++);
                int going = resultSet.getInt(field++);
                int notGoing = resultSet.getInt(field++);

                return new Event(eventId,name, description, celebrationDate, duration, creationDate, activo, going, notGoing);

            } catch (SQLException e){
                throw new RuntimeException(e);
            }
    }

    @Override
    public List<Event> findEvents(Connection connection, LocalDateTime start, LocalDateTime finish, String keyword) {
        String query = "SELECT eventId, name, description, celebrationDate, duration, creationDate, activo, going, notGoing " +
        "FROM Event" + " WHERE celebrationDate between ? AND ?";

        if(keyword != null){
            query += "AND LOWER(description) LIKE LOWER(?)";
        }

        query += "ORDER BY celebrationDate";

        try( PreparedStatement preparedStatement = connection.prepareStatement(query)){
            /* Fill "preparedStatement". */
            int i = 1;
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(start));
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(finish));

            if(keyword != null){
                preparedStatement.setString(i++, "%"+keyword+"%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Event> events = new ArrayList<>();

            while (resultSet.next()) {

                int j = 1;
                Long eventId = resultSet.getLong(j++);
                String name = resultSet.getString(j++);
                String description = resultSet.getString(j++);
                Timestamp celebrationDateAsTimestamp = resultSet.getTimestamp(j++);
                LocalDateTime celebrationDate = celebrationDateAsTimestamp.toLocalDateTime();
                int duration = resultSet.getInt(j++);
                Timestamp creationDateAsTimestamp = resultSet.getTimestamp(j++);
                LocalDateTime creationDate = creationDateAsTimestamp.toLocalDateTime();
                boolean activo = resultSet.getBoolean(j++);
                int going = resultSet.getInt(j++);
                int notGoing = resultSet.getInt(j++);

                events.add(new Event(eventId, name, description, celebrationDate, duration, creationDate, activo, going, notGoing));

            }

            return events;


        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long eventId) throws InstanceNotFoundException {
        /* Crea el "queryString" */
        String queryString = "DELETE FROM Event WHERE" + " eventId = ?";
        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){
            /* Rellena el "preparedStatement" */
            int i = 1;
            preparedStatement.setLong(i++, eventId);
            /* Ejecuta la query */
            int removedRows = preparedStatement.executeUpdate();
            if (removedRows == 0){
                throw new InstanceNotFoundException(eventId, Event.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
