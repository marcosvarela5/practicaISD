package event;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;
import es.udc.ws.util.exceptions.InstanceNotFoundException;


public interface SqlEventDao {
    public Event create(Connection connection, Event event);

    //Por si cancelamos un evento. Podería pasárselle o id do evento solo

    void update(Connection connection, Event event) throws InstanceNotFoundException;

    public Event findEvent(Connection connection, Long eventId) throws InstanceNotFoundException;

    public List<Event> findEvents(Connection connection, LocalDateTime start, LocalDateTime finish, String keyword);

    //Para las pruebas:
    public void remove(Connection connection, Long EventId)
        throws InstanceNotFoundException;
}
