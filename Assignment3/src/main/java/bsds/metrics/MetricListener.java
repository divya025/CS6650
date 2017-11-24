package bsds.metrics;

import bsds.dao.RecordDB;
import bsds.db.ConnectionManager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class MetricListener implements ServletContextListener {
    public static final String RFID_LIFT_DATA_DAO = "RFIDLiftDataDao";
    public static final String METRICS_REPORTER = "Monitor";

    public void contextInitialized(ServletContextEvent sce) {
        ConnectionManager connectionManager = new ConnectionManager();
        Monitor monitor = new Monitor();
        ServletContext context = sce.getServletContext();
        context.setAttribute(RFID_LIFT_DATA_DAO, new RecordDB(connectionManager, monitor));
        context.setAttribute(METRICS_REPORTER, monitor);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(RFID_LIFT_DATA_DAO);
        context.removeAttribute(METRICS_REPORTER);
    }
}