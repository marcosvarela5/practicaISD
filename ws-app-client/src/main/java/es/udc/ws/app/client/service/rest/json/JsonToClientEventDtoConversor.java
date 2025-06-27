package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientEventDtoConversor {
    public static ObjectNode toObjectNode(ClientEventDto eventDto) throws IOException {

        ObjectNode eventObject = JsonNodeFactory.instance.objectNode();
        long between = ChronoUnit.HOURS.between(eventDto.getCelebrationDate(), eventDto.getEndDate());
        if (eventDto.getEventId() != null) {
            eventObject.put("eventId", eventDto.getEventId());
        }
        eventObject.put("name", eventDto.getName()).
                put("description", eventDto.getDescription()).
                put("celebrationDate", eventDto.getCelebrationDate().toString()).
                put("duration", between).
                put("activo", eventDto.isActivo()).
                put("going", eventDto.getGoing()).
                put("total", eventDto.getTotal());

        return eventObject;
    }

    public static ClientEventDto toClientEventDto(InputStream jsonEvent) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvent);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientEventDto(rootNode);
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    public static List<ClientEventDto> toClientEventDtos(InputStream jsonEvents) throws ParsingException {
        try {

            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvents);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode eventArray = (ArrayNode) rootNode;
                List<ClientEventDto> eventDtos = new ArrayList<>(eventArray.size());
                for (JsonNode eventNode : eventArray) {
                    eventDtos.add(toClientEventDto(eventNode));
                }
                return eventDtos;
            }
        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

    private static ClientEventDto toClientEventDto(JsonNode eventNode) throws ParsingException {
        if (eventNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode eventObject = (ObjectNode) eventNode;

            JsonNode eventIdNode = eventObject.get("eventId");
            Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

            String name = eventObject.get("name").textValue().trim();
            String description = eventObject.get("description").textValue().trim();
            LocalDateTime celebrationDate = LocalDateTime.parse(eventObject.get("celebrationDate").textValue());
            int duration = eventObject.get("duration").intValue();
            LocalDateTime endDate = celebrationDate.plusHours(duration);
            boolean activo = eventObject.get("activo").booleanValue();
            int going = eventObject.get("going").intValue();
            int total = eventObject.get("total").intValue();

            return new ClientEventDto(eventId, name, description, celebrationDate, endDate, activo, going, total);
        }
    }

}
