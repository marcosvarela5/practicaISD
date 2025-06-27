package es.udc.ws.app.restservice.dto;

import event.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventToRestEventDtoConversor {

    public static List<RestEventDto> toRestEventDto(List<Event> events){
        List<RestEventDto> eventDtos = new ArrayList<>(events.size());
        for(int i = 0; i < events.size(); i++){
            Event event = events.get(i);
            eventDtos.add(toRestEventDto(event));
        }
        return eventDtos;
    }
    public static RestEventDto toRestEventDto(Event event){
        return new RestEventDto(event.getEventId(), event.getName(), event.getDescription(), event.getCelebrationDate().toString(),
                event.getDuration(), event.getActivo(), event.getGoing(), (event.getGoing() + event.getNotGoing()));
    }

    public static Event toEvent (RestEventDto event){
        return new Event(event.getEventId(), event.getName(), event.getDescription(), LocalDateTime.parse(event.getCelebrationDate()),
                event.getDuration(), LocalDateTime.now(), event.getActivo(), event.getGoing(), (event.getTotal()- event.getGoing()));
    }
}
