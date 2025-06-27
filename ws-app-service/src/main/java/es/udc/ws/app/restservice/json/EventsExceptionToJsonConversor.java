package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import eventservice.EventService;
import eventservice.exceptions.AlreadyAnsweredException;
import eventservice.exceptions.AttendanceOutOfTimeException;
import eventservice.exceptions.EventAlreadyCanceledException;
import eventservice.exceptions.EventNotCancelableBecauseDateException;

public class EventsExceptionToJsonConversor {
    public static ObjectNode toEventAlreadyCanceledException(EventAlreadyCanceledException ex){
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errortype", "EventAlreadyCancelled");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

        return exceptionObject;
    }

    public static ObjectNode toEventNotCancelableBecauseDateException(EventNotCancelableBecauseDateException ex){
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errortype", "EventNotCancelableBecauseDate");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        exceptionObject.put("celebrationDate", (ex.getCelebrationDate() != null) ? ex.getCelebrationDate().toString() : null);

        return exceptionObject;
    }

    public static ObjectNode toAttendanceOutOfTimeException(AttendanceOutOfTimeException ex){
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errortype", "AttendanceOutOfTime");
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);
        exceptionObject.put("celebrationDate", (ex.getCelebrationDate() != null) ? ex.getCelebrationDate().toString() : null);

        return exceptionObject;
    }

    public static ObjectNode toAlreadyAnsweredException(AlreadyAnsweredException ex){
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errortype", "AlreadyAnswered");
        exceptionObject.put("email", (ex.getEmail() != null) ? ex.getEventId() : null);
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

        return exceptionObject;
    }

    public static ObjectNode toAAlreadyAnsweredException(AlreadyAnsweredException ex){
        ObjectNode exceptionObject = JsonNodeFactory.instance.objectNode();

        exceptionObject.put("errortype", "AlreadyAnswered");
        exceptionObject.put("email", (ex.getEmail() != null) ? ex.getEventId() : null);
        exceptionObject.put("eventId", (ex.getEventId() != null) ? ex.getEventId() : null);

        return exceptionObject;
    }



}
