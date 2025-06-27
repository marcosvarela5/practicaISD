package es.udc.ws.app.restservice.dto;

import attendance.Attendance;
import event.Event;

import java.util.ArrayList;
import java.util.List;

public class AttendanceToRestAttendanceDtoConversor {

    public static List<RestAttendanceDto> toRestAttendanceDto(List<Attendance> attendances){
        List<RestAttendanceDto> attendanceDtos = new ArrayList<>(attendances.size());
        for(int i = 0; i < attendances.size(); i++){
            Attendance attendance = attendances.get(i);
            attendanceDtos.add(toRestAttendanceDto(attendance));
        }
        return attendanceDtos;
    }
    public static RestAttendanceDto toRestAttendanceDto(Attendance attendance){
        return new RestAttendanceDto(attendance.getAttendanceId(), attendance.getEventId(), attendance.getEmail(), attendance.getAnswer());
    }

    public static Attendance toAttendance(RestAttendanceDto attendace){
        return new Attendance(attendace.getEventId(), attendace.getEventId(), attendace.getEmail(), attendace.getAnswer());
    }
}
