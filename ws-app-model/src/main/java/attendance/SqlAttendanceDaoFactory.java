package attendance;

import es.udc.ws.util.configuration.ConfigurationParametersManager;
import event.SqlEventDao;

public class SqlAttendanceDaoFactory {

    private final static String CLASS_NAME_PARAMETER = "SqlAttendanceDaoFactory.className";
    private static SqlAttendanceDao dao = null;

    private SqlAttendanceDaoFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SqlAttendanceDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlAttendanceDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static SqlAttendanceDao getDao() {

        if (dao == null) {
            dao = getInstance();
        }
        return dao;

    }
}
