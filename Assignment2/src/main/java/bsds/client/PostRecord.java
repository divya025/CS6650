package bsds.client;

import bsds.utils.Stat;
import bsds.model.Record;

import java.util.Calendar;
import java.util.Date;
import java.sql.Timestamp;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;


/**
 * Connection to the Database to post the data.
 */
public class PostRecord {

    WebTarget webTarget;
    Stat stat;

    PostRecord(WebTarget webTarget,Stat stat){
        this.webTarget = webTarget;
        this.stat = stat;
    }

    public Stat doPost(Record data) throws ClientErrorException {
        Response response;
        long startTime = System.currentTimeMillis();
        response = webTarget.request().post(Entity.json(data));
        //response = webTarget.request().post(Entity.entity(data, MediaType.APPLICATION_JSON), Response.class);
        System.out.println(response.readEntity(String.class));
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
