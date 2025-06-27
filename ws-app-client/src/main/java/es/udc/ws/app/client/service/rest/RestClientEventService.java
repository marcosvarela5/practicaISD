package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientAttendanceDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientAttendanceOutOfTimeException;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCanceledException;
import es.udc.ws.app.client.service.exceptions.ClientEventNotCancelableBecauseDateException;
import es.udc.ws.app.client.service.rest.json.JsonToClientAttendanceDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientEventDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.apache.http.HttpResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.util.List;

public class RestClientEventService implements ClientEventService {
    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";
    private String endpointAddress;


    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {
        try {
            HttpResponse response = Request.Post(getEndpointAddress() + "events").bodyStream(toInputStream(event),
                    ContentType.create("application/json")).execute().returnResponse();
            validateStatusCode(HttpStatus.SC_CREATED, response);
            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventId();
        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, ClientEventAlreadyCanceledException, ClientEventNotCancelableBecauseDateException {
        try {
            HttpResponse response = Request.Post(getEndpointAddress() + "events/" + eventId+ "/cancel")
                    .execute().returnResponse();
            validateStatusCode(HttpStatus.SC_OK, response);
        } catch (InstanceNotFoundException | ClientEventAlreadyCanceledException |
                 ClientEventNotCancelableBecauseDateException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long addAttendance(String email, Long eventId, Boolean going) throws InputValidationException, InstanceNotFoundException, ClientAttendanceOutOfTimeException, ClientAlreadyAnsweredException, ClientEventAlreadyCanceledException {
        try {
            HttpResponse response = Request.Post(getEndpointAddress() + "attendances").bodyForm(
                            Form.form().
                                    add("email", email).
                                    add("eventId", Long.toString(eventId)).
                                    add("going", Boolean.toString(going)).
                                    build()).
                    execute().returnResponse();
            validateStatusCode(HttpStatus.SC_CREATED, response);
            return JsonToClientAttendanceDtoConversor.toClientAttendanceDto(response.getEntity().getContent()).
                    getAttendanceId();
        } catch (InputValidationException | InstanceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventId) {
        try {

            HttpResponse response = Request.Get(getEndpointAddress() + "events/"+eventId).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEventDto> findEvents(LocalDateTime endDate, String keyword) throws InputValidationException {
        try {
            HttpResponse response;
            if (keyword == null) {
                response = Request.Get(getEndpointAddress() + "events?endDate=" + URLEncoder.encode(endDate.toString(), "UTF-8")).execute().returnResponse();
            } else {
                response = Request.Get(getEndpointAddress() + "events?endDate=" + URLEncoder.encode(endDate.toString(), "UTF-8") + "&keyword="
                        + URLEncoder.encode(keyword, "UTF-8")).execute().returnResponse();
            }
            validateStatusCode(HttpStatus.SC_OK, response);
            return JsonToClientEventDtoConversor.toClientEventDtos(response.getEntity().getContent());
        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientAttendanceDto> employeeAttendance(String email, Boolean justGoing) throws InputValidationException {
        try {
            HttpResponse response = Request.Get(getEndpointAddress() + "attendances?email=" + URLEncoder.encode(email, "UTF-8") + "&justGoing="
                    + URLEncoder.encode(String.valueOf(justGoing), "UTF-8")).execute().returnResponse();
            validateStatusCode(HttpStatus.SC_OK, response);
            return JsonToClientAttendanceDtoConversor.toClientAttendaceDtos(response.getEntity().getContent());
        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if (endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientEventDto event) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEventDtoConversor.toObjectNode(event));
            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception {
        try {
            int statusCode = response.getStatusLine().getStatusCode();
            /* Success? */
            if (statusCode == successCode) {
                return;
            }
            /* Handler error. */
            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND:
                    throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                            response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST:
                    throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                            response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN:
                    throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                            response.getEntity().getContent());
                default:
                    throw new RuntimeException("HTTP error; status code = " + statusCode);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
