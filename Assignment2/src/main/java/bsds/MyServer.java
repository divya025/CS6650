package bsds;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class MyServer {

    int count = 0;

    @POST
    @Path("load/{resortID}&{dayNum}&{timestamp}&{skierID}&{liftID}")
    @Consumes(MediaType.TEXT_PLAIN)
    public String postText(
            @PathParam("resortID") String resortID,
            @PathParam("dayNum") String dayNum,
            @PathParam("skierID") String skierID,
            @PathParam("liftID") String liftID,
            @PathParam("timestamp") String timestamp) {



        if(resortID!=null && dayNum!=null && skierID!=null && liftID!=null && timestamp!=null)
            count++;
        return (resortID + dayNum + timestamp + skierID + liftID + "0000" + count);
    }

    @GET
    @Path("{myvert}/{skierID}&{dayNum}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getMessage(
            @PathParam("myvert") String myvert,
            @PathParam("skierID") String skierID,
            @PathParam("dayNum") int dayNum) {

        int liftRides =100;
        int totalVertical =450;
        String s = "Total vertical:"+totalVertical+" Number of lift rides"+liftRides;
        return s;
    }
}

