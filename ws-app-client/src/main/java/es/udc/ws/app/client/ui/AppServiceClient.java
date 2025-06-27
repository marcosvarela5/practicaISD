package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.ClientEventServiceFactory;
import es.udc.ws.app.client.service.dto.ClientAttendanceDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientEventAlreadyCanceledException;
import es.udc.ws.app.client.service.exceptions.ClientEventNotCancelableBecauseDateException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.w3c.dom.events.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) throws InputValidationException {

        if (args.length == 0) {
            printUsageAndExit();
        }
        ClientEventService clientEventService = ClientEventServiceFactory.getService();

        //[add] AppServiceClient -addEvent <name> <description> <start_date> <end_date>

        if ("-addEvent".equalsIgnoreCase(args[0])) {
            validateArgs(args, 5, new int[]{});
            try {
                Long eventId = clientEventService.addEvent(new ClientEventDto(null, args[1], args[2],
                        LocalDateTime.parse(args[3]), LocalDateTime.parse(args[4])));
                System.out.println("Added successfully event with id: " + eventId);
                System.out.println("Id: " + eventId +
                        " Nombre: " + args[1] +
                        " Descripcion: " + args[2] +
                        " Fecha de inicio: " + args[3] +
                        " Fecha de fin: " + args[4]);
            } catch (NumberFormatException | InputValidationException e) {
                e.printStackTrace(System.err);
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
            //[cancel] AppServiceClient -cancel <eventId>
        }else if("-cancel".equalsIgnoreCase(args[0])){
            validateArgs(args, 2, new int[] {1});
            try {
                clientEventService.cancelEvent(Long.parseLong(args[1]));
                System.out.println("Event "+ args[1] + " canceled successfully");

            } catch (ClientEventAlreadyCanceledException | InstanceNotFoundException |
                     ClientEventNotCancelableBecauseDateException e) {
                e.printStackTrace(System.err);
            }catch (Exception e){
                e.printStackTrace(System.err);
            }

            //"[findEvent] AppServiceClient -findEvent <eventId>
        } else if ("-findEvent".equalsIgnoreCase(args[0])) {
            validateArgs(args, 2, new int[] {1});
            try {
                ClientEventDto event = clientEventService.findEvent(Long.parseLong(args[1]));
                System.out.println("Event found:");
                System.out.println(event.toString());

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }

        //"[findEvents] AppServiceClient -findEvents <untilDate> [<keyword>]
        }else if("-findEvents".equalsIgnoreCase(args[0])){
            String keyword = null;
            if(args.length == 3){
                validateArgs(args,3, new int[] {});
                keyword = args[2];
            }
            else if(args.length == 2){
                validateArgs(args,2, new int[] {});
            }
            else {
                printUsage();
            }
            try{
                List<ClientEventDto> events = clientEventService.findEvents(LocalDate.parse(args[1]).atTime(23,59,59), keyword);
                System.out.println("Events found:");
                for(ClientEventDto event: events){
                    System.out.println(event.toString());
                }
            } catch (InputValidationException e) {
                throw new InputValidationException("Invalid arguments");
            }

        //[respond] AppServiceClient -respond <userEmail> <eventId> <response>
        } else if ("-respond".equalsIgnoreCase(args[0])) {
            validateArgs(args, 4, new int[]{2});
            Long attendanceId;
            try {
                attendanceId = clientEventService.addAttendance
                        (args[1], Long.parseLong(args[2]), Boolean.parseBoolean(args[3]));
                System.out.println("Answered " + args[3] + " successfully to event " + args[2] + " with attendanceId = " + attendanceId);

            } catch (NumberFormatException | InstanceNotFoundException | InputValidationException exception) {
                exception.printStackTrace(System.err);
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        //[findResponses] AppServiceClient -findResponses <userEmail> <onlyAffirmative>
        }else if("-findresponses".equalsIgnoreCase(args[0])){
           validateArgs(args, 3, new int[] {});
           try {
               List<ClientAttendanceDto> attendances = clientEventService.employeeAttendance(args[1], Boolean.parseBoolean(args[2]));
               for(ClientAttendanceDto attendance: attendances){
                   System.out.println(attendance.toString());
               }
           } catch (InputValidationException e) {
               throw new RuntimeException(e);
           }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage() {
        System.err.println("Usage:\n" +
                "[add]                AppServiceClient      -addEvent <name> <description> <start_date> <end_date> \n" +
                "[respond]            AppServiceClient      -respond <userEmail> <eventId> <response> \n" +
                "[cancel]             AppServiceClient      -cancel <eventId> \n" +
                "[findEvents]         AppServiceClient      -findEvents <untilDate> [<keyword>] \n" +
                "[findEvent]          AppServiceClient      -findEvent <eventId> \n" +
                "[findResponses]      AppServiceClient      -findResponses <userEmail> <onlyAffirmative> \n");
    }

    public static void validateArgs(String[] args, int expectedArgs, int[] numericArguments) {
        if (expectedArgs != args.length) {
            printUsageAndExit();
        }
        for (int i = 0; i < numericArguments.length; i++) {
            int position = numericArguments[i];
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

}