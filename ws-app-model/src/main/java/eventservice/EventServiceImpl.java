package eventservice;

import attendance.Attendance;
import attendance.SqlAttendanceDao;
import attendance.SqlAttendanceDaoFactory;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;
import event.Event;
import event.SqlEventDao;
import event.SqlEventDaoFactory;
import eventservice.exceptions.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.APP_DATA_SOURCE;

public class EventServiceImpl implements EventService {

    private final DataSource dataSource;
    private final SqlEventDao eventDao;
    private final SqlAttendanceDao attendanceDao;


    public EventServiceImpl() {
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        eventDao = SqlEventDaoFactory.getDao();
        attendanceDao = SqlAttendanceDaoFactory.getDao();
    }

    private static void validateCelebrationDate(LocalDateTime celebrationDate) throws InputValidationException {
        if (celebrationDate == null) {
            throw new InputValidationException("Celebration date can't be null.");
        }
    }

    private static void validateDuration(int duration) throws InputValidationException {
        if (duration == 0) {
            throw new InputValidationException("Duration can't be null.");
        }
        if (duration < 0) {
            throw new InputValidationException("Duration can't be negative.");
        }
    }
    private static void validatePeriod(LocalDateTime start, LocalDateTime finish) throws InputValidationException{
        if(start == null || finish == null){
            throw new InputValidationException("None of the dates can be null.");
        }
        if(finish.isBefore(start)){
            throw new InputValidationException("The end date can't be before the start date");
        }
    }
    private static void validateGoing(int going) throws InputValidationException{
        if(going < 0){
            throw new InputValidationException("Going people can't be negative.");
        }
    }
    private static void validateNotGoing(int notGoing) throws InputValidationException{
        if(notGoing < 0){
            throw new InputValidationException("Not going people can't be negative.");
        }
    }

    private void validateEvent(Event event) throws InputValidationException {
        PropertyValidator.validateMandatoryString("name", event.getName());
        PropertyValidator.validateMandatoryString("description", event.getDescription());
        validateCelebrationDate(event.getCelebrationDate());
        validateDuration(event.getDuration());
        validateGoing(event.getGoing());
        validateNotGoing(event.getNotGoing());
    }

    public static void validateEmail(String email) throws InputValidationException {
        final String err = "Invalid email format, it must be user@domain.ld";
        String format = "^[\\w-_.+]*[\\w-_.]@(\\w+\\.)+\\w+\\w$";
        if (email.matches(format)) {
            return;
        }
        throw new InputValidationException(err);
    }


    @Override
    public Event addEvent(Event event) throws InputValidationException {

        validateEvent(event);
        event.setCreationDate(LocalDateTime.now());
        event.setActivo(true);
        event.setGoing(0);
        event.setNotGoing(0);
        try (Connection connection = dataSource.getConnection()) {
            try {
                /* Prepara conexion */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Hace el trabajo */
                Event createdEvent = eventDao.create(connection, event);
                /* Commit */
                connection.commit();
                return createdEvent;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, EventAlreadyCanceledException, EventNotCancelableBecauseDateException {
        try (Connection connection = dataSource.getConnection()){
            try {
                /* Prepara conexion */
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                /* Hace el trabajo */
                Event event = eventDao.findEvent(connection, eventId);
                //mirar excepciones concretas
                if (!eventDao.findEvent(connection, eventId).getActivo()) {
                    throw new EventAlreadyCanceledException(eventId);
                }
                if (eventDao.findEvent(connection, eventId).getCelebrationDate().isBefore(LocalDateTime.now())) {
                    throw new EventNotCancelableBecauseDateException(eventId, event.getCelebrationDate());
                }
                boolean activo = false;
                event.setActivo(activo);
                eventDao.update(connection, event);
                /* Commit */
                connection.commit();
            } catch (InstanceNotFoundException |EventNotCancelableBecauseDateException | EventAlreadyCanceledException e) {
                connection.commit();
                throw e;
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Attendance addAttendance(String email, Long eventId, Boolean answer) throws InputValidationException, InstanceNotFoundException,
            AttendanceOutOfTimeException, AlreadyAnsweredException, EventAlreadyCanceledException {

        validateEmail(email);
        try (Connection connection = dataSource.getConnection()){

            try{
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                Event event = eventDao.findEvent(connection, eventId);
                if(LocalDateTime.now().plusHours(24).compareTo(event.getCelebrationDate()) > 0){
                    throw new AttendanceOutOfTimeException(eventId, event.getCelebrationDate());
                }
                if(!event.getActivo()){
                    throw new EventAlreadyCanceledException(eventId);
                }
                if(attendanceDao.userHasAnswered(connection, eventId, email)){
                    throw new AlreadyAnsweredException(email, eventId);
                }
                Attendance createdAttendance = attendanceDao.create(connection, new Attendance(eventId,email, answer, LocalDateTime.now().withNano(0)));

                if(answer){
                    int newGoing = event.getGoing() + 1;
                    event.setGoing(newGoing);
                }
                else{
                    int newNotGoing = event.getNotGoing() + 1;
                    event.setNotGoing(newNotGoing);
                }

                eventDao.update(connection, event);

                connection.commit();
                return createdAttendance;
            } catch (InstanceNotFoundException e){
                connection.commit();
                throw e;
            }catch (SQLException | RuntimeException | Error e){
                connection.rollback();
                throw e;
            }
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    @Override
    public Event findEvent(Long eventId) throws InstanceNotFoundException {
        try (Connection connection = dataSource.getConnection()) {
            return eventDao.findEvent(connection, eventId);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Event> findEvents(LocalDateTime start, LocalDateTime finish, String keyword) throws InputValidationException {
        try (Connection connection = dataSource.getConnection()) {
            validatePeriod(start, finish);
            if(keyword != null){
                PropertyValidator.validateMandatoryString("keyword", keyword);
            }
           return eventDao.findEvents(connection,start,finish, keyword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Attendance> employeeAttendance(String email, Boolean justGoing) throws InputValidationException {
        try(Connection connection = dataSource.getConnection()){
            if(email == null) throw new InputValidationException("The email can't be null.");
            if(justGoing == null)throw new InputValidationException("You have to specify which type of answer you are looking for.");
            validateEmail(email);
            return attendanceDao.employeeAttendance(connection, email, justGoing);
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

}
