package bsds.client;

import bsds.utils.Stat;
import bsds.model.Vertical;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Get the total vertical and number of lift rides from the database
 */
public class RecordCollector {

    private int skierID;
    private int dayNum;
    WebTarget webTarget;
    Client client;
    Stat stat;

    public RecordCollector(WebTarget webTarget, int skierID, int dayNum ,Client client, Stat stat) {
        this.webTarget = webTarget;
        this.skierID = skierID;
        this.dayNum = dayNum;
        this.client = client;
        this.stat = stat;
    }

    public Stat getVert() throws ClientErrorException{
        Response response;
        Vertical result = null;
        long startTime = System.currentTimeMillis();
        response = webTarget.request().get();
        result = response.readEntity(Vertical.class);
        System.out.println("INPUT");
        System.out.println("* SkierID: " + skierID);
        System.out.println("* Day Number: "+dayNum);
        System.out.println("OUTPUT");
        System.out.println("* Total Vertical Height Gain: " + result.getTotalVertical());
        System.out.println("* Number of  Lift Rides: " + result.getLiftTimes());
        response.close();

        Calendar calendar = Calendar.getInstance();
        Date timestamp = new Timestamp(calendar.getTime().getTime());
        long timeBucket = timestamp.getTime();
        stat.recordSentRequestNum();
        stat.recordSuccessfulRequestNum(response.getStatus() == 200);
        long latency = System.currentTimeMillis() - startTime;
        stat.recordLatency(latency);
        stat.addLatencyMapping(timeBucket, latency);
        return stat;
    }
}