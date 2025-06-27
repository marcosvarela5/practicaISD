package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientAttendanceDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientAttendanceDtoConversor {

    public static ClientAttendanceDto toClientAttendanceDto(InputStream jsonAttendance) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonAttendance);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                JsonNode attendanceIdNode = eventObject.get("attendanceId");
                Long attendanceId = (attendanceIdNode != null) ? attendanceIdNode.longValue() : null;

                long eventId = eventObject.get("eventId").longValue();
                String email = eventObject.get("email").textValue().trim();
                boolean answer = eventObject.get("answer").booleanValue();

                return new ClientAttendanceDto(attendanceId, eventId, email, answer);
            }
        } catch (ParsingException exception) {
            throw exception;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientAttendanceDto> toClientAttendaceDtos(InputStream jsonAttendances) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonAttendances);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode attendanceArray = (ArrayNode) rootNode;
                List<ClientAttendanceDto> attendanceDtos = new ArrayList<>(attendanceArray.size());
                for (JsonNode attendanceNode : attendanceArray) {
                    attendanceDtos.add(toclientAttendanceDto(attendanceNode));
                }
                return attendanceDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static ClientAttendanceDto toclientAttendanceDto(JsonNode attendaceNode) throws ParsingException{
        if(attendaceNode.getNodeType() != JsonNodeType.OBJECT){
            throw new ParsingException("Unrecognized JSON (object expected)");
        }else{
            ObjectNode attendanceObject = (ObjectNode) attendaceNode;

            JsonNode attendaceIdNode = attendanceObject.get("attendanceId");
            Long attendanceId = ( attendaceIdNode != null) ? attendaceIdNode.longValue() :null;

            Long eventId = attendanceObject.get("eventId").longValue();
            String email = attendanceObject.get("email").textValue().trim();
            Boolean answer = attendanceObject.get("answer").booleanValue();

            return new ClientAttendanceDto(attendanceId, eventId, email, answer);
        }
    }
}
