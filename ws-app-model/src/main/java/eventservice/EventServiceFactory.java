package eventservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

import java.lang.reflect.InvocationTargetException;

public class EventServiceFactory {
    private final static String CLASS_NAME_PARAMETER = "EventServiceFactory.className";
    private static EventService service = null;
    private EventServiceFactory(){
    }

    private static EventService getInstance(){
        try {
            String serviceClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class serviceClass = Class.forName(serviceClassName);
            return (EventService) serviceClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InvocationTargetException | InstantiationException | IllegalAccessException |
                 NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized static EventService getService(){
        if (service == null){
            service = getInstance();
        }
        return service;
    }
}
