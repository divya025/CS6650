package bsds.rest;

import bsds.metrics.MetricListener;
import bsds.dao.RecordDB;
import bsds.metrics.Monitor;
import bsds.metrics.MyMetricsEnum;
import bsds.model.RecordData;

import javax.annotation.PostConstruct;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;


/*
* @author divyaagarwal
*
 */

@Path("/")
@Singleton
public class MyServer {

    @Context
    private ServletContext context;

    private RecordDB rfidLiftDataDAO;
    private Monitor monitor;

    @PostConstruct
    public void postConstruct() {
        rfidLiftDataDAO = (RecordDB) context.getAttribute(MetricListener.RFID_LIFT_DATA_DAO);
        monitor = (Monitor) context.getAttribute(MetricListener.METRICS_REPORTER);
    }

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTest() {
        return "Welcome to AWS Supported Ski Resort!";
    }

    @GET
    @Path("myvert")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getStats(@QueryParam("skierId") int skierId,
                             @QueryParam("dayNum") int dayNum) {
        return monitor.reportOperation(MyMetricsEnum.METRICS_ERRORS, MyMetricsEnum.METRICS_LATENCY, () -> {
            try {
                return Response.ok(rfidLiftDataDAO.getSkierStats(skierId, dayNum), MediaType.APPLICATION_JSON_TYPE)
                        .build();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, () -> Response.serverError().build());
    }

    @POST
    @Path("load")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response loadLiftRecord(String rfidLiftDataJson) {
        return monitor.reportOperation(MyMetricsEnum.RECORD_ERRORS, MyMetricsEnum.RECORD_LATENCY, () -> {
            RecordData recordData = RecordData.fromJson(rfidLiftDataJson);
            try {
                rfidLiftDataDAO.postData(recordData);
                return Response.ok().build();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, () -> Response.serverError().build());
    }



}
