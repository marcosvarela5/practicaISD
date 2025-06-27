package event;

import java.sql.*;

public class Jdbc3CcSqlEventDao extends AbstractSqlEventDao {

    @Override
    public Event create(Connection connection, Event event) {

        /* Crea el "queryString" */
        String queryString = "INSERT INTO Event" +
                "(name, description, celebrationDate, duration, creationDate, activo, going, notGoing)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString,
                Statement.RETURN_GENERATED_KEYS)){

            /* Rellena el "preparedStatement" */
            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setInt(i++, event.getDuration());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate()));
            preparedStatement.setBoolean(i++, event.getActivo());
            preparedStatement.setInt(i++, event.getGoing());
            preparedStatement.setInt(   i++, event.getNotGoing());

            /* Ejecuta la query */
            preparedStatement.executeUpdate();

            /* Coge el identificador generado */
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()){
                throw new SQLException("El driver JDBC no devolvio key generada");
            }
            Long eventId = resultSet.getLong(1);
            /* Devuelve el evento */
            return new Event(eventId, event.getName(), event.getDescription(), event.getCelebrationDate(),
                    event.getDuration(), event.getCreationDate(), event.getActivo(), event.getGoing(), event.getNotGoing());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
