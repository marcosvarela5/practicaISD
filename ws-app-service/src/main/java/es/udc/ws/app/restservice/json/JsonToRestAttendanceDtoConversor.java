package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestAttendanceDto;

import java.util.List;

public class JsonToRestAttendanceDtoConversor {

    public static ObjectNode toObjectNode(RestAttendanceDto attendance) {

        ObjectNode attendanceNode = JsonNodeFactory.instance.objectNode();

        if (attendance.getAttendanceId() != null) {
            attendanceNode.put("attendanceId", attendance.getAttendanceId());
        }
        attendanceNode.put("attendanceId", attendance.getAttendanceId()).
                put("eventId", attendance.getEventId()).
                put("email", attendance.getEmail()).
                put("answer", attendance.getAnswer());

        return attendanceNode;
    }

    public static ArrayNode toArrayNode(List<RestAttendanceDto> attendances) {

        ArrayNode attendancesNode = JsonNodeFactory.instance.arrayNode();
        for (int i = 0; i < attendances.size(); i++) {
            RestAttendanceDto attendanceDto = attendances.get(i);
            ObjectNode attendanceObject = toObjectNode(attendanceDto);
            attendancesNode.add(attendanceObject);
        }
        return attendancesNode;
    }
}
