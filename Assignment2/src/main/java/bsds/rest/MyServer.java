package bsds.rest;

import bsds.model.VertCalc;
import bsds.dao.MyRecord;
import bsds.model.Vertical;
import bsds.model.Record;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;

@Path("/")
public class MyServer {

    VertCalc vertCalc = new VertCalc();


    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public String getTest() {
        return "Welcome to AWS Supported Ski Resort!";
    }

    @POST
    @Path("load")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postRecord(Record record) {
        try {
            record = MyRecord.getMyRecord().postRecord(new Record(record.getSkierID(),
                    record.getLiftID(), record.getDayNum()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Load value ---> " + record.toString();
    }

    @GET
    @Path("myvert/{skierID}&{dayNum}")
    @Produces(MediaType.APPLICATION_JSON)
    public Vertical getMessage(
            @PathParam("skierID") int skierID,
            @PathParam("dayNum") int dayNum) {
        return vertCalc.getMyVertical(skierID, dayNum);
    }
}

