package es.udc.ws.app.test.model.appservice;

import attendance.Attendance;
import attendance.SqlAttendanceDao;
import attendance.SqlAttendanceDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import event.Event;
import event.SqlEventDao;
import event.SqlEventDaoFactory;
import eventservice.EventService;
import eventservice.EventServiceFactory;
import eventservice.exceptions.AlreadyAnsweredException;
import eventservice.exceptions.AttendanceOutOfTimeException;
import eventservice.exceptions.EventAlreadyCanceledException;
import eventservice.exceptions.EventNotCancelableBecauseDateException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppServiceTest {

    private static EventService eventService = null;
    private static SqlEventDao eventDao = null;
    private static SqlAttendanceDao attendanceDao = null;
    private final long NON_EXISTENT_EVENT_ID = -1;
    private final String USER_EMAIL = "test@udc.es";
    @BeforeAll
    public static void init(){
        /* Crea un simple data source y lo añade a "DataSourceLocator
         * (necesario para el test) */
        DataSource dataSource = new SimpleDataSource();
        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);
        eventService = EventServiceFactory.getService();
        eventDao = SqlEventDaoFactory.getDao();
        attendanceDao = SqlAttendanceDaoFactory.getDao();
    }

    private Event getValidEvent(){
        LocalDateTime celebrationDate = LocalDateTime.now().plusDays(7).withNano(0);
        return new Event("Fiesta navidad", "Celebraremos una cena", celebrationDate,6);
    }
    private Event getValidEvent2(){
        LocalDateTime celebrationDate = LocalDateTime.now().plusDays(7).withNano(0);
        return new Event("Fiesta halloween", "Vamos a pedir chucherias", celebrationDate,5);
    }
    private Attendance getValidTrueAttendance(){
        Event event = createEvent(getValidEvent());
        return new Attendance(event.getEventId(), USER_EMAIL, true);
    }

    private Attendance getValidFalseAttendance(){
        Event event = createEvent(getValidEvent2());
        return new Attendance(event.getEventId(), USER_EMAIL, false);
    }
    private Event createEvent(Event event){
        Event addedEvent;
        try {
            addedEvent = eventService.addEvent(event);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedEvent;
    }

    private Attendance createAttendance(Attendance attendance){
        Attendance addedAttendance;
        try {
            addedAttendance = eventService.addAttendance(attendance.getEmail(), attendance.getEventId(), attendance.getAnswer());
        } catch (InputValidationException | AlreadyAnsweredException | InstanceNotFoundException |
                 AttendanceOutOfTimeException | EventAlreadyCanceledException e) {
            throw new RuntimeException(e);
        }
        return addedAttendance;
    }

    private void removeEvent(Long eventId){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Do work. */
                eventDao.remove(connection, eventId);
                /* Commit. */
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void removeAttendance(Long attendanceId){
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepare connection. */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Do work. */
                attendanceDao.remove(connection, attendanceId);
                /* Commit. */
                connection.commit();
            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Attendance findAttendance(Long attendanceId) throws InstanceNotFoundException{
        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        try (Connection connection = dataSource.getConnection()) {
            return attendanceDao.find(connection, attendanceId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testAddMovieAndFindEvent() throws InputValidationException, InstanceNotFoundException{
        Event event = getValidEvent();
        Event addedEvent = null;
        try {
            //crear evento
            addedEvent = eventService.addEvent(event);
            //buscar el evento
            Event foundEvent = eventService.findEvent(addedEvent.getEventId());
            assertEquals(addedEvent, foundEvent);
            assertEquals(foundEvent.getName(), event.getName());
            assertEquals(foundEvent.getDescription(), event.getDescription());
            assertEquals(foundEvent.getCelebrationDate(), event.getCelebrationDate());
            assertEquals(foundEvent.getDuration(), event.getDuration());
            assertEquals(foundEvent.getCreationDate(), event.getCreationDate());
            assertEquals(true, event.getActivo());
            assertEquals(0, event.getGoing());
            assertEquals(0, event.getNotGoing());
        } finally {
            //limpia base de datos
            if (addedEvent!=null){
                removeEvent(addedEvent.getEventId());
            }
        }
    }

    @Test
    public void testAddInvalidEvent(){
        //Comprobar nombre evento nulo
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setName(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar nombre evento vacio
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setName("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar descripcion evento nulo
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar descripcion evento vacio
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDescription("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar celebrationDate no nula
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setCelebrationDate(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar duracion no 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDuration(0);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar duracion > 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setDuration(-1);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar going >= 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setGoing(-1);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
        //Comprobar notgoing >= 0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent();
            event.setNotGoing(-1);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
    }

    @Test
    public void testFindEvent() throws InputValidationException {
        Event event = new Event("Comunion", "De mi sobrina", LocalDateTime.now().plusHours(20), 6);
        Event eventtest = eventService.addEvent(event);
        try{
            assertThrows(InstanceNotFoundException.class, () -> eventService.findEvent(NON_EXISTENT_EVENT_ID));
        } finally{
            removeEvent(eventtest.getEventId());
        }
    }

    @Test
    public void testFindEvents() {

        //Add events
        List<Event> events = new LinkedList<>();
        List<Event> expectedEvents = new LinkedList<>();
        Event event1 = getValidEvent();
        event1.setName("Event 1");
        event1.setDescription("Description");
        event1.setCelebrationDate(LocalDateTime.now().plusDays(3));
        event1 = createEvent(event1);
        events.add(event1);

        Event event2 = getValidEvent();
        event2.setName("Event 2");
        event2.setDescription("Reunion altos cargos");
        event2.setCelebrationDate(LocalDateTime.now().plusDays(5));
        event2 = createEvent(event2);
        events.add(event2);

        Event event3 = getValidEvent();
        event3.setName("Event 3");
        event3.setDescription("Description");
        event3.setCelebrationDate(LocalDateTime.now().plusDays(6));
        event3 = createEvent(event3);
        events.add(event3);

        try {
            LocalDateTime start = LocalDateTime.now().plusDays(2).withNano(0);
            LocalDateTime finish = LocalDateTime.now().plusDays(7).withNano(0);

            List<Event> foundEvents= eventService.findEvents(start, finish, "altos");
            expectedEvents.add(event2);
            assertEquals(expectedEvents, foundEvents);

            start = LocalDateTime.now().plusDays(4).withNano(0);
            finish = LocalDateTime.now().plusDays(7).withNano(0);
            expectedEvents.add(event3);
            foundEvents= eventService.findEvents(start, finish, null);
            assertEquals(expectedEvents,foundEvents);

        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        } finally {
            for (Event event : events) {
                removeEvent(event.getEventId());
            }
        }
    }

    @Test
    public void testInvalidFindEvents(){
        LocalDateTime start = LocalDateTime.now().plusDays(7).withNano(0);
        LocalDateTime finish = LocalDateTime.now().plusDays(2).withNano(0);
        // Start date after finish date
        assertThrows(InputValidationException.class, () -> eventService.findEvents(start, finish, "altos"));
        //Null dates
        assertThrows(InputValidationException.class, () -> eventService.findEvents(null, null, "altos"));

    }

    @Test
    public void testCancelEvent() throws InstanceNotFoundException, EventAlreadyCanceledException, EventNotCancelableBecauseDateException{
        Event event = createEvent(getValidEvent());
        try {
            eventService.cancelEvent(event.getEventId());
            Event eventupdated = eventService.findEvent(event.getEventId());
            assertEquals(false, eventupdated.getActivo());
        } finally {
            removeEvent(event.getEventId());
        }

    }
    @Test
    public void testCancelNonExistentEvent(){
        Event event = getValidEvent();
        event.setEventId(NON_EXISTENT_EVENT_ID);
        assertThrows(InstanceNotFoundException.class, () -> eventService.cancelEvent(event.getEventId()));
    }
    @Test
    public void testCancelEventAlreadyCanceled() throws InstanceNotFoundException, EventNotCancelableBecauseDateException, EventAlreadyCanceledException, InputValidationException {
        Event event = new Event("Comunion", "De mi sobrina", LocalDateTime.now().plusDays(5), 6);
        Event eventtest = eventService.addEvent(event);
        try {
            eventService.cancelEvent(eventtest.getEventId());
            assertThrows(EventAlreadyCanceledException.class, () -> eventService.cancelEvent(eventtest.getEventId()));
        } finally {
            removeEvent(eventtest.getEventId());
        }
    }
    @Test
    public void testCancelEventNotCancelableBecauseDate() throws InputValidationException {
        Event event = new Event("Boda", "De mi sobrina", LocalDateTime.now().minusDays(5), 6);
        Event eventtest = eventService.addEvent(event);
        assertThrows(EventNotCancelableBecauseDateException.class, () -> eventService.cancelEvent(eventtest.getEventId()));
        removeEvent(eventtest.getEventId());
    }

    @Test
    public void testMakeAttendance() throws InputValidationException, AlreadyAnsweredException, InstanceNotFoundException, AttendanceOutOfTimeException,
            EventAlreadyCanceledException{
        Event event = getValidEvent();
        Event addedEvent = null;
        Attendance addedAttendanceTrue = null;
        Attendance addedAttendanceFalse = null;
        try {
            //crear evento
            addedEvent = eventService.addEvent(event);
            addedAttendanceTrue = eventService.addAttendance("f.hermo@udc.es", addedEvent.getEventId(), true);
            addedAttendanceFalse = eventService.addAttendance("test@udc.es", addedEvent.getEventId(), false);
            Event foundEvent = eventService.findEvent(addedEvent.getEventId());
            Attendance foundAttendance = findAttendance(addedAttendanceTrue.getAttendanceId());
            assertEquals(addedAttendanceTrue, foundAttendance);
            assertEquals("f.hermo@udc.es", foundAttendance.getEmail());
            assertEquals(addedEvent.getEventId(), foundAttendance.getEventId());
            assertEquals(true, foundAttendance.getAnswer());
            assertEquals(1, foundEvent.getGoing());
            assertEquals(1, foundEvent.getNotGoing());
        } finally {
            if (addedAttendanceTrue!=null){
                removeAttendance(addedAttendanceTrue.getAttendanceId());
            }
            if (addedAttendanceFalse!=null){
                removeAttendance(addedAttendanceFalse.getAttendanceId());
            }
            if (addedEvent!=null){
                removeEvent(addedEvent.getEventId());
            }
        }
    }
    @Test
    public void cannotAnswerTwice() throws AlreadyAnsweredException, InstanceNotFoundException,
            AttendanceOutOfTimeException, InputValidationException, EventAlreadyCanceledException {
        Event event = createEvent(getValidEvent());
        Attendance attendance = getValidTrueAttendance();
        Attendance attendance2 = new Attendance(event.getEventId(), "test@udc.es",
                true);
        try{
            //Tienen el mismo email y el mismo eventId
            eventService.addAttendance(attendance.getEmail(), event.getEventId(), attendance.getAnswer());
            assertThrows(AlreadyAnsweredException.class, () ->
                    eventService.addAttendance(attendance2.getEmail(), attendance2.getEventId(), attendance2.getAnswer()));
        }finally {
            //No hace falta un removeAttendance ya que la restricción está definida como borrado en cascada -> borramos el event -> borramos sus attendance
            removeEvent(attendance2.getEventId());
            removeEvent(attendance.getEventId());
        }
    }

    @Test
    public void testEmployeeAttendance() throws InputValidationException{
        Attendance notGoing = getValidFalseAttendance();
        Attendance going = getValidTrueAttendance();
        Attendance other_user = new Attendance(going.getEventId(), "luis.mayan@udc.es", true);
        notGoing = createAttendance(notGoing);
        other_user = createAttendance(other_user);
        List<Attendance> attendances = new LinkedList<>();
        List<Attendance> expectedAttendances = new LinkedList<>();
        attendances.add(notGoing);
        attendances.add(other_user);
        try{
            assertEquals(expectedAttendances, eventService.employeeAttendance(USER_EMAIL,true));
            going = createAttendance(going);
            attendances.add(going);
            expectedAttendances.add(going);
            assertEquals(expectedAttendances, eventService.employeeAttendance(USER_EMAIL,true));
            expectedAttendances.add(notGoing);
            Collections.reverse(expectedAttendances);
            assertEquals(expectedAttendances, eventService.employeeAttendance(USER_EMAIL,false));

        }finally {
            removeEvent(going.getEventId());
            removeEvent(notGoing.getEventId());
        }
    }

    @Test
    public void InvalidEmployeeAttendace(){
        assertThrows(InputValidationException.class, () -> eventService.employeeAttendance("udc.es", true));
        assertThrows(InputValidationException.class, () -> eventService.employeeAttendance(null, false));
        assertThrows(InputValidationException.class, () -> eventService.employeeAttendance(USER_EMAIL, null));
    }

    @Test
    public void attendToACancelledEvent() throws InputValidationException, InstanceNotFoundException, EventNotCancelableBecauseDateException, EventAlreadyCanceledException {
        Event event = new Event("Comunion", "De mi sobrina", LocalDateTime.now().plusDays(5), 6);
        Event eventtest = eventService.addEvent(event);
        try{
            eventService.cancelEvent(eventtest.getEventId());
            assertThrows(EventAlreadyCanceledException.class, () ->{
                Attendance attendance = eventService.addAttendance(USER_EMAIL, eventtest.getEventId(), true);
                removeAttendance(attendance.getAttendanceId());
            });
        } finally {
            removeEvent(eventtest.getEventId());
        }
    }

    @Test
    public void attendOutOfTime() throws InputValidationException {
        Event event = new Event("Comunion", "De mi sobrina", LocalDateTime.now().plusHours(20), 6);
        Event eventtest = eventService.addEvent(event);
        assertThrows(AttendanceOutOfTimeException.class, () ->{
            Attendance attendance = eventService.addAttendance(USER_EMAIL, eventtest.getEventId(), true);
            removeAttendance(attendance.getAttendanceId());
        });
        removeEvent(eventtest.getEventId());
    }
}