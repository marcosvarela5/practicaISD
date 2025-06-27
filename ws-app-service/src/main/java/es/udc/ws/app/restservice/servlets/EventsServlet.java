package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import event.Event;
import eventservice.EventService;
import eventservice.EventServiceFactory;
import eventservice.exceptions.EventAlreadyCanceledException;
import eventservice.exceptions.EventNotCancelableBecauseDateException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventsServlet extends RestHttpServletTemplate {

    @Override
    protected  void processPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
            InputValidationException, InstanceNotFoundException {
        String path = request.getPathInfo();
        System.out.println(path);
        if(path==null || path.equals("/")){
            System.out.println("66666666666666666666666666666666");
            RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(request.getInputStream());
            Event event = EventToRestEventDtoConversor.toEvent(eventDto);
            event = EventServiceFactory.getService().addEvent(event);
            eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            String eventURL = ServletUtils.normalizePath(request.getRequestURL().toString() + "/" + event.getEventId());
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Location", eventURL);
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_CREATED,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
        }else if (path.matches("/\\d+/cancel")){
            String [] substrings = path.split("/");
            Long id = Long.parseLong(substrings[1]);
            System.out.println("i5555555555555555555555555555555555555");
            Event event = EventServiceFactory.getService().findEvent(id);
            try {
                EventServiceFactory.getService().cancelEvent(id);
            } catch (EventNotCancelableBecauseDateException | EventAlreadyCanceledException e) {
                e.printStackTrace();
            } catch (InstanceNotFoundException e) {
                throw new RuntimeException(e);
            }
            RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            String eventURL = ServletUtils.normalizePath(request.getRequestURL().toString() + "/" + id +"/cancel");
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Location", eventURL);
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
        }

    }

    @Override
    protected void processPut(HttpServletRequest request, HttpServletResponse response) throws IOException, InputValidationException,
            InstanceNotFoundException{
        Long eventId = ServletUtils.getIdFromPath(request, "events");
        RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(request.getInputStream());
        if (!eventId.equals(eventDto.getEventId())){
            throw new InputValidationException("Invalid Request: invalid eventId");
        }
        Event event = EventToRestEventDtoConversor.toEvent(eventDto);
        try {
            EventServiceFactory.getService().cancelEvent(event.getEventId());
        } catch (EventNotCancelableBecauseDateException | EventAlreadyCanceledException e) {
            e.printStackTrace();
        }
        ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_NO_CONTENT, null, null);
    }

    @Override
    protected void processGet(HttpServletRequest request, HttpServletResponse response) throws InputValidationException, IOException {
        String path = request.getPathInfo();
        System.out.println(path);
        if(path==null || path.equals("/")){
            LocalDateTime endDate = LocalDateTime.parse(request.getParameter("endDate"));
            String keyword = request.getParameter("keyword");
            List<Event> events = EventServiceFactory.getService().findEvents(LocalDateTime.now().withNano(0), endDate,keyword );
            List<RestEventDto> eventDtos = EventToRestEventDtoConversor.toRestEventDto(events);
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toArrayNode(eventDtos), null);
        }else{
          Long eventId = ServletUtils.getIdFromPath(request, "events");
          Event event;
          try{
              event = EventServiceFactory.getService().findEvent(eventId);
          } catch (InstanceNotFoundException e) {
              throw new RuntimeException(e);
          }
          RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
          ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK, JsonToRestEventDtoConversor.toObjectNode(eventDto), null);
      }
    }
}
